package org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias;

import org.liveSense.misc.queryBuilder.criterias.DistinctFromCriteria;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.beans.CompositeValueProxy;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(DistinctFromCriteria.class)
public interface DistinctFromCriteriaValueProxy extends ValueProxy, CriteriaValueProxy {

	public CompositeValueProxy getValue();

	public void setValue(
		CompositeValueProxy value);
}
