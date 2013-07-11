package org.liveSense.sample.WebServiceServlet.dosgiwithsecurity;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.jws.WebService;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.auth.core.AuthenticationSupport;
import org.liveSense.sample.WebServiceServlet.HelloBean;
import org.liveSense.sample.WebServiceServlet.JaxWsServiceInterface;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component(metatype = false, inherit = true, immediate = true)
//@Service(value = JaxWsServiceInterface.class)
@Properties(value = { 
		@Property(name = "org.apache.cxf.ws.httpservice.context", value = "/webservices/jaxwsservletoverdosgiwithsecurity"),
//		@Property(name = "org.apache.cxf.ws.databinding", value = "jaxb"),
//		@Property(name = "org.apache.cxf.ws.frontend", value = "jaxws"),
		@Property(name = "service.exported.interfaces", value = "*")
//		@Property(name = "service.exported.configs", value="org.apache.cxf.ws")
//		@Property(name = "org.apache.cxf.ws.address", value="http://localhost:8181/webservices/jaxwsservletoverdosgiwithsecurity")
})
@SuppressWarnings({ "serial", "restriction" })

@WebService
public class JaxWsServletOverDOSGIWithSecurity implements JaxWsServiceInterface {

	static Logger log = LoggerFactory.getLogger(JaxWsServletOverDOSGIWithSecurity.class);

	@Reference(bind="bindAuth", unbind="unbindAuth")
	AuthenticationSupport auth = null;
	protected void bindAuth(AuthenticationSupport auth) {
		this.auth = auth;
		if (filter != null) {
			filter.setAuthenticationSupport(auth);
		}
	}

	protected void unbindAuth(AuthenticationSupport auth) {
		this.auth = null;
	}

	/**
	 * This is the OSGi component/service activation method. It initializes this
	 * service.
	 * 
	 * @param context
	 *            The OSGi context provided by the activator.
	 */
	ServiceRegistration filterRegistration;
	SampleSlingSecurityFilter filter = null;

	@Activate
	protected void activate(
			ComponentContext context) {
		// Register a servlet filter for this servlet only
		@SuppressWarnings("rawtypes")
		Dictionary<String, Comparable> filterProps = new Hashtable<String, Comparable>();
		filterProps.put("org.apache.cxf.httpservice.filter", Boolean.TRUE);

		log.info("Registering SecurityFilter");
		// Pax-Web whiteboard (if deployed) will attempt to apply this filter to servlets by name or URL, and will complain
		// if neither servletName or urlPatterns are specified.  The felix http service whiteboard may do something similar.
//		filterProps.put("urlPatterns", PropertiesUtil.toString(context.getProperties().get("org.apache.cxf.ws.httpservice.context"), "")+"/*");
//		filter = new SampleSlingSecurityFilter();
//		filter.setAuthenticationSupport(auth);
//		filterRegistration = context.getBundleContext().registerService(Filter.class.getName(), filter, filterProps);
	}

	@Deactivate
	protected void deactivate(
			ComponentContext context) {
//		if (filterRegistration != null) {
//			context.getBundleContext().ungetService(filterRegistration.getReference());
//			log.info("UnRegistering SecurityFilter");
//		}
	}	
	// PUBLIC METHODS
	public HelloBean helloWorld(
			String name)
					throws Exception {
		HelloBean bean = new HelloBean();
		bean.setHello("Hello "+name+" from the DOSGI JaxWs with Security servlet");
		return bean;
	}

}
