package org.liveSense.sample.WebServiceServlet.ds;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

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
	@Property(name = "org.liveSense.service.webservice.path", value = "/declaretivejaxrs"),
	@Property(name = "org.liveSense.service.webservice.type", value = "jaxrs")
})
public class JaxRsDeclaretiveService implements WebServiceMarkerInterface {

	private static Logger log = LoggerFactory.getLogger(JaxRsDeclaretiveService.class);

	@Context private UriInfo uriInfo;
	@Context private HttpServletRequest servletRequest;
	@Context private ServletContext servletContext;
	@Context private HttpHeaders headers;
	
	
	private String getServletInfo() {
		return "Context Path info: "+(servletRequest != null ? servletRequest.getPathInfo(): "NO CONTEXT REQUEST")+" "+
		"Filter therad local request Path info: "+(WebServiceThreadLocalRequestFilter.getThreadLocalRequest() != null ? WebServiceThreadLocalRequestFilter.getThreadLocalRequest().getPathInfo() : "NO THREADLOCAL REQUEST")+" "+
		"Filter thread Local request Server Address info: "+(WebServiceThreadLocalRequestFilter.getWebServiceServer() != null ? WebServiceThreadLocalRequestFilter.getWebServiceServer().getDestination().getAddress().getAddress().getValue() : "NO CXF SERVER");
	}

	// PUBLIC METHODS
	// Test with: curl http://localhost:8080/webservices/declaretivejaxrs/hello/tes
	//			  curl http://localhost:8080/webservices/declaretivejaxrs/hello/test.xml
	//			  curl http://localhost:8080/webservices/declaretivejaxrs/hello/test.json
	@GET
	@Path("/hello/{name}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN})
	public HelloBean helloWorld(
		@PathParam("name") String name) {
			HelloBean bean = new HelloBean();
			bean.setHello("Hello "+name+" from the declaretive JaxRs servlet "+getServletInfo());
			return bean;
	}
	
	
	
	// Test with:  curl -X POST -d "test=test" http://localhost:8080/webservices/declaretivejaxrs/testform 
	//             curl -d POST -d "test=test" http://localhost:8080/webservices/declaretivejaxrs/testform 
	@POST
	@Path("/testform")
	@Consumes(value={MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED})
	@Produces(value={MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public String registerUser(
		@FormParam("test") String test) {
		return "OK - "+test+" "+uriInfo.getPath()+" "+getServletInfo();
	}
}
