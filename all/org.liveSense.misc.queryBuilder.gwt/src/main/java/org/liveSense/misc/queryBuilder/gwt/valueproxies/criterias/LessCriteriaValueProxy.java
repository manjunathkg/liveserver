package org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias;

import org.liveSense.misc.queryBuilder.criterias.LessCriteria;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.beans.CompositeValueProxy;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(LessCriteria.class)
public interface LessCriteriaValueProxy extends ValueProxy, CriteriaValueProxy {	

	public CompositeValueProxy getValue();

	public void setValue(
		CompositeValueProxy value);

}
