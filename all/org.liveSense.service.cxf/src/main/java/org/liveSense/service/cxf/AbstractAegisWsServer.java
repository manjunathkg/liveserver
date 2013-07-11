package org.liveSense.service.cxf;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.cxf.BusFactory;
import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.felix.scr.annotations.Component;

@Deprecated

/**
 * Abstract servlet-based implementation for CXF-based SOAP services. Ensures
 * that correct class loader is used is during initialization and invoking
 * phases. Via getCurrentRequest() and getCurrentResponse() it is possible to
 * access these objects from SOAP method implementations.
 * @Deprecated
 * Use @WebServiceMarkerInterface and declaretive service instead. 
 * Example: https://github.com/liveSense/org.liveSense.sample.webServiceServlet/tree/master/src/main/java/org/liveSense/sample/WebServiceServlet/ds
 */
@Component(componentAbstract=true)
public abstract class AbstractAegisWsServer extends AbstractWsServer {
	private static final long serialVersionUID = 1L;


	@Override
	public void init(ServletConfig pServletConfig) throws ServletException {
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			// set classloader to CXF bundle class loader to avoid OSGI classloader problems
			Thread.currentThread().setContextClassLoader(BusFactory.class.getClassLoader());

			super.init(pServletConfig);

			ServerFactoryBean sf = new ServerFactoryBean();
			sf.setBus(getBus());
			sf.setAddress(getServletUrl() == null ? SoapRequestWrapper.VIRTUAL_PATH : getServletUrl());
			sf.setServiceClass(getServerInterfaceType());
			sf.getServiceFactory().setDataBinding(new AegisDatabinding());
			sf.setServiceBean(this);
			sf.create();

		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
	}

	@Override
	public void destroy() {
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			super.destroy();
		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
	}

}
