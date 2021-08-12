package org.dice.fact_check.corraborative.query;
/**
 * Allows to specify a jena model to be searched for Paths instead of a sparql endpoint.
 * 
 * @author Sven Kuhlmann
 */

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LocalQueryExecutioner extends QueryExecutioner {
  private Model model;

  @Autowired
  public LocalQueryExecutioner(String graphString) {
    super(graphString);
    if (!graphString.isBlank()) {
      readModelFromFile(graphString);
    }
  }

  private void readModelFromFile(String graphString) {
    Model model = ModelFactory.createDefaultModel();
    model.read(graphString);
    this.model = model;
  }

  @Override
  public void setLocation(String graphLocation) {
    super.setLocation(graphLocation);
    readModelFromFile(graphLocation);
  }

  @Override
  public QueryExecution getQueryExecution(Query query) {
    return QueryExecutionFactory.create(query, model);
  }
}
