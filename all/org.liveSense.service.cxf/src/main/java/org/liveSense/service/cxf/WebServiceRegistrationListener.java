package org.liveSense.service.cxf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.BusFactory;
import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.cxf.common.classloader.ClassLoaderUtils.ClassLoaderHolder;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.cxf.transport.http.DestinationRegistry;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.sling.auth.core.AuthenticationSupport;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.liveSense.core.service.OSGIClassLoaderManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(immediate=true, metatype=true)
@Properties(value={
		@Property(name=WebServiceRegistrationListener.PROP_SERVICE_ROOT_URL, value=WebServiceRegistrationListener.DEFAULT_SERVICE_ROOT_URL)
})

public class WebServiceRegistrationListener implements ServiceListener {
	private static final long serialVersionUID = 1L;

	public static final String WS_INTERFACE = "org.liveSense.ws.interface";
	public static final String WS_PATH = "org.liveSense.service.webservice.path";
	public static final String WS_TYPE = "org.liveSense.service.webservice.type";


	public static final String PROP_SERVICE_ROOT_URL = "root";
	public static final String DEFAULT_SERVICE_ROOT_URL = "/webservices";

	private String serviceRootUrl = PROP_SERVICE_ROOT_URL;


	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	private HttpService osgiHttpService;

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	private AuthenticationSupport slingAuthenticator;

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_UNARY)
	private MimeTypeService mimeTypeService;

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	private OSGIClassLoaderManager osgiClassLoaderManager;

	private Bus bus;
	private BundleContext context;
	private DestinationRegistry destinationRegistry;
	private WebServiceThreadLocalRequestFilter filter;
	private ServiceRegistration filterRegistration;
	private ServiceTracker httpServiceTracker;

	Map<String, HttpServlet> registeredServlets = new ConcurrentHashMap<String, HttpServlet>();
	Map<String, Server> registeredServices = new ConcurrentHashMap<String, Server>();

	private final Logger log = LoggerFactory.getLogger(WebServiceRegistrationListener.class);

	@Activate
	protected void activate(ComponentContext context) {

		log.info("Activating WebServiceRegistrationListener");
		this.context = context.getBundleContext();

		// Setting up properties
		serviceRootUrl = PropertiesUtil.toString(context.getProperties().get(PROP_SERVICE_ROOT_URL), DEFAULT_SERVICE_ROOT_URL);

		try {

			bus = BusFactory.newInstance().createBus();
			//bus.setExtension(pckClassLoader, ClassLoader.class);
			DestinationFactoryManager dfm = bus.getExtension(DestinationFactoryManager.class);
			destinationRegistry = null;
			try {
				DestinationFactory df = dfm
						.getDestinationFactory("http://cxf.apache.org/transports/http/configuration");
				if (df instanceof HTTPTransportFactory) {
					HTTPTransportFactory transportFactory = (HTTPTransportFactory)df;
					destinationRegistry = transportFactory.getRegistry();
				}
			} catch (BusException e) {
				log.warn("DestinationFactoryManager not found", e);
			}

			// TODO: Handling security??
			if (destinationRegistry != null) {
				HttpContext httpContext = new HttpContext() {

					@Override
					public boolean handleSecurity(HttpServletRequest request,
							HttpServletResponse response) throws IOException {
						return true;
					}

					// this context provides no resources, always call the servlet
					@Override
					public URL getResource(String name) {
						return null;
					}

					@Override
					public String getMimeType(String name) {
						MimeTypeService mts = WebServiceRegistrationListener.this.mimeTypeService;
						return (mts != null) ? mts.getMimeType(name) : null;
					}
				};

				final CXFNonSpringServlet cxfServlet = new CXFNonSpringServlet(destinationRegistry, false);
				registeredServlets.put(serviceRootUrl, cxfServlet);

				try {
					log.info("Registering CXF Root servlet: "+serviceRootUrl);
					Dictionary props; 
					props = new Hashtable();
					props.put( "alias", serviceRootUrl );
					
					osgiHttpService.registerServlet(serviceRootUrl, cxfServlet, props, httpContext);
					
					log.info("Registering WebServiceThreadLocalRequestFilter");
					// Registering threadLocalRequest servlet filter also
					Dictionary<String, Comparable> filterProps = new Hashtable<String, Comparable>();
					filterProps.put("pattern", serviceRootUrl+"/.*");
					filterProps.put("alias", serviceRootUrl );

					filterProps.put("init.message", "WebServiceThreadLocalRequestFilter!");
					filterProps.put("service.ranking", "1");
					filter = new WebServiceThreadLocalRequestFilter();
					filter.setWebServiceRegistrationListener(this);

					filterRegistration = context.getBundleContext().registerService(Filter.class.getName(), filter, filterProps);

				} catch (ServletException e) {
					log.error("Error on servlet registration: "+serviceRootUrl, e);
				} catch (NamespaceException e) {
					log.error("Could not register servlet to URL: "+serviceRootUrl+" (Another servlet on URL?)", e);
				}

			}

		} catch (Throwable th) {
			log.error("Error on activation", th);
		} finally {
		}

		// If this started later we search for all references to register
		try {
			ServiceReference[] refs = this.context.getAllServiceReferences(WebServiceMarkerInterface.class.getName(), null);
			if (refs != null) {
				for (ServiceReference ref : refs) {
					registerService(ref);
				}
			}
		} catch (InvalidSyntaxException e) {
			log.error("Cannot get sercive references");
		}

		log.info("Adding OSGi service listener");
		context.getBundleContext().addServiceListener(this);
	}

	@Deactivate
	protected void deactivate(ComponentContext context) {

		log.info("Deactivating WebServiceRegistrationListener");
		// Destroys all services
		for (String path : registeredServices.keySet()) {
			try {
				log.info("Stopping service: "+path);
				if (registeredServices.get(path).isStarted()) 
					registeredServices.get(path).stop();

				log.info("Destroy service: "+path);
				registeredServices.get(path).destroy();
			} catch (Exception e) {
				log.error("Could not destroy WebService: "+path);
			}
		}

		// Remove OSGi service listener
		try {
			log.info("Remove OSGi service listener");
			context.getBundleContext().removeServiceListener(this);
		} catch (Exception e) {
			log.error("Could not get unregister service listener", e);
		}

		// Remove ThreadLocalRequest filter
		if (filterRegistration != null) {
			log.info("Unregistering WebServiceThreadLocalRequestFilter");
			try {
				context.getBundleContext().ungetService(filterRegistration.getReference());
			} catch (Exception e) {
				log.error("Could not unregister WebServiceThreadLocalRequestFilter", e);
			}
		}

		// For safety we remove all HTTPServlet
		for (String serv : registeredServlets.keySet()) {
			try {
				log.info("Unregistering servlet: "+serv);
				osgiHttpService.unregister(serv);
			} catch (Exception e) {
				log.error("Could not remove: "+serv, e);
			}
		}
		registeredServlets.clear();

		if (destinationRegistry != null) {
			for (String path : destinationRegistry.getDestinationsPaths()) {
				// clean up the destination in case the destination itself can 
				// no longer access the registry later
				AbstractHTTPDestination dest = destinationRegistry.getDestinationForPath(path);
				synchronized (dest) {
					destinationRegistry.removeDestination(path);
					dest.releaseRegistry();
				}
			}
			destinationRegistry = null;
		}
		bus = null;
	}

	private void unregisterService(ServiceReference sr) {
		String serviceUrl = (String)sr.getProperty(WS_PATH);
		if (serviceUrl != null && context.getService(sr) instanceof WebServiceMarkerInterface && registeredServices.containsKey(serviceUrl)) {
			log.info("Service unregistration: "+context.getService(sr).getClass().getName());

			Server service = registeredServices.get(serviceUrl);
			registeredServices.remove(serviceUrl);
			try {
				if (service.isStarted())
					service.stop();
				service.destroy();
			} catch (Exception e) {
				log.error("Could not remove service: "+serviceUrl, e);
			}
		}

	}

	private void registerService(ServiceReference sr) {

		String serviceUrl = (String)sr.getProperty(WS_PATH);
		if (serviceUrl != null && context.getService(sr) instanceof WebServiceMarkerInterface && !registeredServlets.containsKey(serviceUrl)) {

			ClassLoaderHolder oldClassLoader = ClassLoaderUtils.setThreadContextClassloader(Bus.class.getClassLoader());
			String type = getServiceType(sr);

			try {
				Class serviceClass = getServiceClass(sr);
				if (serviceClass == null) {
					serviceClass = context.getService(sr).getClass();
				}
				log.info(type+" WebService registration: "+serviceUrl+" Interface: "+serviceClass+" Implementation: "+context.getService(sr).getClass().getName());

				if (type.equalsIgnoreCase("jaxws")) {
					JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
					sf.setBus(bus);
					sf.setAddress(serviceUrl);
					sf.setServiceClass(serviceClass);
					sf.getServiceFactory().setDataBinding(new JAXBDataBinding());
					sf.setServiceBean(context.getService(sr));
					registeredServices.put(serviceUrl, sf.create());
					// Adding ThreadLocalWebServiceContextInterceptor. 
					// It is setting the SoapMessageContext to WebServiceContext from the message
					registeredServices.get(serviceUrl).getEndpoint().getInInterceptors().add(new WebServiceThreadLocalWebServiceContextInterceptor(null));
				} else if (type.equalsIgnoreCase("aegis")) {
					ServerFactoryBean sf = new ServerFactoryBean();
					sf.setBus(bus);
					sf.setAddress(serviceUrl);
					sf.setServiceClass(serviceClass);
					sf.getServiceFactory().setDataBinding(new AegisDatabinding());
					sf.setServiceBean(context.getService(sr));
					registeredServices.put(serviceUrl, sf.create());
					// Adding ThreadLocalWebServiceContextInterceptor. 
					// It is setting the SoapMessageContext to WebServiceContext from the message
					registeredServices.get(serviceUrl).getEndpoint().getInInterceptors().add(new WebServiceThreadLocalWebServiceContextInterceptor(null));
				} else if (type.equalsIgnoreCase("jaxrs")) {
					createJaxRsService(serviceUrl, serviceClass, context.getService(sr));
				}
			} catch (Exception e) {
				log.error("Coluld not register "+type+" service: "+serviceUrl, e);;
			} finally {
				if (oldClassLoader != null) {
					oldClassLoader.reset();
				}
			}
		}
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		ServiceReference sr = event.getServiceReference();
		switch(event.getType()) {
		case ServiceEvent.REGISTERED: {
			registerService(sr);
		}
		break;

		case ServiceEvent.UNREGISTERING: {
			unregisterService(sr);
		}
		break;

		default:
			break;
		}
	}


	private String getServiceType(ServiceReference sr) {
		// If type is set we use it, else we use Aegis dataBinding
		String type = "aegis";
		if (sr.getProperty(WS_TYPE) != null) {
			type = PropertiesUtil.toString(sr.getProperty(WS_TYPE), null);
			if (type == null || (!type.equalsIgnoreCase("jaxrs") && !type.equalsIgnoreCase("jaxws"))) {
				type = "aegis";
			}
		}
		return type;
	}

	private Class getServiceClass(ServiceReference sr) {
		// If Interface set we use the interface for service delegation
		Class clazz = null;
		if (sr.getProperty(WS_INTERFACE) != null) {
			String servinterface = PropertiesUtil.toString(sr.getProperty(WS_INTERFACE), null);
			try {
				clazz = context.getService(sr).getClass().getClassLoader().loadClass(servinterface).getClass();
			} catch (Exception e) {
				log.error("Could not load interface "+servinterface+" for service "+context.getService(sr).getClass().getName()+" "+sr.getBundle().getSymbolicName()+"("+sr.getBundle().getBundleId()+")");
			}
		}
		return clazz;
	}

	private void createJaxRsService(String serviceUrl, Class serviceInterface, Object serviceBean) {

		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		sf.setBus(bus);

		sf.setAddress(serviceUrl);

		Map<Object, Object> extensionMappings = new HashMap<Object, Object>();
		extensionMappings.put("xml", "application/xml");
		extensionMappings.put("json", "application/json");


		// TODO: HTML extension is not workong now it requires XSLTJaxbProvider.
		// But we have to able to define the XSLT file for the proveder, we need
		// to extend the loader service for it to be flexible.  More info: 
		// http://sberyozkin.blogspot.hu/2009/05/mvc-xml-way-with-cxf-jax-rs.html
		// http://cxf.apache.org/docs/jax-rs-advanced-xml.html

		// extensionMappings.put("html", "text/html");
		// It can be configured with this way:
		//  <map id="outTemplates">
		//      <entry key="application/xml" value="classpath:/WEB-INF/templates/book-xml.xsl"/>
		//      <entry key="text/html" value="classpath:/WEB-INF/templates/book-html.xsl"/>
		//      <entry key="application/json" value="classpath:/WEB-INF/templates/book-json.xsl"/>
		//  </map>
		//	  
		//	  <bean id="uriResolver" class="org.apache.cxf.systest.jaxrs.URIResolverImpl"/>
		//	  
		//	  <bean id="xsltProvider" class="org.apache.cxf.jaxrs.provider.XSLTJaxbProvider">    
		//	      <property name="outMediaTemplates" ref="outTemplates"/>
		//	      <property name="resolver" ref="uriResolver"/>
		//  </bean>

		sf.setExtensionMappings(extensionMappings);

		List<Object> providers = new ArrayList<Object>();
		providers.add(new JAXBElementProvider());

		// Jackson JSON Provider
		JacksonJaxbJsonProvider jp = new JacksonJaxbJsonProvider();

		// This is hack, because the interface does not work in first time, so we emulate it
		// http://stackoverflow.com/questions/10860142/appengine-java-jersey-jackson-jaxbannotationintrospector-noclassdeffounderror
		// But that solution is not correct fpr this problem, because xc cause other problem (reason: JAXB annotations)
		try {
			jp.writeTo(new Long(1), Long.class, Long.class, new Annotation[]{}, MediaType.APPLICATION_JSON_TYPE, null, new ByteArrayOutputStream());
		} catch (Throwable e) {
		}
		providers.add(jp);
		
		
		sf.setProviders(providers);

		BindingFactoryManager manager = sf.getBus().getExtension(BindingFactoryManager.class);
		JAXRSBindingFactory factory = new JAXRSBindingFactory();
		factory.setBus(sf.getBus());
		manager.registerBindingFactory(JAXRSBindingFactory.JAXRS_BINDING_ID, factory);

		sf.setResourceClasses(serviceInterface == null ? serviceBean.getClass() : serviceInterface);
		sf.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
		sf.setResourceProvider(serviceInterface == null ? serviceBean.getClass() : serviceInterface, new SingletonResourceProvider(serviceBean));
		registeredServices.put(serviceUrl, sf.create());

	}

	public Server getServiceByPath(String path) {
		return registeredServices.get(path);
	}

	public Server getServiceByRequest(HttpServletRequest request) {
		String pathInfo = request.getPathInfo() == null ? request.getServletPath() : request.getPathInfo();
		
		if (pathInfo.startsWith(serviceRootUrl))
			pathInfo = pathInfo.substring(serviceRootUrl.length());
		
		for (String path : registeredServices.keySet()) {
	 
			AbstractHTTPDestination d = destinationRegistry.getDestinationForPath(pathInfo, true);
			if (d == null) {
				d = destinationRegistry.checkRestfulRequest(pathInfo);
			}
			
			if (d!=null) {
				EndpointInfo ei = d.getEndpointInfo();			
				if (ei.getAddress().equals(registeredServices.get(path).getEndpoint().getEndpointInfo().getAddress())) {
					return registeredServices.get(path);
				}
			}
		}
		return null;
	}
}
