package org.liveSense.misc.queryBuilder.gwt.valueproxies.operators;

import org.liveSense.misc.queryBuilder.operators.NotOperator;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(NotOperator.class)
public interface NotOperatorValueProxy extends ValueProxy, OperatorValueProxy {
}
