package org.liveSense.misc.queryBuilder.gwt.valueproxies.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.liveSense.misc.queryBuilder.beans.Value;
import org.liveSense.misc.queryBuilder.beans.ValueDomain.ValueTypes;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(Value.class)
public interface CompositeValueProxy extends ValueProxy {

	public void setValueAsString(String value);

	public void setValueAsBoolean(Boolean value);

	public void setValueAsInteger(Integer value);

	public void setValueAsLong(Long value);

	public void setValueAsDouble(Double value);

	public void setValueAsFloat(Float value);

	public void setValueAsDate(Date value);

	public void setValueAsBigInteger(BigInteger value);

	public void setValueAsBigDecimal(BigDecimal value);

	public void setValueAsList(List<CompositeValueProxy> value);

	public void setType(ValueTypes type);

	public String getValueAsString();

	public Boolean getValueAsBoolean();

	public Integer getValueAsInteger();

	public Long getValueAsLong();

	public Double getValueAsDouble();

	public Float getValueAsFloat();

	public Date getValueAsDate();

	public BigInteger getValueAsBigInteger();

	public BigDecimal getValueAsBigDecimal();
	
	public List<CompositeValueProxy> getValueAsList();

	public ValueTypes getType();
}
