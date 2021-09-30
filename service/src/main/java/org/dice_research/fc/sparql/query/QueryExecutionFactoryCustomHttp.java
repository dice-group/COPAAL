package org.dice_research.fc.sparql.query;

import java.io.IOException;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryBase;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.dice_research.fc.paths.repository.IQueryResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is factory for custom http query execution .
 *
 * @author Farshad Afshari
 *
 */

public class QueryExecutionFactoryCustomHttp extends QueryExecutionFactoryBase {

  /**
   * the reposotory need for use QueryEngineCustomHTTPSaveDb
   */
  protected IQueryResultsRepository repository;

  /**
   * the type of QueryEngineCustomHttp
   * default : QueryEngineCustomHttp
   * useDb : QueryEngineCustomHTTPSaveDb
   */
  private String type;

  /**
   * The url of SPARQL endpoint
   */
  private String service;
  private CloseableHttpClient client;

/*  *//**
   * Constructor.
   * 
   * @param service The URL of the SPARQL endpoint.
   * @param type of QueryEngineCustomHttp
   *//*
  public QueryExecutionFactoryCustomHttp(String service, String type) {
    this(service, 0,type);
  }

  *//**
   * Constructor.
   *
   * @param service The URL of the SPARQL endpoint.
   *//*
  public QueryExecutionFactoryCustomHttp(String service,IQueryResultsRepository repository) {
    this(service, 0,repository);
  }*/

  /**
   * Constructor.
   * 
   * @param service The URL of the SPARQL endpoint.
   * @param timeout The time out for running a query.
   */
  public QueryExecutionFactoryCustomHttp(String service, int timeout, IQueryResultsRepository repository, String type) {
    this.service = service;
    this.repository = repository;
    this.type = type;
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
   */
  public QueryExecutionFactoryCustomHttp(String service, CloseableHttpClient client,IQueryResultsRepository repository) {
    this.service = service;
    this.client = client;
    this.repository = repository;
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
    QueryEngineCustomHTTP qe = new QueryEngineCustomHTTP(query, client, service);
    if(type.equalsIgnoreCase("useDb")){
      if(repository==null){
        //throw new Exception("the IQueryResultsRepository is null , it is impossible to use database");
      }
      qe = new QueryEngineCustomHTTPSaveDb(query, client, service, repository);
    }
    return qe;
  }

  @Override
  public QueryExecution createQueryExecution(String queryString) {
    Query query = QueryFactory.create(queryString);
    return createQueryExecution(query);
  }

  @Override
  public void close() {
    if (client != null) {
      try {
        client.close();
      } catch (IOException e) {
        // Nothing to do
      }
    }
  }
}
