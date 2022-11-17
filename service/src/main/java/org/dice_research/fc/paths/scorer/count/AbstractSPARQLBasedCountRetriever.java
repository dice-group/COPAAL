package org.dice_research.fc.paths.scorer.count;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.paths.scorer.count.max.MaxCounter;
import org.dice_research.fc.sparql.query.IQueryValidator;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSPARQLBasedCountRetriever implements ICountRetriever {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractSPARQLBasedCountRetriever.class);

  protected static final String COUNT_VARIABLE_NAME = "sum";

  protected QueryExecutionFactory qef;

  IQueryValidator queryValidator;

  /**
   * The max count retriever.
   */
  protected MaxCounter maxCounter;

  public AbstractSPARQLBasedCountRetriever(QueryExecutionFactory qef, MaxCounter maxCounter, IQueryValidator queryValidator) {
    this.qef = qef;
    this.maxCounter = maxCounter;
    this.queryValidator = queryValidator;
    LOGGER.trace("in AbstractSPARQLBasedCountRetriever QueryExecutionFactory is : {}",QueryExecutionFactory.class.getName());
    LOGGER.trace("in AbstractSPARQLBasedCountRetriever MaxCounter is : {}",maxCounter.getClass().getName());
  }

  public long deriveMaxCount(Predicate predicate) {
    return maxCounter.deriveMaxCount(predicate);
  }

  @Override
  public long countPredicateInstances(Predicate predicate) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT (count(DISTINCT *) AS ?");
    queryBuilder.append(COUNT_VARIABLE_NAME);
    queryBuilder.append(") WHERE { ?s <");
    queryBuilder.append(predicate.getProperty().getURI());
    queryBuilder.append("> ?o . ");
    predicate.getDomain().addRestrictionToQuery("s", queryBuilder);
    predicate.getRange().addRestrictionToQuery("o", queryBuilder);
    queryBuilder.append(" }");
    return executeCountQuery(queryBuilder);
  }

  /**
   * Adds the given q-restricted path instance as property path to the query in the given query
   * builder. The implementation follows the definition of property paths at
   * https://www.w3.org/TR/sparql11-query/#propertypaths.
   * 
   * @param path the path that should be added to the query
   * @param queryBuilder the builder for the query to which the path should be added
   * 
   * @deprecated Use a {@link org.dice_research.fc.sparql.path.PropPathBasedPathClauseGenerator} instead.
   */
  @Deprecated
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

  protected long countTypeInstances(ITypeRestriction restriction) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT (count(DISTINCT ?s) AS ?");
    queryBuilder.append(COUNT_VARIABLE_NAME);
    queryBuilder.append(") WHERE { ");
    restriction.addRestrictionToQuery("s", queryBuilder);
    queryBuilder.append(" }");
    return executeCountQuery(queryBuilder);
  }

  protected long executeCountQuery(StringBuilder queryBuilder) {
    String query = queryBuilder.toString();
    long time = System.currentTimeMillis();
    LOGGER.info("Starting count query {}", query);
    if(query.contains("FILTER()")||query.contains("FILTER( )")){
      query = query.replace("FILTER()","");
      query = query.replace("FILTER( )","");
      LOGGER.info("replace empty Filter");
    }
    if(queryValidator.validate(query)) {
      try (QueryExecution qe = qef.createQueryExecution(query)) {
        ResultSet result = qe.execSelect();
        if (!result.hasNext()) {
          LOGGER.info("Got a query without a single result line (\"{}\"). Returning 0.", query);
          return 0L;
        }
        QuerySolution qs = result.next();
        Literal count = qs.getLiteral(COUNT_VARIABLE_NAME);
        if (result.hasNext()) {
          LOGGER.info(
                  "Got a query with more than 1 result line (\"{}\"). The remaining lines will be ignored.",
                  query);
        }
        long n = count.getLong();
        LOGGER.info("Got a query result ({}) after {}ms.", n, System.currentTimeMillis() - time);
        return n;
      } catch (Exception e) {
        LOGGER.error("Got an exception while running count query \"" + query + "\". Returning 0.", e);
        return 0L;
      }
    }else{
      // not valid query
      LOGGER.error("Query is not valid : "+ query+" | query validator is :"+queryValidator.getClass().getName());
      return 0L;
    }
  }
}
