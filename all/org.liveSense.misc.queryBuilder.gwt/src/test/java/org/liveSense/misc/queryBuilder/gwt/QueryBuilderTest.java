package org.liveSense.misc.queryBuilder.gwt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.liveSense.misc.queryBuilder.gwt.QueryBuilderTestHelper.TestBeanFactory;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.beans.CompositeValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.clauses.LimitClauseValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.clauses.OrderByClauseValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.BetweenCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.DistinctFromCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.EqualCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.GreaterCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.GreaterOrEqualCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.InCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.IsNotNullCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.IsNullCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.LessCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.LessOrEqualCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.LikeCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.NotEqualCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.StartingWithCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.operands.DefaultOperandValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.operands.OperandValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.operands.UpperOperandValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.operators.AndOperatorValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.operators.NotOperatorValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.operators.OperatorValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.operators.OrOperatorValueProxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;

public class QueryBuilderTest extends TestCase {
	@Test
	public void testCopmositeValueProxy() {
		TestBeanFactory testBeanFactory = AutoBeanFactorySource.create(TestBeanFactory.class);		
		QueryBuilderTestHelper.testCopmositeValueProxy(testBeanFactory);
	}
	
	@Test
	public void testOperators() {
		TestBeanFactory testBeanFactory = AutoBeanFactorySource.create(TestBeanFactory.class);		
		QueryBuilderTestHelper.testOperators(testBeanFactory);
	}

}