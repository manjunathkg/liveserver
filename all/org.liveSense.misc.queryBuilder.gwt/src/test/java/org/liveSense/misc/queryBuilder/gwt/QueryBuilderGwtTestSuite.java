package org.liveSense.misc.queryBuilder.gwt;

import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class QueryBuilderGwtTestSuite  extends GWTTestSuite {
	  public static Test suite() {
		    TestSuite suite = new TestSuite("QueryBuilder GWT test suite");
		    suite.addTestSuite(QueryBuilderGwtTest.class); 
		    return suite;
		  }
}


