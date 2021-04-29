package org.dice_research.fc.query;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;

public abstract class QueryExecutioner implements QueryExecutionFactory {

  @Override
  public QueryExecution createQueryExecution(String queryString) {
    Query query = QueryFactory.create(queryString);
    return createQueryExecution(query);
  }

  @Override
  public String getId() {
    return null;
  }

  @Override
  public String getState() {
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T unwrap(Class<T> clazz) {
    T result = getClass().isAssignableFrom(clazz) ? (T) this : null;
    return result;
  }

  @Override
  public void close() throws Exception {}
}
