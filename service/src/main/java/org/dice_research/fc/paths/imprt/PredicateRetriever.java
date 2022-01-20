package org.dice_research.fc.paths.imprt;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.dice_research.fc.sparql.filter.NamespaceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves all or a select few predicates from a KG.
 * 
 * @author Alexandra Silva
 *
 */
public class PredicateRetriever {

  private static final Logger LOGGER = LoggerFactory.getLogger(PredicateRetriever.class);

  /**
   * The query executioner.
   */
  private QueryExecutionFactory qef;

  /**
   * Predicate variable in queries.
   */
  private final String PREDICATE_VARIABLE = "p";

  /**
   * Predicate filter.
   */
  private Predicate<String> filter;

  /**
   * Constructor.
   */
  public PredicateRetriever(QueryExecutionFactory qef, NamespaceFilter filter) {
    this.qef = qef;
    if (filter.isExcludeMatch()) {
      this.filter = k -> k.contains(filter.getNamespace());
    } else {
      this.filter = k -> !k.contains(filter.getNamespace());
    }
  }

  public PredicateRetriever(QueryExecutionFactory qef, Predicate<String> filter) {
    this.qef = qef;
    this.filter = filter;
  }

  /**
   * 
   * Retrieves < n most frequent predicates from a graph.
   * 
   * @param max Maximum number of predicates desired.
   * @return
   */
  public Set<String> getMostFrequentPredicates(int max) {
    StringBuilder freqPredQuery = new StringBuilder();
    freqPredQuery.append("SELECT ?p (COUNT(*) AS ?freq) WHERE { ?s ?p ?o .  ");
    // removed filter clause since it was not working
    // filter.addFilter(PREDICATE_VARIABLE, freqPredQuery);
    freqPredQuery.append("} GROUP BY ?p ORDER BY DESC(?freq) LIMIT ");
    freqPredQuery.append(max);

    // execute query and filter afterwards
    Set<String> result = executeQuery(freqPredQuery.toString());
    result.removeIf(filter);
    return result;
  }

  /**
   * @return All predicates in a graph.
   */
  public Set<String> getAllPredicates() {
    return executeQuery("SELECT DISTINCT ?p  WHERE { ?s ?p ?o . }");
  }


  private Set<String> executeQuery(String query) {
    Set<String> predicates = new HashSet<>();
    try (QueryExecution qe = qef.createQueryExecution(query)) {
      ResultSet result = qe.execSelect();
      while (result.hasNext()) {
        QuerySolution qs = result.next();
        predicates.add(qs.getResource(PREDICATE_VARIABLE).getURI());
      }
    } catch (Exception e) {
      LOGGER.error("Got an exception while running count query \"" + query + "\". Returning 0.", e);
      return predicates;
    }
    return predicates;
  }

}
