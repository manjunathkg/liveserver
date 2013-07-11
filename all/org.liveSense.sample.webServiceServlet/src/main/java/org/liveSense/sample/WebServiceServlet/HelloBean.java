package org.liveSense.sample.WebServiceServlet;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HelloBean {
	
	private String hello;

	public String getHello() {
		return hello;
	}

	public void setHello(String hello) {
		this.hello = hello;
	}
	
	

}
