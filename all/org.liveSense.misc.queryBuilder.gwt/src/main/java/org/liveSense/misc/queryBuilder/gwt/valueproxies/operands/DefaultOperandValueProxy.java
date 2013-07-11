package org.liveSense.misc.queryBuilder.gwt.valueproxies.operands;

import org.liveSense.misc.queryBuilder.operands.DefaultOperand;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(DefaultOperand.class)
public interface DefaultOperandValueProxy extends ValueProxy, OperandValueProxy  {
	
}
