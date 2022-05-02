package org.dice_research.fc.paths.search;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.filter.IRIFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class is the path searcher of COPAAL as described in the paper of Syed et al. (2019). It is
 * based on a search for paths connecting s and o with a maximum length.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@Component
public class SubSelectBasedSOPathSearcher extends SPARQLBasedSOPathSearcher {

  @Autowired
  public SubSelectBasedSOPathSearcher(QueryExecutionFactory qef, int maximumLength,
      Collection<IRIFilter> propertyFilter) {
    super(qef,maximumLength, propertyFilter);
  }

  protected List<SearchQuery> generateSearchQueries(Resource subject, Predicate predicate,
      Resource object) {
    List<SearchQuery> queries = new ArrayList<SearchQuery>();
    generateShortSearchQuery(subject, predicate, object, queries);
    for (int i = 2; i <= maximumLength; ++i) {
      generateSearchQueries(i, subject, predicate, object, queries);
    }
    return queries;
  }

  /**
   * Generates queries for searching paths of length 1.
   * 
   * @param subject the subject of the given triple
   * @param predicate the predicate of the given triple
   * @param object the object of the given triple
   * @param queries the list of search queries to which the generated queries should be added
   */
  protected void generateShortSearchQuery(Resource subject, Predicate predicate, Resource object,
      List<SearchQuery> queries) {
    // s p1 o
    StringBuilder queryBuilder = new StringBuilder();
    // (PROPERTY_VARIABLE_NAME + "1") is the same as using propertyVariables[0] but faster ;)
    queryBuilder.append("SELECT DISTINCT ?" + PROPERTY_VARIABLE_NAME + "1 WHERE { <");
    queryBuilder.append(subject.getURI());
    queryBuilder.append("> ?" + PROPERTY_VARIABLE_NAME + "1 <");
    queryBuilder.append(object.getURI());
    queryBuilder.append("> . ");
    queryBuilder.append(propertyFilter[0]);
    queryBuilder.append(" }");
    BitSet directions = new BitSet(1);
    directions.set(0);
    queries.add(new SearchQuery(queryBuilder.toString(), directions, 1));
    // o p1 s
    queryBuilder.delete(0, queryBuilder.length());
    queryBuilder.append("SELECT DISTINCT ?" + PROPERTY_VARIABLE_NAME + "1 WHERE { <");
    queryBuilder.append(object.getURI());
    queryBuilder.append("> ?" + PROPERTY_VARIABLE_NAME + "1 <");
    queryBuilder.append(subject.getURI());
    queryBuilder.append("> . ");
    queryBuilder.append(propertyFilter[0]);
    queryBuilder.append(" }");
    directions = new BitSet(1);
    queries.add(new SearchQuery(queryBuilder.toString(), directions, 1));
  }

  /**
   * Generates 2^{length} queries of length > 1. For shorter queries,
   * {@link #generateShortSearchQuery(Resource, Predicate, Resource, List)} should be used.
   * 
   * @param length the length of the queries (i.e., the number of properties)
   * @param subject the subject of the given triple
   * @param predicate the predicate of the given triple
   * @param object the object of the given triple
   * @param queries the list of search queries to which the generated queries should be added
   */
  protected void generateSearchQueries(int length, Resource subject, Predicate predicate,
      Resource object, List<SearchQuery> queries) {
    // Create search query builders for all direction combinations
    SearchQueryBuilder[] builders = new SearchQueryBuilder[1 << length];
    for (int i = 0; i < builders.length; ++i) {
      builders[i] = new SearchQueryBuilder(length);
    }
    // Fill the builders recursively
    generateSearchQuery_Recursion(1, length, subject, predicate, object, builders);
    // Add the built queries to the result
    for (int i = 0; i < builders.length; ++i) {
      queries.add(builders[i].build());
    }
  }

