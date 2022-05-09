package org.dice_research.fc.paths.scorer.count.max;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
/**
 * Maximum count retriever class , when can not retrieve from domain and range, then use the Virtual type
 *
 * @author Farshad Afshari
 *
 */
public class HybridMaxCounter extends MaxCounter{


    public HybridMaxCounter(QueryExecutionFactory qef) {
        super(qef);
    }

    @Override
    public long deriveMaxCount(Predicate predicate) {
        return countTypeInstancesDomain(predicate.getDomain(), predicate) * countTypeInstancesRange(predicate.getRange(), predicate);
    }

    protected long countTypeInstancesDomain(ITypeRestriction restriction, Predicate predicate) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT (count(DISTINCT ?s) AS ?");
        queryBuilder.append(COUNT_VARIABLE_NAME);
        queryBuilder.append(") WHERE { ");
        restriction.addRestrictionToQuery("s", queryBuilder);
        queryBuilder.append(" }");
        long result = executeCountQuery(queryBuilder);
        if(result == 0) {
            String predicateURI = predicate.getProperty().getURI();
            queryBuilder.setLength(0);
            queryBuilder.append("SELECT (count(DISTINCT ?s) AS ?").append(COUNT_VARIABLE_NAME).append(") WHERE {");
            queryBuilder.append("?s <").append(predicateURI).append("> [] . }");
            result = executeCountQuery(queryBuilder);
        }
        return result;
    }

    protected long countTypeInstancesRange(ITypeRestriction restriction,Predicate predicate) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT (count(DISTINCT ?s) AS ?");
        queryBuilder.append(COUNT_VARIABLE_NAME);
        queryBuilder.append(") WHERE { ");
        restriction.addRestrictionToQuery("s", queryBuilder);
        queryBuilder.append(" }");
        long result = executeCountQuery(queryBuilder);
        if(result == 0){
            String predicateURI = predicate.getProperty().getURI();
            queryBuilder.setLength(0);
            queryBuilder.append("SELECT (count(DISTINCT ?o) AS ?").append(COUNT_VARIABLE_NAME).append(") WHERE {");
            queryBuilder.append(" [] <").append(predicateURI).append("> ?o . }");
            result =  executeCountQuery(queryBuilder);
        }
        return result;
    }
}
