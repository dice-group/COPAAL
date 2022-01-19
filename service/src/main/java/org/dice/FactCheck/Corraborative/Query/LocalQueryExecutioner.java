package org.dice.FactCheck.Corraborative.Query;

/** Allows to specify a jena model to be searched for Paths instead of a sparql endpoint.
 * @author Sven Kuhlmann
 */

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;

public class LocalQueryExecutioner extends QueryExecutioner {
	private Model model;

	public LocalQueryExecutioner(Model model) {
		this.model = model;
	}

	@Override
	public QueryExecution getQueryExecution(Query query) {
		return QueryExecutionFactory.create(query, model);
	}
}
