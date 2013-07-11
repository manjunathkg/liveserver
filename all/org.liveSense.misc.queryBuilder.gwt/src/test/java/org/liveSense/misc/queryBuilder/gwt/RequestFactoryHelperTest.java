package org.liveSense.misc.queryBuilder.gwt;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.shared.Service;

public class RequestFactoryHelperTest {

 @Service(value = TestService.class, locator = DummyServiceLocator.class) 
  interface TestRequestContext extends RequestContext {
	    Request<Void> testMethod();
  }

 interface TestRequestFactory extends RequestFactory {
	    TestRequestContext context();
 }
 
 interface TestService {
    void testMethod();
  }
  
  
  @Test
  public void testServiceIsSame() {
    TestService service1 = RequestFactoryHelper.getMockService( TestService.class );
    TestService service2 = RequestFactoryHelper.getMockService( TestService.class );
    
    assertSame( service1, service2 );
  }
  
  @Test
  public void testMockIsInvoked() {
    TestService service = RequestFactoryHelper.getMockService( TestService.class );
    TestRequestFactory factory = RequestFactoryHelper.createMock( TestRequestFactory.class );
    
    factory.context().testMethod().fire();
    
    verify( service ).testMethod();
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testCaptureResult() {
    Receiver<Object> receiver = mock( Receiver.class );
    Object expected = new Object();
    receiver.onSuccess( expected );
    
    Object returned = RequestFactoryHelper.captureResult( receiver );
    
    assertSame( expected, returned );
  }
  
}
