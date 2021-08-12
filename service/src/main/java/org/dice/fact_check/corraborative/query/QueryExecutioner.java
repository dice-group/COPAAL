package org.dice.fact_check.corraborative.query;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryExecutioner {

  private String graphLocation;

  @Autowired
  public QueryExecutioner(String graphLocation) {
    this.graphLocation = graphLocation;
  }


  public void setLocation(String graphLocation) {
    this.graphLocation = graphLocation;
  }

  public QueryExecution getQueryExecution(Query query) {
    return QueryExecutionFactory.createServiceRequest(graphLocation, query);
  }
}
