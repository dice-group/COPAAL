package org.dice_research.fc.sparql.query;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;

/**
 * Timeout decorator
 *
 * @author Farshad Afshari
 *
 */

public class QueryExecutionFactoryCustomHttpTimeout extends  QueryExecutionFactoryCustomHttpDecorator{

    /**
     * The timeout , beware here the timeout is long but in @QueryEngineCustomHttp it is int
     */
    private long timeOut ;

    public QueryExecutionFactoryCustomHttpTimeout(QueryExecutionFactory decorate, long timeout) {
        super(decorate);
        this.timeOut = timeout;
    }

    @Override
    public QueryExecution createQueryExecution(Query query) {
        QueryExecution result = super.createQueryExecution(query);
        result.setTimeout(timeOut);

        return result;
    }

    @Override
    public QueryExecution createQueryExecution(String queryString) {
        QueryExecution result = super.createQueryExecution(queryString);

        result.setTimeout(timeOut);

        return result;
    }
}
