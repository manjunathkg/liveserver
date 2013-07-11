package org.liveSense.sample.WebServiceServlet;

import javax.jws.WebService;

/**
 * functions 
 *
 */
@SuppressWarnings("restriction")
@WebService
public interface JaxWsServiceInterface {
	String NAME_SPACE = "http://webserviceservlet.sample.liveSense.org";
	String PACKAGE = "org.liveSense.sample.webserviceservlet";

	// Jax WS Annotations
//	@WebResult(name = "return", targetNamespace = "")
//	@RequestWrapper(localName = "helloWorldRequest", targetNamespace = NAME_SPACE, className = PACKAGE + "helloWorldRequest")
//	@ResponseWrapper(localName = "helloWorldResponse", targetNamespace = NAME_SPACE, className = PACKAGE + "helloWorldResponse")
//	@WebMethod
//	public HelloBean helloWorld(@WebParam(name = "name", targetNamespace = NAME_SPACE) String name)
//		throws Exception;

	public HelloBean helloWorld(String name)
			throws Exception;

}
