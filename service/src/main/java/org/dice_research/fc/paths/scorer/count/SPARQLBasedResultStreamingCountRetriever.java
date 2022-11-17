package org.dice_research.fc.paths.scorer.count;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.scorer.count.max.MaxCounter;
import org.dice_research.fc.sparql.path.IPathClauseGenerator;
import org.dice_research.fc.sparql.path.PropPathBasedPathClauseGenerator;
import org.dice_research.fc.sparql.query.IQueryValidator;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class makes use of a SPARQL client to query for results and count them, locally. It
 * shouldn't be used in production since streaming all results may lead to much higher runtimes than
 * counting them within the SPARQL endpoint.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@Component
public class SPARQLBasedResultStreamingCountRetriever extends AbstractSPARQLBasedCountRetriever {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SPARQLBasedResultStreamingCountRetriever.class);

  protected IPathClauseGenerator pathClauseGenerator;

  @Autowired
  public SPARQLBasedResultStreamingCountRetriever(QueryExecutionFactory qef,
      MaxCounter maxCounter, IQueryValidator queryValidator) {
    this(qef, maxCounter, new PropPathBasedPathClauseGenerator(), queryValidator);
  }

  @Autowired
  public SPARQLBasedResultStreamingCountRetriever(QueryExecutionFactory qef, MaxCounter maxCounter,
                                                  IPathClauseGenerator pathClauseGenerator, IQueryValidator queryValidator) {
    super(qef, maxCounter, queryValidator);
    this.pathClauseGenerator = pathClauseGenerator;
  }

  @Override
  public long countPredicateInstances(Predicate predicate) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT DISTINCT * WHERE { ?s <");
    queryBuilder.append(predicate.getProperty().getURI());
    queryBuilder.append("> ?o . ");
    predicate.getDomain().addRestrictionToQuery("s", queryBuilder);
    predicate.getRange().addRestrictionToQuery("o", queryBuilder);
    queryBuilder.append(" }");
    return executeCountQuery(queryBuilder);
  }

  protected long countTypeInstances(ITypeRestriction restriction) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT DISTINCT * WHERE { ");
    restriction.addRestrictionToQuery("s", queryBuilder);
    queryBuilder.append(" }");
    return executeCountQuery(queryBuilder);
  }

  @Override
  public long countPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction,
      ITypeRestriction rangeRestriction) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT DISTINCT * WHERE { ");
    domainRestriction.addRestrictionToQuery("s", queryBuilder);
    rangeRestriction.addRestrictionToQuery("o", queryBuilder);
    pathClauseGenerator.addPath(path, queryBuilder, "s", "o");
    queryBuilder.append(" }");
    return executeCountQuery(queryBuilder);
  }

  @Override
  public long countCooccurrences(Predicate predicate, QRestrictedPath path) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT DISTINCT * WHERE { ?s <");
    queryBuilder.append(predicate.getProperty().getURI());
    queryBuilder.append("> ?o . ");
    predicate.getDomain().addRestrictionToQuery("s", queryBuilder);
    predicate.getRange().addRestrictionToQuery("o", queryBuilder);
    pathClauseGenerator.addPath(path, queryBuilder, "s", "o");
    queryBuilder.append(" }");
    return executeCountQuery(queryBuilder);
  }

  protected long executeCountQuery(StringBuilder queryBuilder) {
    String query = queryBuilder.toString();
    long time = System.currentTimeMillis();
    LOGGER.debug("Starting count query {}", query);
    if(query.contains("FILTER()")||query.contains("FILTER( )")){
      query = query.replace("FILTER()","");
      query = query.replace("FILTER( )","");
      LOGGER.info("replace empty Filter");
    }
    try (QueryExecution qe = qef.createQueryExecution(query)) {
      ResultSet result = qe.execSelect();
      long count = 0;
      while (result.hasNext()) {
        result.next();
        ++count;
      }
      LOGGER.info("Got a query result ({}) after {}ms.", count, System.currentTimeMillis() - time);
      return count;
    } catch (Exception e) {
      LOGGER.error("Got an exception while running count query \"" + query + "\". Returning 0.", e);
      return 0L;
    }
  }

}
