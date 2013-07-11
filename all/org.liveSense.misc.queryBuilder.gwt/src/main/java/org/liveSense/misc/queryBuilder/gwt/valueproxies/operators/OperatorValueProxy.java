package org.liveSense.misc.queryBuilder.gwt.valueproxies.operators;


import java.util.List;

import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.BetweenCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.CriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.DistinctFromCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.EqualCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.GreaterCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.GreaterOrEqualCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.InCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.IsNotNullCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.IsNullCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.LessCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.LessOrEqualCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.LikeCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.NotEqualCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.gwt.valueproxies.criterias.StartingWithCriteriaValueProxy;
import org.liveSense.misc.queryBuilder.operators.AbstractOperator;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(AbstractOperator.class)
public interface OperatorValueProxy extends ValueProxy {

	public void setBetweenCriteria(BetweenCriteriaValueProxy criteria);

	public void setDistinctFromCriteria(DistinctFromCriteriaValueProxy criteria);

	public void setEqualCriteria(EqualCriteriaValueProxy criteria);

	public void setGreaterCriteria(GreaterCriteriaValueProxy criteria);

	public void setGreaterOrEqualCriteria(GreaterOrEqualCriteriaValueProxy criteria);

	public void setInCriteria(InCriteriaValueProxy criteria);

	public void setIsNotNullCriteria(IsNotNullCriteriaValueProxy criteria);

	public void setIsNullCriteria(IsNullCriteriaValueProxy criteria);

	public void setLessCriteria(LessCriteriaValueProxy criteria);

	public void setLessOrEqualCriteria(LessOrEqualCriteriaValueProxy criteria);

	public void setLikeCriteria(LikeCriteriaValueProxy criteria);

	public void setNotEqualCriteria(NotEqualCriteriaValueProxy criteria);

	public void setStartingWithCriteria(StartingWithCriteriaValueProxy criteria);

	//public void setCriterias(List<CriteriaValueProxy> criterias);

	public void setAndOperator(AndOperatorValueProxy operator);

	public void setOrOperator(OrOperatorValueProxy operator);

	public void setNotOperator(NotOperatorValueProxy operator);

	public BetweenCriteriaValueProxy getBetweenCriteria();

	public DistinctFromCriteriaValueProxy getDistinctFromCriteria();

	public EqualCriteriaValueProxy getEqualCriteria();

	public GreaterCriteriaValueProxy getGreaterCriteria();

	public GreaterOrEqualCriteriaValueProxy getGreaterOrEqualCriteria();

	public InCriteriaValueProxy getInCriteria();

	public IsNotNullCriteriaValueProxy getIsNotNullCriteria();

	public IsNullCriteriaValueProxy getIsNullCriteria();

	public LessCriteriaValueProxy getLessCriteria();

	public LessOrEqualCriteriaValueProxy getLessOrEqualCriteria();

	public LikeCriteriaValueProxy getLikeCriteria();

	public NotEqualCriteriaValueProxy getNotEqualCriteria();

	public StartingWithCriteriaValueProxy getStartingWithCriteria();

	//public List<CriteriaValueProxy> getCriterias();

	public AndOperatorValueProxy getAndOperator();

	public OrOperatorValueProxy getOrOperator();

	public NotOperatorValueProxy getNotOperator();

}