  /**
   * A method for recursively generating search queries of a given length. The queries are generated
   * by updating the given array of query builders.
   * 
   * @param step the current step, i.e., which of the properties is selected by the sub query
   *        generated by this call (starts at 1).
   * @param length the length of the queries, i.e., the number of properties that the queries will
   *        select
   * @param subject the subject of the given triple
   * @param predicate the predicate of the given triple
   * @param object the object of the given triple
   * @param builders the array of query builders that are used to build the single queries.
   */
  protected void generateSearchQuery_Recursion(int step, int length, Resource subject,
      Predicate predicate, Resource object, SearchQueryBuilder[] builders) {
    // prepare some stuff
    StringBuilder localBuilder = new StringBuilder();
    String sVariable = step == 1 ? ("<" + subject.getURI() + ">")
        : ("?" + INTERMEDIATE_NODE_VARIABLE_NAME + (step - 1));
    String oVariable = step == length ? ("<" + object.getURI() + ">")
        : ("?" + INTERMEDIATE_NODE_VARIABLE_NAME + step);

    // If this is not the first select, we need to start with a bracket
    if (step > 1) {
      localBuilder.append(" { ");
    }

    // Create SELECT part
    localBuilder.append("SELECT DISTINCT");
    for (int i = step; i <= length; ++i) {
      localBuilder.append(" ?");
      localBuilder.append(propertyVariables[i - 1]);
    }
    // If this is not the first select, we need to select the intermediate node of this step
    if (step > 1) {
      localBuilder.append(" ?");
      localBuilder.append(INTERMEDIATE_NODE_VARIABLE_NAME);
      localBuilder.append(step - 1);
    }
    localBuilder.append(" WHERE { ");

    // prepare recursion by adding the first part of the query to the builders
    for (int i = 0; i < builders.length; ++i) {
      builders[i].getQueryBuilder().append(localBuilder);
    }
    localBuilder.delete(0, localBuilder.length());

    // Recursion
    if (step < length) {
      generateSearchQuery_Recursion(step + 1, length, subject, predicate, object, builders);
    }

    // Create a mask for this step to separate the directions
    int stepMask = 1 << (step - 1);
    // create the triple pattern of this step with the s -> o direction
    addPatternWithFilter(step, sVariable, oVariable, localBuilder);
    // go through the builders and add the pattern in this direction to all that fit to the mask of
    // this step
    for (int i = 0; i < builders.length; ++i) {
      if ((i & stepMask) > 0) {
        builders[i].getQueryBuilder().append(localBuilder);
        // The triple pattern is in s -> o direction, so we have to set the direction to TRUE
        builders[i].getDirections().set(step - 1);
      }
    }
    // create the triple pattern of this step with the o -> s direction
    localBuilder.delete(0, localBuilder.length());
    addPatternWithFilter(step, oVariable, sVariable, localBuilder);
    // go through the builders and add the pattern in this direction to all that do not fit to the
    // mask of this step
    for (int i = 0; i < builders.length; ++i) {
      if ((i & stepMask) == 0) {
        builders[i].getQueryBuilder().append(localBuilder);
        // The triple pattern is in o -> s direction, so we have leave the direction as FALSE
      }
    }

    // Clean up local builder so that we can reuse it
    localBuilder.delete(0, localBuilder.length());
    // Add filters for the intermediate nodes to avoid loops, i.e., node of this step should not be
    // a node of another step
    if (step > 1) {
      if (step > 2) {
        // Add intermediate node != subject
        localBuilder.append(" FILTER (");
        localBuilder.append(sVariable);
        localBuilder.append(" != <");
        localBuilder.append(subject.getURI());
        localBuilder.append(">) \n");
      }
      for (int i = (step + 1); i < length; ++i) {
        // Add intermediate node != intermediate node
        localBuilder.append(" FILTER (");
        localBuilder.append(sVariable);
        localBuilder.append(" != ?" + INTERMEDIATE_NODE_VARIABLE_NAME);
        localBuilder.append(i);
        localBuilder.append(") \n");
      }
      if (step < length) {
        // Add intermediate node != object
        localBuilder.append(" FILTER (");
        localBuilder.append(sVariable);
        localBuilder.append(" != <");
        localBuilder.append(object.getURI());
        localBuilder.append(">) \n");
      }
    }
    // If this is not the first select, we need to end with an additional bracket
    if (step > 1) {
      localBuilder.append(" } ");
    }
    localBuilder.append(" } ");
    // Add the filters to all queries
    for (int i = 0; i < builders.length; ++i) {
      builders[i].getQueryBuilder().append(localBuilder);
    }
  }

}
