package org.liveSense.sample.WebServiceServlet.abstractwsservlet;

import javax.jws.WebService;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.liveSense.sample.WebServiceServlet.HelloBean;
import org.liveSense.sample.WebServiceServlet.JaxWsServiceInterface;
import org.liveSense.service.cxf.AbstractJaxWsServer;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(metatype = false, inherit = true, immediate = true)
@Service(value = javax.servlet.Servlet.class)

@Properties(value = { 
	@Property(name = "sling.servlet.paths", value = "/webservices/jaxwsservlet"),
	@Property(name = "sling.servlet.methods", value = { "GET", "POST" }) })
@SuppressWarnings({ "serial", "restriction" })
@WebService
public class JaxWsServlet extends AbstractJaxWsServer implements JaxWsServiceInterface {
	private final static String NAME_SPACE = "http://webserviceservlet.sample.liveSense.org";
	private final static String PACKAGE = "org.liveSense.sample.webserviceservlet";

	private static Logger log = LoggerFactory.getLogger(JaxWsServlet.class);

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
		setBundleContext(context.getBundleContext());
		setServletUrl(PropertiesUtil.toString(context.getProperties().get("sling.servlet.paths"), "/webservices/jaxwsservlet"));
	}
	
	/* Authentication and other code before the method called put here */
	@Override
	public void callInit()
		throws Throwable {
		log.info("CallInit called");
	}

	/* The code after the method called put here */
	@Override
	public void callFinal() {
		log.info("CallFinal called");		
	}


	@SuppressWarnings("rawtypes")
	@Override
	protected Class getServerInterfaceType() {
		return JaxWsServiceInterface.class;
	}


	// PUBLIC METHODS
	@Override
	public HelloBean helloWorld(
		String name)
		throws Exception {
			HelloBean bean = new HelloBean();
			bean.setHello("Hello "+name+" from the JaxWs servlet");
			return bean;
	}
}
