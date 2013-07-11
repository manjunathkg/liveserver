package org.liveSense.service.gwt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.liveSense.core.ClassInstanceCache;

import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;
import com.google.web.bindery.requestfactory.shared.Locator;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceLocator;

public class OsgiServiceLayerDecorator extends ServiceLayerDecorator {

	ClassLoader classLoader = null;
	ServiceLocator serviceLocator = null;
	ClassInstanceCache instanceCache = null;

	public OsgiServiceLayerDecorator(ClassLoader classLoader, ServiceLocator serviceLocator, ClassInstanceCache instanceCache) {
		this.classLoader = classLoader;
		this.serviceLocator = serviceLocator;
		this.instanceCache = instanceCache;
	}

	@Override
	public ClassLoader getDomainClassLoader() {
		return classLoader;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public Object invoke(Method domainMethod, Object... args) {
		Throwable ex;
		try {
			domainMethod.setAccessible(true);
			if (Modifier.isStatic(domainMethod.getModifiers())) {
				return domainMethod.invoke(null, args);
			} else {
				Object[] realArgs = new Object[args.length - 1];
				System.arraycopy(args, 1, realArgs, 0, realArgs.length);
				return domainMethod.invoke(args[0], realArgs);
			}
		} catch (IllegalArgumentException e) {
			ex = e;
		} catch (IllegalAccessException e) {
			ex = e;
		} catch (InvocationTargetException e) {
			//return report(e);
			ex = e.getTargetException();
		}
		return die(ex, "Could not invoke method %s", args[0].getClass().getName()+"."+domainMethod.getName());
	}


	@Override
	public <T> Set<ConstraintViolation<T>> validate(T domainObject) {
		// TODO: JSR 303 Validation
		return super.validate(domainObject);
	}

	@Override
	public Object createServiceInstance(Class<? extends RequestContext> requestContext) {
		//		We are in OSGi context we use OSGiServiceLocator
		//	    Class<? extends ServiceLocator> locatorType = getTop().resolveServiceLocator(requestContext);
		//	    ServiceLocator locator = getTop().createServiceLocator(locatorType);
		Class<?> serviceClass = getTop().resolveServiceClass(requestContext);
		//	    return locator.getInstance(serviceClass);
		return serviceLocator.getInstance(serviceClass);
	}

	@Override
	public <T extends Locator<?, ?>> T createLocator(Class<T> clazz) {
		// Searching for locator and setting 
		super.createLocator(clazz);
		return (T) instanceCache.getInstance(clazz);
	}

}
