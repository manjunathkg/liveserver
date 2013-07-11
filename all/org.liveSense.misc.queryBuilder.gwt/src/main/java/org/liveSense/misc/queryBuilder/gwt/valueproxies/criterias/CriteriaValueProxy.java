package org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias;

import org.liveSense.misc.queryBuilder.criterias.AbstractCriteria;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.operands.OperandValueProxy;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;


@ProxyFor(AbstractCriteria.class)
public interface CriteriaValueProxy extends ValueProxy {

	public OperandValueProxy getOperand();

	public void setOperand(OperandValueProxy operand);
		
}
