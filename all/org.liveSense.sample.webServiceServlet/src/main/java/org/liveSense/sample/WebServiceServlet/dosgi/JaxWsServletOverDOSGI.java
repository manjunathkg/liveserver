package org.liveSense.sample.WebServiceServlet.dosgi;

import javax.jws.WebService;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.liveSense.sample.WebServiceServlet.HelloBean;
import org.liveSense.sample.WebServiceServlet.JaxWsServiceInterface;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IT IS NOT ACTIVATING, THE DOSGI COULD NOT LOAD BECAUSE DUPLICATING INTERFACES, SO IT IS DISABLED
 *
 */

//@Component(metatype = false, inherit = true, immediate = true)
//@Service(value = JaxWsServiceInterface.class)
@Properties(value = { 
		@Property(name = "org.apache.cxf.ws.httpservice.context", value = "/webservices/jaxwsservletoverdosgi"),
//		@Property(name = "org.apache.cxf.ws.databinding", value = "jaxb"),
//		@Property(name = "org.apache.cxf.ws.frontend", value = "jaxws"),
		@Property(name = "service.exported.interfaces", value = "*")
//		@Property(name = "service.exported.configs", value="org.apache.cxf.ws"),
//		@Property(name = "org.apache.cxf.ws.address", value="http://localhost:8181/webservices/jaxwsservletoverdosgi")
})
@SuppressWarnings({ "serial", "restriction" })

@WebService
public class JaxWsServletOverDOSGI implements JaxWsServiceInterface {
	private final static String NAME_SPACE = "http://webserviceservlet.sample.liveSense.org";
	private final static String PACKAGE = "org.liveSense.sample.webserviceservlet";

	private static Logger log = LoggerFactory.getLogger(JaxWsServletOverDOSGI.class);

	/**
	 * This is the OSGi component/service activation method. It initializes this
	 * service.
	 * 
	 * @param context
	 *            The OSGi context provided by the activator.
	 */

	@Activate
	protected void activate(
			ComponentContext context) {
	}


	// PUBLIC METHODS
	@Override
	public HelloBean helloWorld(
			String name)
					throws Exception {
		HelloBean bean = new HelloBean();
		bean.setHello("Hello "+name+" from the DOSGI JaxWs servlet");
		return bean;
	}
}
