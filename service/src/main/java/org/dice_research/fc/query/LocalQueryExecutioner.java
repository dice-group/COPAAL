package org.dice_research.fc.query;

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
  public QueryExecution createQueryExecution(Query query) {
    return QueryExecutionFactory.create(query, model);
  }

}
