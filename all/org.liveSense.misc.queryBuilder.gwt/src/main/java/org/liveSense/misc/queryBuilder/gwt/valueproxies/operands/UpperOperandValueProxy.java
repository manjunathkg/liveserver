package org.liveSense.misc.queryBuilder.gwt.valueproxies.operands;

import org.liveSense.misc.queryBuilder.operands.UpperOperand;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(UpperOperand.class)
public interface UpperOperandValueProxy
	extends ValueProxy, DefaultOperandValueProxy {
	
}
