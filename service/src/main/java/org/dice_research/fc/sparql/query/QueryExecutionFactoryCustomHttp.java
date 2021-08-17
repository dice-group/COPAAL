package org.dice_research.fc.sparql.query;

import org.aksw.jena_sparql_api.core.QueryExecutionFactoryBase;
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
    public QueryExecutionFactoryCustomHttp(String service) {
        this.service = service;
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
        QueryEngineCustomHTTP qe = new QueryEngineCustomHTTP(query,service);
        return qe;
    }

    @Override
    public QueryExecution createQueryExecution(String queryString) {
        Query query = QueryFactory.create(queryString);
        QueryEngineCustomHTTP qe = new QueryEngineCustomHTTP(query, service);
        return qe;
    }
}
