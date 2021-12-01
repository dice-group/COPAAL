package org.dice_research.fc.paths.scorer.count.max;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for the maximum count calculation.
 *
 */
public abstract class AbstractMaxCounter implements IMaxCounter {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMaxCounter.class);

  protected QueryExecutionFactory qef;

  /**
   * The variable name of counts in SPARQL queries.
   */
  protected final String COUNT_VARIABLE_NAME = "sum";

  /**
   * Constructor.
   * 
   * @param qef The {@link QueryExecutionFactory} object to execute queries with.
   */
  public AbstractMaxCounter(QueryExecutionFactory qef) {
    this.qef = qef;
  }

  protected long executeCountQuery(StringBuilder queryBuilder) {
    String query = queryBuilder.toString();
    long time = System.currentTimeMillis();
    LOGGER.debug("Starting count query {}", query);
    try (QueryExecution qe = qef.createQueryExecution(query)) {
      ResultSet result = qe.execSelect();
      if (!result.hasNext()) {
        LOGGER.warn("Got a query without a single result line (\"{}\"). Returning 0.", query);
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
      LOGGER.debug("Got a query result ({}) after {}ms.", n, System.currentTimeMillis() - time);
      return n;
    } catch (Exception e) {
      LOGGER.error("Got an exception while running count query \"" + query + "\". Returning 0.", e);
      return 0L;
    }
  }

}
