package org.dice_research.fc.sparql.query;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.resultset.XMLInput;
import org.apache.jena.sparql.util.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
     * constructor of the class
     * @param query is a query to run
     * @param service is a url of a SPARQL endpoint
     */

    public QueryEngineCustomHTTP(Query query, HttpClient client, String service) {
        this.query = query;
        this.client = client;
        this.service = service;
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
        String result = createRequest();

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
     * read an InputStream and return the String
     * @param content is an InputStream
     * @return string which is a content of input stream
     */
    private String read(InputStream content) {
        ByteSource byteSource = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return content;
            }
        };

        try {
            return byteSource.asCharSource(Charsets.UTF_8).read();
        } catch (IOException e) {
            LOGGER.error("Could not read stream due to ",e);
        }
        return "";
    }


    /**
     * run the query and return the result
     * when the timeout reached the query terminated and should handle in catch
     * @return string which is a result of the query
     */
    private String createRequest() {
        HttpResponse response = null;
        try {
            HttpGet get = new HttpGet(service + "?query=" + URLEncoder.encode(query.toString(), "UTF-8"));
            if(timeout > 0) {
              RequestConfig config = RequestConfig.custom()
                  .setConnectTimeout(timeout)
                  .setConnectionRequestTimeout(timeout)
                  .setSocketTimeout(timeout).build();
              get.setConfig(config);
            }
            get.addHeader(HttpHeaders.ACCEPT, "application/sparql-results+xml");
            response = client.execute(get);
            String responseContent = read(response.getEntity().getContent());
            InputStream is = new ByteArrayInputStream(responseContent.getBytes(StandardCharsets.UTF_8));
            String result = IOUtils.toString(is, StandardCharsets.UTF_8);
            LOGGER.debug(result);
            return result;
        }
        catch(SocketTimeoutException e) {
            LOGGER.debug("Timeout this query: "+query.toString());
            return "";
        }
        catch(Exception e){
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
      String result = createRequest();

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
