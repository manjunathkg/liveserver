package org.liveSense.misc.queryBuilder.gwt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
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

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.requestfactory.shared.ExtraTypes;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.shared.Service;

public class QueryBuilderTestHelper {

	@Service(value = QueryBuilderService.class, locator = CreateNewInstanceLocator.class)
	@ExtraTypes({

			// CriteriaValueProxy subtypes
			BetweenCriteriaValueProxy.class, 
			DistinctFromCriteriaValueProxy.class,
			EqualCriteriaValueProxy.class, 
			GreaterCriteriaValueProxy.class, 
			GreaterOrEqualCriteriaValueProxy.class, 
			InCriteriaValueProxy.class, 
			IsNotNullCriteriaValueProxy.class, 
			IsNullCriteriaValueProxy.class, 
			LessCriteriaValueProxy.class,
			LessOrEqualCriteriaValueProxy.class, 
			LikeCriteriaValueProxy.class, 
			NotEqualCriteriaValueProxy.class, 
			StartingWithCriteriaValueProxy.class,

			// OperandValueProxy subtypes
			DefaultOperandValueProxy.class, 
			UpperOperandValueProxy.class,

			// OperatorValueProxy subtypes
			AndOperatorValueProxy.class, 
			NotOperatorValueProxy.class, 
			OrOperatorValueProxy.class

	})
	public interface QueryBuilderRequestContext extends RequestContext {
		Request<List<OperatorValueProxy>> getOperators();
		Request<OperatorValueProxy> getOperator();
		Request<Void> setOperator(OperatorValueProxy operator);
	}

	public interface QueryBuilderRequestFactory extends RequestFactory {
		QueryBuilderRequestContext context();
	}

	// Declare the factory type
	interface TestBeanFactory extends AutoBeanFactory {
		AutoBean<CompositeValueProxy> compositeValueProxy();
		AutoBean<LimitClauseValueProxy> limitClauseValueProxy();
		AutoBean<OrderByClauseValueProxy> orderByClauseValueProxy();
		AutoBean<BetweenCriteriaValueProxy> betweenCriteriaValueProxy();
		AutoBean<DistinctFromCriteriaValueProxy> distinctFromCriteriaValueProxy();
		AutoBean<EqualCriteriaValueProxy> equalCriteriaValueProxy();
		AutoBean<GreaterCriteriaValueProxy> greaterCriteriaValueProxy();
		AutoBean<GreaterOrEqualCriteriaValueProxy> greaterOrEqualCriteriaValueProxy();
		AutoBean<InCriteriaValueProxy> inCriteriaValueProxy();
		AutoBean<IsNotNullCriteriaValueProxy> isNotNullCriteriaValueProxy();
		AutoBean<IsNullCriteriaValueProxy> isNullCriteriaValueProxy();
		AutoBean<LessCriteriaValueProxy> lessCriteriaValueProxy();
		AutoBean<LessOrEqualCriteriaValueProxy> lessOrEqualCriteriaValueProxy();
		AutoBean<LikeCriteriaValueProxy> likeCriteriaValueProxy();
		AutoBean<NotEqualCriteriaValueProxy> notEqualCriteriaValueProxy();
		AutoBean<StartingWithCriteriaValueProxy> startingWithCriteriaValueProxy();
		AutoBean<DefaultOperandValueProxy> defaultOperandValueProxy();
		AutoBean<UpperOperandValueProxy> upperOperandValueProxy();
		AutoBean<AndOperatorValueProxy> andOperatorValueProxy();
		AutoBean<OrOperatorValueProxy> orOperatorValueProxy();
		AutoBean<NotOperatorValueProxy> notOperatorValueProxy();
	}

