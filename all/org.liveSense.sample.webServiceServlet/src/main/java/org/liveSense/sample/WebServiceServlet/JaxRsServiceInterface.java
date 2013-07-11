package org.liveSense.sample.WebServiceServlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;




/**
 * functions 
 *
 */
@SuppressWarnings("restriction")
public interface JaxRsServiceInterface {
	// JAX RS Annnotations
	@GET
	@Path("/hello/{name}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN})
	public HelloBean helloWorld(@PathParam("name") String name)
		throws Exception;


}
