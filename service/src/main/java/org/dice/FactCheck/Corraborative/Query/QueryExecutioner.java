package org.dice.FactCheck.Corraborative.Query;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;

public abstract class QueryExecutioner {
	public abstract QueryExecution getQueryExecution(Query query);
}
