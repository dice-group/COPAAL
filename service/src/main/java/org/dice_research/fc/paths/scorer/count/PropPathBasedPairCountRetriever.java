package org.dice_research.fc.paths.scorer.count;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;

/**
 * This extension of the {@link AbstractSPARQLBasedCountRetriever} counts the exact number of
 * subject-object pairs that are connected by the given q-restricted path. The implementation relies
 * on the usage of SPARQL property paths.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class PropPathBasedPairCountRetriever extends AbstractSPARQLBasedCountRetriever {

  public PropPathBasedPairCountRetriever(QueryExecutionFactory qef) {
    super(qef);
  }

  @Override
  public int countPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction,
      ITypeRestriction rangeRestriction) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT count(DISTINCT *) AS ?");
    queryBuilder.append(COUNT_VARIABLE_NAME);
    queryBuilder.append(" WHERE { ");
    domainRestriction.addRestrictionToQuery("s", queryBuilder);
    rangeRestriction.addRestrictionToQuery("o", queryBuilder);
    queryBuilder.append(" ?s <");
    addAsPropertyPath(path, queryBuilder);
    queryBuilder.append("> ?o }");
    return executeCountQuery(queryBuilder);
  }

  @Override
  public int countCooccurrences(Predicate predicate, QRestrictedPath path) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT count(DISTINCT *) AS ?");
    queryBuilder.append(COUNT_VARIABLE_NAME);
    queryBuilder.append(" WHERE { ?s <");
    queryBuilder.append(predicate.getProperty().getURI());
    queryBuilder.append("> ?o . ");
    predicate.getDomain().addRestrictionToQuery("s", queryBuilder);
    predicate.getRange().addRestrictionToQuery("o", queryBuilder);
    queryBuilder.append(" ?s ");
    addAsPropertyPath(path, queryBuilder);
    queryBuilder.append(" ?o }");
    return executeCountQuery(queryBuilder);
  }

}
