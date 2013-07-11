package org.liveSense.sample.WebServiceServlet.abstractwsservlet;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.liveSense.sample.WebServiceServlet.AegisServiceInterface;
import org.liveSense.sample.WebServiceServlet.HelloBean;
import org.liveSense.service.cxf.AbstractAegisWsServer;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = true, inherit = true, immediate = true)
@Service(value = javax.servlet.Servlet.class)
@Properties(value = { 
	@Property(name = "sling.servlet.paths", value = "/webservices/aegisservlet"),
	@Property(name = "sling.servlet.methods", value = { "GET", "POST" }) })
@SuppressWarnings({ "serial" })
public class AegisServlet extends AbstractAegisWsServer implements AegisServiceInterface {

	private static Logger log = LoggerFactory.getLogger(AegisServlet.class);

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
		setServletUrl(PropertiesUtil.toString(context.getProperties().get("sling.servlet.paths"), "/webservices/aegisservlet"));
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
		return AegisServiceInterface.class;
	}


	// PUBLIC METHODS
	@Override
	public HelloBean helloWorld(
		String name)
		throws Exception {
			HelloBean bean = new HelloBean();
			bean.setHello("Hello "+name+" from the Aegis servlet");
			return bean;
	}

}
