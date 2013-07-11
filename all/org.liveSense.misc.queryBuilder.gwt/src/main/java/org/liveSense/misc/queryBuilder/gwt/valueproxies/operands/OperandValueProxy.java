package org.liveSense.misc.queryBuilder.gwt.valueproxies.operands;


import org.liveSense.misc.queryBuilder.gwt.valueproxies.beans.CompositeValueProxy;
import org.liveSense.misc.queryBuilder.operands.AbstractOperand;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(AbstractOperand.class)
public interface OperandValueProxy extends ValueProxy {	

	public String getQualifier();
	
	public CompositeValueProxy getSource();
	
	public boolean isLiteral();
	
	public String getFunction();
		
	public void setSource(CompositeValueProxy source);

	public void setQualifier(String qualifier);

	public void setLiteral(boolean literal);

	public void setFunction(String function);
		
}