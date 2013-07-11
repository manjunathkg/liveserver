package org.liveSense.misc.queryBuilder.gwt;

import org.junit.Test;
import org.liveSense.misc.queryBuilder.gwt.QueryBuilderTestHelper.TestBeanFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class QueryBuilderGwtTest extends GWTTestCase {

	static TestBeanFactory testBeanFactory;

	@Override
	public String getModuleName() {
		return "org.liveSense.misc.queryBuilder.gwtTest";
	}
	
	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();
		testBeanFactory = GWT.create(TestBeanFactory.class);		
	}
		
	@Test
	public void testCopmositeValueProxy() {
		QueryBuilderTestHelper.testCopmositeValueProxy(testBeanFactory);
	}
	
	@Test
	public void testOperators() {
		QueryBuilderTestHelper.testOperators(testBeanFactory);
	}

}
