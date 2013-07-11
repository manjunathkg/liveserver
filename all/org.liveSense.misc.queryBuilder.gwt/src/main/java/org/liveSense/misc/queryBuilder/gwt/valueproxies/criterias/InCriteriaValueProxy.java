package org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias;

import java.util.List;

import org.liveSense.misc.queryBuilder.criterias.InCriteria;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.beans.CompositeValueProxy;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(InCriteria.class)
public interface InCriteriaValueProxy extends ValueProxy, CriteriaValueProxy {	
	
	public List<CompositeValueProxy> getValues();

	public void setValues(List<CompositeValueProxy> values);

}
