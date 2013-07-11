package org.liveSense.misc.queryBuilder.gwt;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.server.ServiceLayer;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;
import com.google.web.bindery.requestfactory.server.SimpleRequestProcessor;
import com.google.web.bindery.requestfactory.server.testing.InProcessRequestTransport;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.shared.ServiceLocator;
import com.google.web.bindery.requestfactory.vm.RequestFactorySource;


@SuppressWarnings("unchecked")
public class RequestFactoryHelper {
  static final Logger log = LoggerFactory.getLogger(RequestFactoryHelper.class);
  {
	  // Disable service layer cache
	  System.setProperty("gwt.rf.ServiceLayerCache", "false");
  }

	
  private static class MockServiceLocator implements ServiceLocator {
    private final Map<Class<?>, Object> services = new HashMap<Class<?>, Object>();
    public Object getInstance( Class<?> clazz ) {
      // Make sure to return always the same mocked instance for each requested type
      Object result = services.get( clazz );
      if (result == null) {
        result = mock( clazz );
		log.info("MockServiceLocator.getInstance()");
        services.put( clazz, result );
      }
      return result;
    }    
  }

  private static class CachedServiceLocator implements ServiceLocator {
	    private final static Map<Class<?>, Object> services = new HashMap<Class<?>, Object>();
	    
	    public Object getInstance( Class<?> clazz ) {
	      // Make sure to return always the same instance for each requested type
	      Object result = services.get( clazz );
	      if (result == null) {
	        try {
				log.info("CachedServiceLocator.getInstance()");
	        		result = clazz.newInstance();
			} catch (InstantiationException e) {
				log.error("CachedServiceLocator.getInstance()", e);
			} catch (IllegalAccessException e) {
				log.error("CachedServiceLocator.getInstance()", e);
			}
	        services.put( clazz, result );
	      }
	      return result;
	    }
	    
	    public static Object getService(Class<?> clazz) {
	    		return services.get(clazz);
	    }
  }

  
  private static class MockServiceDecorator extends ServiceLayerDecorator {

    @Override
    public <T extends ServiceLocator> T createServiceLocator( Class<T> clazz ) {
      return (T) mockServiceLocator;
    }
  }
  private static class CachedServiceDecorator extends ServiceLayerDecorator {
	    @Override
	    public <T extends ServiceLocator> T createServiceLocator( Class<T> clazz ) {
	      return (T) cachedServiceLocator;
	    }
  }

  
  private static MockServiceLocator mockServiceLocator = new MockServiceLocator();
  private static ServiceLayer mockServiceLayer = ServiceLayer.create( new MockServiceDecorator() );

  private static CachedServiceLocator cachedServiceLocator = new CachedServiceLocator();
  private static ServiceLayer cachedServiceLayer = ServiceLayer.create( new CachedServiceDecorator() );

  /**
   * Creates a {@link RequestFactory}.
   */
  public static <T extends RequestFactory> T createMock(Class<T> requestFactoryClass ) {
    SimpleRequestProcessor processor = new SimpleRequestProcessor( mockServiceLayer );
    T factory = RequestFactorySource.create( requestFactoryClass );
    factory.initialize( new SimpleEventBus(), new InProcessRequestTransport( processor ) );
    return factory;
  }

  /**
   * Creates a {@link RequestFactory}.
   */
  public static <T extends RequestFactory> T createCached(Class<T> requestFactoryClass ) {
    SimpleRequestProcessor processor = new SimpleRequestProcessor( cachedServiceLayer );
    T factory = RequestFactorySource.create( requestFactoryClass );
    factory.initialize( new SimpleEventBus(), new InProcessRequestTransport( processor ) );
    return factory;
  }

  /**
   * Returns the same service instance as used by the RequestFactory internals.
   */
  public static <T> T getMockService( Class<T> serviceClass ) {
    T result = (T) mockServiceLocator.getInstance( serviceClass );
    reset( result ); // reset mock to avoid side effects when used in multiple tests
    return result;
  }

  /**
   * Returns the same service instance as used by the RequestFactory internals.
   */
  public static <T> T getCachedService( Class<T> serviceClass ) {
    T result = (T) cachedServiceLocator.getInstance( serviceClass );
    return result;
  }

  
  /**
   * Returns the value passed to {@link Receiver#onSuccess(Object)}
   */
  public static <T> T captureResult( Receiver<T> receiver ) {
    ArgumentCaptor<Object> captor = ArgumentCaptor.forClass( Object.class );
    verify( receiver ).onSuccess( (T) captor.capture() );
    return (T) captor.getValue();
  }
  
}
