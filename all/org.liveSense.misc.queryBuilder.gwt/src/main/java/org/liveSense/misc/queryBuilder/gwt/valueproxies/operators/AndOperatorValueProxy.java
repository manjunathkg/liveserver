package org.liveSense.misc.queryBuilder.gwt.valueproxies.operators;

import org.liveSense.misc.queryBuilder.operators.AndOperator;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(AndOperator.class)
public interface AndOperatorValueProxy extends ValueProxy, OperatorValueProxy {
	
}
