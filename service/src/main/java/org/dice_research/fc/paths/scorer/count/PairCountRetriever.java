package org.dice_research.fc.paths.scorer.count;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.scorer.count.max.MaxCounter;
import org.dice_research.fc.sparql.path.IPathClauseGenerator;
import org.dice_research.fc.sparql.path.PropPathBasedPathClauseGenerator;
import org.dice_research.fc.sparql.query.IQueryValidator;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This extension of the {@link AbstractSPARQLBasedCountRetriever} counts the exact number of
 * subject-object pairs that are connected by the given q-restricted path. The implementation relies
 * on the usage of SPARQL property paths.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@Component
public class PairCountRetriever extends AbstractSPARQLBasedCountRetriever {

  protected IPathClauseGenerator pathClauseGenerator;

  @Autowired
  public PairCountRetriever(QueryExecutionFactory qef, MaxCounter maxCounter, IQueryValidator queryValidator) {
    this(qef, maxCounter, new PropPathBasedPathClauseGenerator(), queryValidator);
  }

  @Autowired
  public PairCountRetriever(QueryExecutionFactory qef, MaxCounter maxCounter, IPathClauseGenerator pathClauseGenerator, IQueryValidator queryValidator) {
    super(qef, maxCounter, queryValidator);
    this.pathClauseGenerator = pathClauseGenerator;
  }

  @Override
  public long countPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction,
      ITypeRestriction rangeRestriction) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT (count(DISTINCT *) AS ?");
    queryBuilder.append(COUNT_VARIABLE_NAME);
    queryBuilder.append(") WHERE { ");
    domainRestriction.addRestrictionToQuery("s", queryBuilder);
    rangeRestriction.addRestrictionToQuery("o", queryBuilder);
    pathClauseGenerator.addPath(path, queryBuilder, "s", "o");
    queryBuilder.append(" }");
    return executeCountQuery(queryBuilder);
  }

  @Override
  public long countCooccurrences(Predicate predicate, QRestrictedPath path) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT (count(DISTINCT *) AS ?");
    queryBuilder.append(COUNT_VARIABLE_NAME);
    queryBuilder.append(") WHERE { ?s <");
    queryBuilder.append(predicate.getProperty().getURI());
    queryBuilder.append("> ?o . ");
    predicate.getDomain().addRestrictionToQuery("s", queryBuilder);
    predicate.getRange().addRestrictionToQuery("o", queryBuilder);
    pathClauseGenerator.addPath(path, queryBuilder, "s", "o");
    queryBuilder.append(" }");
    return executeCountQuery(queryBuilder);
  }

}
