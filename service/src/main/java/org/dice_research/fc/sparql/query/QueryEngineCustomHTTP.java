package org.dice_research.fc.sparql.query;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.util.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    int tryNumber;

    /**
     * The query which should run
     */
    private Query query;

    /**
     * The SPARQL endpoint
     */
    private String service;

    /**
     * The time out for running query ,
     * beware here the timeout is int (because of RequestConfig ) but in QueryExecution it is long that's why we have conversion in set timeout
     */
    private int timeout;

    /**
     * constructor of the class
     * @param query is a query to run
     * @param service is a url of a SPARQL endpoint
     */

    public QueryEngineCustomHTTP(Query query, String service) {
        this.query = query;
        this.service = service;
        tryNumber = 0;
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
        tryNumber = 0;
        String result = createRequest();
        ResultSetFactory fac = new ResultSetFactory();

        // the result is not a valid XML then replace with an empty XML
        if(result.length()<10) {
            result = emptyXML();
        }
        ResultSet resultSet = fac.fromXML(result);

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
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        try(CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build()){
            LOGGER.info("--------Start Reqest------------");
            LOGGER.info(service + "?query=" + URLEncoder.encode(query.toString(), "UTF-8"));
            HttpGet get = new HttpGet(service + "?query=" + URLEncoder.encode(query.toString(), "UTF-8"));
            get.addHeader(HttpHeaders.ACCEPT, "application/sparql-results+xml");
            HttpResponse resp = client.execute(get);
            String responseContent = read(resp.getEntity().getContent());
            InputStream is = new ByteArrayInputStream(responseContent.getBytes(StandardCharsets.UTF_8));
            String result = IOUtils.toString(is, StandardCharsets.UTF_8);

            if(result.contains("404 File not found") && tryNumber < 5){
                // face error try one more time
                LOGGER.info("----------try one more -------------"+tryNumber+"---");
                TimeUnit.SECONDS.sleep(3);
                tryNumber = tryNumber +1;
                client.close();
                createRequest();
            }
            LOGGER.info(result);
            LOGGER.info("----------end Reqest-------------");
            return result;
        }
        catch(java.net.SocketTimeoutException e) {
            LOGGER.info("Timeout this query: "+query.toString());
            return "";
        }
        catch(Exception e){
            throw new RuntimeException("There is an error while running the query",e);
        }finally {
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
        throw new UnsupportedOperationException("Invalid operation");
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
