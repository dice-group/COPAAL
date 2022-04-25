package org.dice_research.fc.sparql.query;

import org.aksw.jena_sparql_api.core.QueryExecutionFactoryBase;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;

/**
 * This class is factory for custom http query execution .
 *
 * @author Farshad Afshari
 *
 */

public class QueryExecutionFactoryCustomHttp extends QueryExecutionFactoryBase {
  /**
   * The url of SPARQL endpoint
   */
  private String service;

  /**
   * Http client
   */
  private CloseableHttpClient client;

  /**
   * this flag show is it should use Post or Get
   */
  private boolean isPostRequest;
  /**
   * Constructor.
   * 
   * @param service The URL of the SPARQL endpoint.
   */
  public QueryExecutionFactoryCustomHttp(String service) {
    this(service, 0, false);
  }

  /**
   * Constructor.
   *
   * @param service The URL of the SPARQL endpoint.
   * @param isPostRequest this flag show is it should use Post or Get
   */
  public QueryExecutionFactoryCustomHttp(String service,boolean isPostRequest) {
    this(service, 0, isPostRequest);
  }

  /**
   * Constructor.
   * 
   * @param service The URL of the SPARQL endpoint.
   * @param timeout The time out for running a query.
   * @param isPostRequest this flag show is it should use Post or Get
   */
  public QueryExecutionFactoryCustomHttp(String service, int timeout,boolean isPostRequest) {
    this.service = service;
    this.isPostRequest = isPostRequest;
    HttpClientBuilder builder = HttpClientBuilder.create();
    if (timeout > 0) {
      RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout)
          .setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
      builder.setDefaultRequestConfig(config);
    }
    client = builder.build();
  }

  /**
   * Constructor.
   * 
   * @param service The URL of the SPARQL endpoint.
   * @param client The HTTP client that will be used to send requests. It should be noted that this
   *        factory will take over the ownership of the client, i.e., it will close the client if
   *        the factory is closed.
   * @param isPostRequest this flag show is it should use Post or Get
   */
  public QueryExecutionFactoryCustomHttp(String service, CloseableHttpClient client,boolean isPostRequest) {
    this.service = service;
    this.client = client;
    this.isPostRequest = isPostRequest;
  }

  @Override
  public String getId() {
    return service;
  }

  @Override
  public String getState() {
    return null;
  }

  @Override
  public QueryExecution createQueryExecution(Query query) {
    QueryEngineCustomHTTP qe = new QueryEngineCustomHTTP(query, client, service,isPostRequest);
    return qe;
  }

  @Override
  public QueryExecution createQueryExecution(String queryString) {
    Query query = QueryFactory.create(queryString);
    QueryEngineCustomHTTP qe = new QueryEngineCustomHTTP(query, client, service,isPostRequest);
    return qe;
  }

  @Override
  public void close() {
    if (client != null) {
      try {
        client.close();
      } catch (Exception e) {
        // Nothing to do
      }
    }
  }
}
