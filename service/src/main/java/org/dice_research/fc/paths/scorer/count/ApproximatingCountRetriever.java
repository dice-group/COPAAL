package org.dice_research.fc.paths.scorer.count;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.scorer.count.max.MaxCounter;
import org.dice_research.fc.sparql.query.IQueryValidator;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApproximatingCountRetriever extends AbstractSPARQLBasedCountRetriever {

  protected static final String INTERMEDIATE_COUNT_VARIABLE_NAME = "c";
  protected static final String INTERMEDIATE_COUNT2_VARIABLE_NAME = "l";
  protected static final String INTERMEDIATE_NODE_VARIABLE_NAME = "x";
  protected static final String SUBJECT_VARIABLE_NAME = INTERMEDIATE_NODE_VARIABLE_NAME + "0";
  protected static final String OBJECT_VARIABLE_NAME = "o";

  @Autowired
  public ApproximatingCountRetriever(QueryExecutionFactory qef, MaxCounter maxCounter, IQueryValidator queryValidator) {
    super(qef, maxCounter,queryValidator);
  }

  @Override
  public long countCooccurrences(Predicate predicate, QRestrictedPath path) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT (count(*) AS ?");
    queryBuilder.append(COUNT_VARIABLE_NAME);
    queryBuilder.append(") WHERE { ?" + SUBJECT_VARIABLE_NAME + " <");
    queryBuilder.append(predicate.getProperty().getURI());
    queryBuilder.append("> ?" + OBJECT_VARIABLE_NAME + " . ");
    predicate.getDomain().addRestrictionToQuery(SUBJECT_VARIABLE_NAME, queryBuilder);
    predicate.getRange().addRestrictionToQuery(OBJECT_VARIABLE_NAME, queryBuilder);
    queryBuilder.append(" ?" + SUBJECT_VARIABLE_NAME + " ");
    addAsPropertyPath(path, queryBuilder);
    queryBuilder.append(" ?" + OBJECT_VARIABLE_NAME + " }");
    return executeCountQuery(queryBuilder);
  }

  @Override
  public long countPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction,
      ITypeRestriction rangeRestriction) {
    StringBuilder queryBuilder;
    if (path.length() == 1) {
      queryBuilder = createSinglePropertyQuery(path, domainRestriction, rangeRestriction);
    } else {
      queryBuilder = createPropertyQueryRecursively(path, domainRestriction, rangeRestriction);
    }
    return executeCountQuery(queryBuilder);
  }

  protected void addTriplePattern(Pair<Property, Boolean> pathElement, String firstVariable,
      String secondVariable, StringBuilder builder) {
    String subject;
    String object;
    if (pathElement.getSecond()) {
      object = secondVariable;
      subject = firstVariable;
    } else {
      object = firstVariable;
      subject = secondVariable;
    }
    builder.append("?").append(subject);
    builder.append(" <");
    builder.append(pathElement.getFirst().getURI());
    builder.append("> ");
    builder.append("?").append(object);
    builder.append(" .\n");
  }

  private StringBuilder createSinglePropertyQuery(QRestrictedPath path,
      ITypeRestriction domainRestriction, ITypeRestriction rangeRestriction) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT (count(*) AS ?");
    queryBuilder.append(COUNT_VARIABLE_NAME);
    queryBuilder.append(") WHERE {");
    addTriplePattern(path.getPathElements().get(0), "s", "o", queryBuilder);
    domainRestriction.addRestrictionToQuery("s", queryBuilder);
    rangeRestriction.addRestrictionToQuery("o", queryBuilder);
    queryBuilder.append("}");
    return queryBuilder;
  }

  private StringBuilder createPropertyQueryRecursively(QRestrictedPath path,
      ITypeRestriction domainRestriction, ITypeRestriction rangeRestriction) {
    StringBuilder queryBuilder = new StringBuilder();
    // This is the first property in the list
    queryBuilder.append("SELECT (coalesce(sum(?" + INTERMEDIATE_COUNT_VARIABLE_NAME);
    queryBuilder.append("0*");
    if (path.length() > 2) {
      queryBuilder.append("?").append(INTERMEDIATE_COUNT2_VARIABLE_NAME);
    } else {
      queryBuilder.append("?").append(INTERMEDIATE_COUNT_VARIABLE_NAME);
    }
    queryBuilder.append("1), 0) AS ?" + COUNT_VARIABLE_NAME + ") WHERE { \n");
    // Recursion
    createPropertyQueryRecursion(0, path, domainRestriction, rangeRestriction, queryBuilder);
    queryBuilder.append("}");
    return queryBuilder;
  }

  private void createPropertyQueryRecursion(int propId, QRestrictedPath path,
                                            ITypeRestriction domainRestriction, ITypeRestriction rangeRestriction,
                                            StringBuilder queryBuilder) {
    if (propId == path.length() - 1) {
      // This is the last property in the list --> recursion ends
      queryBuilder.append("Select (count(*) as ?" + INTERMEDIATE_COUNT_VARIABLE_NAME);
      queryBuilder.append(propId);
      queryBuilder.append(") ?" + INTERMEDIATE_NODE_VARIABLE_NAME);
      queryBuilder.append(propId);
      queryBuilder.append(" where { \n");
      // Use the subject variable
      addTriplePattern(path.getPathElements().get(propId), INTERMEDIATE_NODE_VARIABLE_NAME + propId,
          OBJECT_VARIABLE_NAME, queryBuilder);
      // Add subject types
      rangeRestriction.addRestrictionToQuery(OBJECT_VARIABLE_NAME, queryBuilder);
      queryBuilder.append("} group by ?" + INTERMEDIATE_NODE_VARIABLE_NAME);
      queryBuilder.append(propId);
      queryBuilder.append('\n');
    } else {
      // Create first sub select which selects the subject and it's types
      queryBuilder.append("Select (count(*) as ?" + INTERMEDIATE_COUNT_VARIABLE_NAME);
      queryBuilder.append(propId);
      queryBuilder.append(") ?" + INTERMEDIATE_NODE_VARIABLE_NAME);
      queryBuilder.append(propId);

      // If we are at the position before the last position
      if (propId == path.length() - 2) {
        // We simply select the count from the last position
        queryBuilder.append(" ?" + INTERMEDIATE_COUNT_VARIABLE_NAME);
        queryBuilder.append(propId + 1);
      } else {
        // Calculate the product of the previous counts
        queryBuilder.append(" ( ?" + INTERMEDIATE_COUNT_VARIABLE_NAME);
        queryBuilder.append(propId + 1);
        if (propId == path.length() - 3) {
          queryBuilder.append("*sum( ?" + INTERMEDIATE_COUNT_VARIABLE_NAME);
        } else {
          queryBuilder.append("*sum( ?" + INTERMEDIATE_COUNT2_VARIABLE_NAME);
        }
        queryBuilder.append(propId + 2);
        queryBuilder.append(") as ?" + INTERMEDIATE_COUNT2_VARIABLE_NAME);
        queryBuilder.append(propId + 1);
        queryBuilder.append(")");
      }
      queryBuilder.append(" where { \n");
      addTriplePattern(path.getPathElements().get(propId), INTERMEDIATE_NODE_VARIABLE_NAME + propId,
          INTERMEDIATE_NODE_VARIABLE_NAME + (propId + 1), queryBuilder);
      // If this is the first sub select
      if (propId == 0) {
        // Add subject types
        domainRestriction.addRestrictionToQuery(SUBJECT_VARIABLE_NAME, queryBuilder);
      }
      // Start the recursion
      queryBuilder.append("{\n");
      createPropertyQueryRecursion(propId + 1, path, domainRestriction, rangeRestriction,
          queryBuilder);
      queryBuilder.append("}\n");
      // Finalize sub select of this recursion step
      queryBuilder.append("} group by ?" + INTERMEDIATE_NODE_VARIABLE_NAME);
      queryBuilder.append(propId);
      queryBuilder.append(" ?" + INTERMEDIATE_COUNT_VARIABLE_NAME);
      queryBuilder.append(propId + 1);
      queryBuilder.append('\n');
    }
  }

}
