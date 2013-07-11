package org.livesense.sample.webserviceservlet.aegistestclient;

import org.livesense.sample.webserviceservlet.HelloBean;
import org.livesense.sample.webserviceservlet.ds.AegisDeclaretiveService;
import org.livesense.sample.webserviceservlet.ds.AegisDeclaretiveServicePortType;

public class Client {

	public static void main(String[] args) {
		AegisDeclaretiveService service = new AegisDeclaretiveService();
		AegisDeclaretiveServicePortType client = service.getAegisDeclaretiveServicePort();
		HelloBean resp = client.helloWorld("Test client");
		
		System.out.println("Response: "+resp.getHello().getValue());
	}
	
	
}