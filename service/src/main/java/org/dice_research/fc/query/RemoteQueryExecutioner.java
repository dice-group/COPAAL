package org.dice_research.fc.query;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;

public class RemoteQueryExecutioner extends QueryExecutioner {
  private String serviceRequestURL;

  public RemoteQueryExecutioner(String serviceRequestURL) {
    this.serviceRequestURL = serviceRequestURL;
  }

  @Override
  public QueryExecution createQueryExecution(Query query) {
    return QueryExecutionFactory.createServiceRequest(serviceRequestURL, query);
  }

}
