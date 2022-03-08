package org.dice_research.fc.sparql.query;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.resultset.XMLInput;
import org.apache.jena.sparql.util.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


/**
 * This class run SPARQL queries with CloseableHttpClient .
 *
 * @author Farshad Afshari
 *
 */

public class QueryEngineCustomHTTP implements QueryExecution {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryEngineCustomHTTP.class);
    /**
     * if be true then the request perform as POST
     */
    private boolean isPostRequest;

    /**
     * The query which should run
     */
    private Query query;

    /**
     * The SPARQL endpoint
     */
    private String service;

    /**
     * The HTTP client which will be used to send queries.
     */
    private HttpClient client;

    /**
     * The time out for running query ,
     * beware here the timeout is int (because of RequestConfig ) but in QueryExecution it is long that's why we have conversion in set timeout
     */
    private int timeout = 0;

    /**
     * when face 404 error keep trying for this try number
     * the default is 1
     * after be sure the 404 error from Virtuoso is not occur we can remove this
     */
    private int tryNumber = 1;

    /**
     * constructor of the class
     * @param query is a query to run
     * @param client is a HttpClient
     * @param service is a url of a SPARQL endpoint
     * it uses Get as a default
     */
    public QueryEngineCustomHTTP(Query query, HttpClient client, String service) {
        this(query, client, service, false);
    }

    /**
     * constructor of the class
     * @param query is a query to run
     * @param client is a HttpClient
     * @param service is a url of a SPARQL endpoint
     * @param isPostRequest is a flag , if it is true then the client uses post if not uses get
     */
    public QueryEngineCustomHTTP(Query query, HttpClient client, String service, boolean isPostRequest) {
        this.query = query;
        this.client = client;
        this.service = service;
        this.isPostRequest = isPostRequest;
    }

    @Override
    public void setInitialBinding(QuerySolution binding) {}

    @Override
    public Dataset getDataset() {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public Context getContext() {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public Query getQuery() {
        return this.query;
    }

    @Override
    public ResultSet execSelect() {
        String result = performRequest(tryNumber);

        // the result is not a valid XML then replace with an empty XML
        if(result.length()<10) {
            result = emptyXML();
        }

        ResultSet resultSet = ResultSetFactory.fromXML(result);

        return resultSet;
    }

    /**
     * the empty xml used to generate empty ResultSet
     * @return string which is an empty xml
     */
    private String emptyXML() {
        return "<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.w3.org/2001/sw/DataAccess/rf1/result2.xsd\"><head></head><results distinct=\"false\" ordered=\"true\"></results></sparql>>";
    }

    /**
     * create request
     * based on isPostRequest it determine should create GET or POST request
     * @return request ready for execute
     */
    private HttpRequestBase createRequest() throws UnsupportedEncodingException {
        HttpRequestBase request = null;
        if(isPostRequest) {
            HttpPost post = new HttpPost(service);
            post.setEntity(new StringEntity(query.toString()));
            request = post;
        } else {
            request = new HttpGet(service + "?query=" + URLEncoder.encode(query.toString(), "UTF-8"));
        }
        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/sparql-update");
        request.addHeader(HttpHeaders.ACCEPT, "application/sparql-results+xml");

        if(timeout > 0) {
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setSocketTimeout(timeout).build();
            request.setConfig(config);
        }
        return request;
    }

    /**
     * run the query and return the result
     * when the timeout reached the query terminated and should handle in catch
     * @return string and it is a result of the query
     */
    private String performRequest(int tryNumber) {
        HttpResponse response = null;
        try{
            HttpRequestBase request = createRequest();
            if(LOGGER.isDebugEnabled()){
                QueryCounter.add();
            }
            response = client.execute(request);
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            LOGGER.debug("http response code is {} query number is {}",String.valueOf(response.getStatusLine().getStatusCode()),QueryCounter.show());
            if(result.contains("404 File not found") && tryNumber < 5){
                LOGGER.info("----------try one more -------------"+tryNumber+"---");
                TimeUnit.SECONDS.sleep(3);
                performRequest(tryNumber+1);
            }
            LOGGER.debug(result);
            if(response.getStatusLine().getStatusCode()==404){
                throw new Exception("There is an error , response is 404");
            }
            return result;
        }
        catch(SocketTimeoutException | ConnectionPoolTimeoutException e) {
            LOGGER.debug("Timeout this query: "+query.toString());
            return "";
        } catch(Exception e){
            LOGGER.error(e.getMessage() + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("There is an error while running the query",e);
        }finally {
            // If we received a response, we need to ensure that its entity is consumed correctly to free
            // all resources
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
            close();
        }
    }

    @Override
    public Model execConstruct() {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public Model execConstruct(Model model) {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public Iterator<Triple> execConstructTriples() {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public Iterator<Quad> execConstructQuads() {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public Dataset execConstructDataset() {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public Dataset execConstructDataset(Dataset dataset) {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public Model execDescribe() {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public Model execDescribe(Model model) {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public Iterator<Triple> execDescribeTriples() {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public boolean execAsk() {
      String result = performRequest(tryNumber);

      // the result is not a valid XML then replace with an empty XML
      if(result.length()<10) {
          result = emptyXML();
      }
      
      return  XMLInput.booleanFromXML(result);
    }

    @Override
    public void abort() {
        throw new UnsupportedOperationException("Invalid operation");
    }

    /**
     * we do not need this because the CloseableHttpClient closed by itself , but because this method called leave it empty
     */
    @Override
    public void close() {
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void setTimeout(long timeout, TimeUnit timeoutUnits) {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public void setTimeout(long timeout) {
        Long t = timeout;
        this.timeout = t.intValue();
    }

    @Override
    public void setTimeout(long timeout1, TimeUnit timeUnit1, long timeout2, TimeUnit timeUnit2) {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public void setTimeout(long timeout1, long timeout2) {
        throw new UnsupportedOperationException("Invalid operation");
    }

    @Override
    public long getTimeout1() {
        return this.timeout;
    }

    @Override
    public long getTimeout2() {
        throw new UnsupportedOperationException("Invalid operation");
    }
}
