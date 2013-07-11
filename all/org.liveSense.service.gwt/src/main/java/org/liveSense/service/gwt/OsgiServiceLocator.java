package org.liveSense.service.gwt;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.liveSense.core.ClassInstanceCache;
import org.liveSense.core.ClassInstanceCacheImpl;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.web.bindery.requestfactory.shared.ServiceLocator;

@Component(immediate=true)
@Service(ServiceLocator.class)
public class OsgiServiceLocator implements ServiceLocator {

	static Logger log = LoggerFactory.getLogger(OsgiServiceLocator.class);
		
	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	ClassInstanceCache instanceCache = ClassInstanceCacheImpl.INSTANCE;
	
	public static OsgiServiceLocator INSTANCE = new OsgiServiceLocator();
	
	
	@Activate
	protected void activate(BundleContext context) {
		INSTANCE = this;
	}
	
	@Deactivate
	protected void deactivate(BundleContext context) {
		INSTANCE = null;
	}
	
	public Object getInstance(String className) {
		return instanceCache.getInstance(className);
	}

	@Override
	public Object getInstance(Class<?> clazz) {
		if (clazz != null) {
			return getInstance(clazz.getName());
		}
		return null;
	}
}
