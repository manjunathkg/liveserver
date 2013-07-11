package org.liveSense.misc.queryBuilder.gwt.valueproxies.clauses;

import org.liveSense.misc.queryBuilder.clauses.DefaultLimitClause;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(DefaultLimitClause.class)
public interface LimitClauseValueProxy extends ValueProxy {

	public Integer getLimit();

	public void setLimit(Integer limit);
	
	public Integer getOffset();
	
	public void setOffset(Integer offset);

}
