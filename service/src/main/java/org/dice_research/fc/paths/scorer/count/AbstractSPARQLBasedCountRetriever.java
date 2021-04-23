package org.dice_research.fc.paths.scorer.count;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSPARQLBasedCountRetriever implements ICountRetriever {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractSPARQLBasedCountRetriever.class);

  protected static final String COUNT_VARIABLE_NAME = "sum";

  protected QueryExecutionFactory qef;

  public AbstractSPARQLBasedCountRetriever(QueryExecutionFactory qef) {
    this.qef = qef;
  }

  @Override
  public int countPredicateInstances(Predicate predicate) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT count(DISTINCT *) AS ?");
    queryBuilder.append(COUNT_VARIABLE_NAME);
    queryBuilder.append(" WHERE { ?s <");
    queryBuilder.append(predicate.getProperty().getURI());
    queryBuilder.append("> ?o }");
    return executeCountQuery(queryBuilder);
  }

  @Override
  public int deriveMaxCount(Resource subject, Predicate predicate, Resource Object) {
    return countTypeInstances(predicate.getDomain()) * countTypeInstances(predicate.getRange());
  }

  /**
   * Adds the given q-restricted path instance as property path to the query in the given query
   * builder. The implementation follows the definition of property paths at
   * https://www.w3.org/TR/sparql11-query/#propertypaths.
   * 
   * @param path the path that should be added to the query
   * @param queryBuilder the builder for the query to which the path should be added
   */
  protected void addAsPropertyPath(QRestrictedPath path, StringBuilder queryBuilder) {
    boolean first = true;
    for (Pair<Property, Boolean> p : path.getPathElements()) {
      if (first) {
        first = false;
      } else {
        queryBuilder.append('/');
      }
      if (!p.getSecond()) {
        queryBuilder.append('^');
      }
      queryBuilder.append('<');
      queryBuilder.append(p.getFirst().getURI());
      queryBuilder.append('>');
    }
  }

  protected int countTypeInstances(ITypeRestriction restriction) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT count(DISTINCT ?s) AS ?");
    queryBuilder.append(COUNT_VARIABLE_NAME);
    queryBuilder.append(" WHERE { ");
    restriction.addRestrictionToQuery("?s", queryBuilder);
    queryBuilder.append(" }");
    return executeCountQuery(queryBuilder);
  }

  protected int executeCountQuery(StringBuilder queryBuilder) {
    QueryExecution qe = qef.createQueryExecution(queryBuilder.toString());
    ResultSet result = qe.execSelect();
    if (!result.hasNext()) {
      LOGGER.warn("Got a query without a single result line (\"{}\"). Returning 0.", queryBuilder);
    }
    QuerySolution qs = result.next();
    Literal count = qs.getLiteral(COUNT_VARIABLE_NAME);
    if (result.hasNext()) {
      LOGGER.info(
          "Got a query with more than 1 result line (\"{}\"). The remaining lines will be ignored.",
          queryBuilder);
    }
    return count.getInt();
  }
}
