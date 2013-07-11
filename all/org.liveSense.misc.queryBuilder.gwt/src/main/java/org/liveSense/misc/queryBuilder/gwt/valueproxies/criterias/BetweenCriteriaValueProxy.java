package org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias;

import org.liveSense.misc.queryBuilder.criterias.BetweenCriteria;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.beans.CompositeValueProxy;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(BetweenCriteria.class)
public interface BetweenCriteriaValueProxy extends ValueProxy, CriteriaValueProxy {	

	public CompositeValueProxy getValue1();

	public void setValue1(
		CompositeValueProxy value1);

	public CompositeValueProxy getValue2();
	
	public void setValue2(
		CompositeValueProxy value2);

}
