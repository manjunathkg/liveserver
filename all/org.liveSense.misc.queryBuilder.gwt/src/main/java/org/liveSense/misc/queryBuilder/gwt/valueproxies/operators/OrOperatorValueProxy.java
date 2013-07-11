package org.liveSense.misc.queryBuilder.gwt.valueproxies.operators;

import org.liveSense.misc.queryBuilder.operators.OrOperator;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(OrOperator.class)
public interface OrOperatorValueProxy extends ValueProxy, OperatorValueProxy {
}