	public static void testCopmositeValueProxy(TestBeanFactory testBeanFactory) {
		CompositeValueProxy test = testBeanFactory.compositeValueProxy().as();
		AutoBean<CompositeValueProxy> bean;

		// Integer
		test = testBeanFactory.compositeValueProxy().as();
		test.setValueAsInteger(1);
		String payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload();
		Assert.assertEquals("{\"valueAsInteger\":1}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());
		bean = AutoBeanCodex.decode(testBeanFactory, CompositeValueProxy.class, payload);
		Assert.assertEquals((Integer) 1, bean.as().getValueAsInteger());
		test.setValueAsInteger(null);
		Assert.assertEquals("{}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());

		// Long
		test = testBeanFactory.compositeValueProxy().as();
		test.setValueAsLong(new Long(1));
		Assert.assertEquals("{\"valueAsLong\":\"1\"}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());
		payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload();
		bean = AutoBeanCodex.decode(testBeanFactory, CompositeValueProxy.class, payload);
		Assert.assertEquals(new Long(1), bean.as().getValueAsLong());
		test.setValueAsLong(null);
		Assert.assertEquals("{}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());

		// String
		test = testBeanFactory.compositeValueProxy().as();
		test.setValueAsString("test");
		Assert.assertEquals("{\"valueAsString\":\"test\"}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());
		payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload();
		bean = AutoBeanCodex.decode(testBeanFactory, CompositeValueProxy.class, payload);
		Assert.assertEquals("test", bean.as().getValueAsString());
		test.setValueAsString(null);
		Assert.assertEquals("{}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());

		// BigDecimal
		test = testBeanFactory.compositeValueProxy().as();
		test.setValueAsBigDecimal(new BigDecimal(1));
		Assert.assertEquals("{\"valueAsBigDecimal\":\"1\"}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());
		payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload();
		bean = AutoBeanCodex.decode(testBeanFactory, CompositeValueProxy.class, payload);
		Assert.assertEquals(new BigDecimal(1), bean.as().getValueAsBigDecimal());
		test.setValueAsBigDecimal((BigDecimal) null);
		Assert.assertEquals("{}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());

		// BigInteger
		test = testBeanFactory.compositeValueProxy().as();
		test.setValueAsBigInteger(new BigInteger("1"));
		Assert.assertEquals("{\"valueAsBigInteger\":\"1\"}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());
		payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload();
		bean = AutoBeanCodex.decode(testBeanFactory, CompositeValueProxy.class, payload);
		Assert.assertEquals(new BigInteger("1"), bean.as().getValueAsBigInteger());
		test.setValueAsBigInteger((BigInteger) null);
		Assert.assertEquals("{}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());

		// Boolean
		test = testBeanFactory.compositeValueProxy().as();
		test.setValueAsBoolean(new Boolean(true));
		Assert.assertEquals("{\"valueAsBoolean\":true}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());
		payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload();
		bean = AutoBeanCodex.decode(testBeanFactory, CompositeValueProxy.class, payload);
		Assert.assertEquals(new Boolean(true), bean.as().getValueAsBoolean());
		test.setValueAsBoolean((Boolean) null);
		Assert.assertEquals("{}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());

		// Double
		test = testBeanFactory.compositeValueProxy().as();
		test.setValueAsDouble(new Double(1.001));
		Assert.assertEquals("{\"valueAsDouble\":1.001}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());
		payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload();
		bean = AutoBeanCodex.decode(testBeanFactory, CompositeValueProxy.class, payload);
		Assert.assertEquals(new Double(1.001), bean.as().getValueAsDouble());
		test.setValueAsDouble((Double) null);
		Assert.assertEquals("{}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());

		// Float
		test = testBeanFactory.compositeValueProxy().as();
		test.setValueAsFloat(new Float(1.001));
		Assert.assertEquals("{\"valueAsFloat\":1.0010000467300415}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());
		payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload();
		bean = AutoBeanCodex.decode(testBeanFactory, CompositeValueProxy.class, payload);
		Assert.assertTrue(Math.abs(new Float(1.001) - bean.as().getValueAsFloat()) < 0.0001);
		test.setValueAsFloat((Float) null);
		Assert.assertEquals("{}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());

		// Date
		Date date = new Date();
		test = testBeanFactory.compositeValueProxy().as();
		test.setValueAsDate(date);
		Assert.assertEquals("{\"valueAsDate\":\"" + date.getTime() + "\"}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());
		payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload();
		bean = AutoBeanCodex.decode(testBeanFactory, CompositeValueProxy.class, payload);
		Assert.assertEquals(date, bean.as().getValueAsDate());
		test.setValueAsDate((Date) null);
		Assert.assertEquals("{}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());

		// List
		test = testBeanFactory.compositeValueProxy().as();
		List<CompositeValueProxy> testl = new ArrayList<CompositeValueProxy>();
		CompositeValueProxy test2 = testBeanFactory.compositeValueProxy().as();
		test2.setValueAsString("test");
		testl.add(test2);
		test.setValueAsList(testl);
		Assert.assertEquals("{\"valueAsList\":[{\"valueAsString\":\"test\"}]}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());
		payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload();
		bean = AutoBeanCodex.decode(testBeanFactory, CompositeValueProxy.class, payload);
		Assert.assertEquals("test", bean.as().getValueAsList().get(0).getValueAsString());
		test.setValueAsList((List) null);
		Assert.assertEquals("{}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(test)).getPayload());
	}

	public static void testOperators(TestBeanFactory testBeanFactory) {
		AndOperatorValueProxy andOperator = testBeanFactory.andOperatorValueProxy().as();
		AutoBean<AndOperatorValueProxy> andOperatorAutoBean;

		// Single criteria is included
		EqualCriteriaValueProxy equalCriteria = testBeanFactory.equalCriteriaValueProxy().as();
		andOperator.setEqualCriteria(equalCriteria);
		Assert.assertEquals("{\"equalCriteria\":{}}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(andOperator)).getPayload());

		String payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(andOperator)).getPayload();
		andOperatorAutoBean = AutoBeanCodex.decode(testBeanFactory, AndOperatorValueProxy.class, payload);
		Assert.assertNotNull(andOperatorAutoBean.as().getEqualCriteria());

		// Criteria list 
		//		EqualCriteriaValueProxy equalCriteria = testBeanFactory.equalCriteriaValueProxy().as();
		//		andOperator.setEqualCriteria(equalCriteria);
		// Composite test

		AndOperatorValueProxy operator = testBeanFactory.andOperatorValueProxy().as();
		EqualCriteriaValueProxy foreignKeyCriteria = testBeanFactory.equalCriteriaValueProxy().as();
		DefaultOperandValueProxy fieldName = testBeanFactory.defaultOperandValueProxy().as();
		CompositeValueProxy fieldNameValue = testBeanFactory.compositeValueProxy().as();
		CompositeValueProxy value = testBeanFactory.compositeValueProxy().as();

		fieldNameValue.setValueAsString("testName");
		fieldName.setSource(fieldNameValue);
		foreignKeyCriteria.setOperand(fieldName);
		value.setValueAsInteger(1);
		foreignKeyCriteria.setValue(value);
		operator.setEqualCriteria(foreignKeyCriteria);
		Assert.assertEquals("{\"equalCriteria\":{\"value\":{\"valueAsInteger\":1},\"operand\":{\"source\":{\"valueAsString\":\"testName\"}}}}", AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(operator)).getPayload());

		
		// Request factory call
	}

}
