package org.dice_research.fc.sparql.query;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;

/**
 * the decorator for custom http query execution factory , when need add a decorator it should extend this
 *
 * @author Farshad Afshari
 *
 */

public class QueryExecutionFactoryCustomHttpDecorator implements QueryExecutionFactory {

    /**
     * decorator
     */
    protected QueryExecutionFactory decorate;

    public QueryExecutionFactoryCustomHttpDecorator(QueryExecutionFactory decorate) {
        this.decorate = decorate;
    }

    @Override
    public String getId() {
        return decorate.getId();
    }

    @Override
    public String getState() {
        return decorate.getState();
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        T result;
        if(getClass().isAssignableFrom(clazz)) {
            result = (T)this;
        }
        else {
            result = decorate.unwrap(clazz);
        }

        return result;
    }

    @Override
    public void close() throws Exception {
        decorate.close();
    }

    @Override
    public QueryExecution createQueryExecution(Query query) {
        return decorate.createQueryExecution(query);
    }

    @Override
    public QueryExecution createQueryExecution(String queryString) {
        return decorate.createQueryExecution(queryString);
    }
}
