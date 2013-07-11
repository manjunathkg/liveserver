package org.liveSense.sample.WebServiceServlet.ds;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;

import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.liveSense.sample.WebServiceServlet.HelloBean;
import org.liveSense.service.cxf.WebServiceMarkerInterface;
import org.liveSense.service.cxf.WebServiceThreadLocalRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(metatype = true, immediate = true)
@Service(value = WebServiceMarkerInterface.class)
@Properties(value = { 
	@Property(name = "org.liveSense.service.webservice.path", value = "/declaretivejaxws"),
	@Property(name = "org.liveSense.service.webservice.type", value = "jaxws")
})
@SuppressWarnings({ "serial" })
@WebService

// We defining a processor in the Input chain. You can define authentication and other preprocessors here
@InInterceptors(classes={TestLogInterceptor.class})
public class JaxWsDeclaretiveService implements WebServiceMarkerInterface {

	private static Logger log = LoggerFactory.getLogger(JaxWsDeclaretiveService.class);
	
	@Resource 
	private WebServiceContext context; 
	
	private HttpServletRequest getRequest() {
		if (context != null && context.getMessageContext() != null)
			return (HttpServletRequest)context.getMessageContext().get(AbstractHTTPDestination.HTTP_REQUEST);
		return null;
	}

	private HttpServletResponse getResponse() {
		if (context != null && context.getMessageContext() != null)
			return (HttpServletResponse)context.getMessageContext().get(AbstractHTTPDestination.HTTP_RESPONSE);
		return null;
	}

	private String getServletInfo() {
		return "Context Path info: "+(getRequest() != null ? getRequest().getPathInfo(): "NO CONTEXT REQUEST")+" "+
		"Filter therad local request Path info: "+(WebServiceThreadLocalRequestFilter.getThreadLocalRequest() != null ? WebServiceThreadLocalRequestFilter.getThreadLocalRequest().getPathInfo() : "NO THREADLOCAL REQUEST")+" "+
		"Filter thread Local request Server Address info: "+(WebServiceThreadLocalRequestFilter.getWebServiceServer() != null ? WebServiceThreadLocalRequestFilter.getWebServiceServer().getDestination().getAddress().getAddress().getValue() : "NO CXF SERVER");
	}

	// PUBLIC METHODS
	private static final String NAME_SPACE = "http://jaxwsdeclaretiveservice.implemented.webserviceservlet.sample.liveSense.org";
	private static final String PACKAGE = "org.liveSense.sample.webserviceservlet.implemented.jaxwsdeclaretiveservice.";

	// Jax WS Annotations
	@WebResult(name = "return", targetNamespace = "")
	@RequestWrapper(localName = "helloWorldRequest", targetNamespace = NAME_SPACE, className = PACKAGE + "helloWorldRequest")
	@ResponseWrapper(localName = "helloWorldResponse", targetNamespace = NAME_SPACE, className = PACKAGE + "helloWorldResponse")
	@WebMethod
	public HelloBean helloWorld(@WebParam(name = "name", targetNamespace = NAME_SPACE) String name) {
			HelloBean bean = new HelloBean();
			bean.setHello("Hello "+name+" from the Declaretive JaxWs servlet"+getServletInfo());
			return bean;
	}
}
