package org.livesense.sample.webserviceservlet.jaxwstestclient;

import org.livesense.sample.webserviceservlet.ds.HelloBean;
import org.livesense.sample.webserviceservlet.ds.JaxWsDeclaretiveService;
import org.livesense.sample.webserviceservlet.ds.JaxWsDeclaretiveServiceService;
import org.livesense.sample.webserviceservlet.implemented.jaxwsdeclaretiveservice.HelloWorldRequest;

public class Client {

	public static void main(String[] args) {
		JaxWsDeclaretiveServiceService service = new JaxWsDeclaretiveServiceService();
		JaxWsDeclaretiveService client = service.getJaxWsDeclaretiveServicePort();
		
		HelloWorldRequest req = new HelloWorldRequest();
		req.setName("Test client");
		HelloBean resp = client.helloWorld(req).getReturn();
		
		System.out.println("Response: "+resp.getHello());
	}
	
	
}