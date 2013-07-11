package org.liveSense.sample.WebServiceServlet.ds;


import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogInterceptor extends AbstractPhaseInterceptor<Message> {
	Logger log = LoggerFactory.getLogger(TestLogInterceptor.class);

	String phase = "";
	
	public TestLogInterceptor(String phase) {
		super(Phase.PRE_INVOKE);
		this.phase = phase;
	}

	public TestLogInterceptor() {
		super(Phase.PRE_INVOKE);
		this.phase = Phase.PRE_INVOKE;
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		log.info("TEST Phase: "+this.phase+" INTERCEPTOR MESSAGE: "+message.get("HTTP.REQUEST")+" Thread: "+Thread.currentThread().toString());
	}

}
