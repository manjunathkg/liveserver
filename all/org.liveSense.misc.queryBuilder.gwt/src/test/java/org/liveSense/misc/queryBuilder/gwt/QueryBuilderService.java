package org.liveSense.misc.queryBuilder.gwt;

import java.util.List;

import org.liveSense.misc.queryBuilder.operators.AbstractOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static junit.framework.Assert.*;

public class QueryBuilderService {
	static final Logger log = LoggerFactory.getLogger(QueryBuilderService.class);

	AbstractOperator operator;
	
	public QueryBuilderService() {
		
	}
	
	public List<AbstractOperator> getOperators() {
		log.info("getOperators");
		return null;
	}
	
	public AbstractOperator getOperator() {
		log.info("getOperator");
		return operator;
	}

	public void setOperator(AbstractOperator operator) {
		log.info("setOperator");
		this.operator = operator;
		assertEquals(1,2);
	}
	

}