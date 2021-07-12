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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class run SPARQL queries with CloseableHttpClient .
 *
 * @author Farshad Afshari
 *
 */

public class QueryEngineCustomHTTP implements QueryExecution {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryEngineCustomHTTP.class);

    private Query query;
    private String service;
    private long timeout=15000;

    public QueryEngineCustomHTTP(Query query, String service) {
        this.query = query;
        this.service = service;
    }

    @Override
    public void setInitialBinding(QuerySolution binding) {

    }

    @Override
    public Dataset getDataset() {
        return null;
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public Query getQuery() {
        return this.query;
    }

    @Override
    public ResultSet execSelect() {
        String result = createRequest(query.toString(), timeout, service);
        ResultSetFactory fac = new ResultSetFactory();

        // the result is not a valid XML then replace with an empty XML
        if(result.length()<10) {
            result = emptyXML();
        }

        ResultSet resultSet = fac.fromXML(result);

        return resultSet;
    }

    private String emptyXML() {
        return "<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.w3.org/2001/sw/DataAccess/rf1/result2.xsd\"><head></head><results distinct=\"false\" ordered=\"true\"></results></sparql>>";
    }

    private static String read(InputStream content) {
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

    protected static String createRequest(String sparqlQuery, Long to,String service){
        List<String> ret =  new ArrayList<String>();
        int code=0;
        String test;
        String actualContentType="";
        try {
            int timeout = to.intValue();
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setSocketTimeout(timeout).build();
            CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
            HttpGet get = new HttpGet(service+"?query="+ URLEncoder.encode(sparqlQuery, "UTF-8"));
            get.addHeader(HttpHeaders.ACCEPT, "application/sparql-results+xml");
            HttpResponse resp = client.execute(get);
            test = read(resp.getEntity().getContent());
            InputStream is = new ByteArrayInputStream(test.getBytes(StandardCharsets.UTF_8));
            String result = IOUtils.toString(is, StandardCharsets.UTF_8);
            LOGGER.info(result);
            return result;
        }catch(Exception e){
            LOGGER.error("Could not execute request due to ",e);
        }
        return "";
    }

    @Override
    public Model execConstruct() {
        return null;
    }

    @Override
    public Model execConstruct(Model model) {
        return null;
    }

    @Override
    public Iterator<Triple> execConstructTriples() {
        return null;
    }

    @Override
    public Iterator<Quad> execConstructQuads() {
        return null;
    }

    @Override
    public Dataset execConstructDataset() {
        return null;
    }

    @Override
    public Dataset execConstructDataset(Dataset dataset) {
        return null;
    }

    @Override
    public Model execDescribe() {
        return null;
    }

    @Override
    public Model execDescribe(Model model) {
        return null;
    }

    @Override
    public Iterator<Triple> execDescribeTriples() {
        return null;
    }

    @Override
    public boolean execAsk() {
        return false;
    }

    @Override
    public void abort() {

    }

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
        this.timeout = timeout;
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
