//$URL: http://feanor:8050/svn/test/trunk/DevTest/apache-sling/adaptto/sling-cxf-integration/helloworld-application/src/main/java/adaptto/slingcxf/server/util/AbstractJaxWsServer.java $
//$Id: AbstractJaxWsServer.java 680 2011-09-12 16:57:25Z PRO-VISION\SSeifert $
package org.liveSense.service.cxf;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.BusFactory;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.apache.felix.scr.annotations.Component;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated

/**
 * Abstract servlet-based implementation for CXF-based SOAP services. Ensures
 * that correct class loader is used is during initialization and invoking
 * phases. Via getCurrentRequest() and getCurrentResponse() it is possible to
 * access these objects from SOAP method implementations.
 * @Deprecated
 * Use @WebServiceMarkerInterface and declaretive service instead. 
 * Example: https://github.com/liveSense/org.liveSense.sample.webServiceServlet/tree/master/src/main/java/org/liveSense/sample/WebServiceServlet/ds
 */
@Component(componentAbstract=true)
public abstract class AbstractJaxRsServer extends AbstractWsServer {
	private static final long serialVersionUID = 1L;

	public static Logger log = LoggerFactory.getLogger(AbstractWsServer.class);
	
	
	@Override
	public void init(ServletConfig pServletConfig) throws ServletException {
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			
			Thread.currentThread().setContextClassLoader(BusFactory.class.getClassLoader());

			super.init(pServletConfig);
			JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
			sf.setBus(getBus());
			
			sf.setAddress(getServletUrl() == null ? SoapRequestWrapper.VIRTUAL_PATH : getServletUrl());
			
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
			
			sf.setResourceClasses(getServerInterfaceType());
			//sf.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
			sf.setResourceProvider(getServerInterfaceType(), new SingletonResourceProvider(this));
			sf.create();
		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
	}

	@Override
	public void destroy() {
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(BusFactory.class.getClassLoader());
		try {
			super.destroy();
		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
	}
}
