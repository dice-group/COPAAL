package org.dice_research.fc.paths.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.IPathSearcher;
import org.dice_research.fc.sparql.filter.IRIFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the path searcher of COPAAL as described in the paper of Syed et al. (2019). It is
 * based on a search for paths connecting s and o with a maximum length.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class SPARQLBasedSOPathSearcher implements IPathSearcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(SPARQLBasedSOPathSearcher.class);

  /**
   * The variable name of the properties (an ID will be added to distinguish the properties of the
   * single positions).
   */
  protected static final String PROPERTY_VARIABLE_NAME = "p";
  /**
   * The variable name of the intermediate nodes between s and o on the path (an ID will be added to
   * distinguish the nodes).
   */
  protected static final String INTERMEDIATE_NODE_VARIABLE_NAME = "x";

  /**
   * The query execution factory that will be used to execute all search queries
   */
  protected QueryExecutionFactory qef;
  /**
   * The maximum length of a generated q-restricted path (as number of properties).
   */
  protected int maximumLength;
  /**
   * The filters for the single properties of the single positions in the path.
   */
  protected String[] propertyFilter;
  /**
   * The variables of the properties within the search queries.
   */
  protected String[] propertyVariables;

  public SPARQLBasedSOPathSearcher(QueryExecutionFactory qef, int maximumLength,
      Collection<IRIFilter> propertyFilter) {
    super();
    this.qef = qef;
    this.maximumLength = maximumLength;

    // We can create the names of the property variables
    propertyVariables = new String[maximumLength];
    for (int i = 0; i < propertyVariables.length; ++i) {
      propertyVariables[i] = PROPERTY_VARIABLE_NAME + (i + 1);
    }
    // We can already generate all the filter statements for our predicates
    this.propertyFilter = new String[maximumLength];
    if (propertyFilter.size() > 0) {
      StringBuilder filterBuilder = new StringBuilder();
      for (int i = 0; i < this.propertyFilter.length; ++i) {
        for (IRIFilter filter : propertyFilter) {
          filter.addFilter(propertyVariables[i], filterBuilder);
        }
        this.propertyFilter[i] = filterBuilder.toString();
        filterBuilder.delete(0, filterBuilder.length());
      }
    } else {
      // The filters are not needed
      Arrays.fill(this.propertyFilter, "");
    }
  }

  @Override
  public Collection<QRestrictedPath> search(Resource subject, Predicate predicate,
      Resource object) {
    // Generate queries
    List<SearchQuery> queries = new ArrayList<SearchQuery>();
    generateShortSearchQuery(subject, predicate, object, queries);
    for (int i = 2; i <= maximumLength; ++i) {
      generateSearchQueries(i, subject, predicate, object, queries);
    }
    LOGGER.info("Generated {} queries for the triple ({}, {}, {})", queries.size(), subject.getURI(),
        predicate.getProperty().getURI(), object.getURI());
    List<QRestrictedPath> paths = searchPaths(queries);
    LOGGER.info("Found {} paths for the triple ({}, {}, {})", paths.size(), subject.getURI(),
        predicate.getProperty().getURI(), object.getURI());
    return paths;
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
   * This method adds two things to the given query builder: 1) a basic graph pattern with the given
   * subject, object and a property with the given id, and 2) the property filters that will exclude
   * the filtered properties defined during the creation of this class.
   * 
   * @param propertyId the ID of the property
   * @param subject the subject of the basic graph pattern that should be added
   * @param object the object of the basic graph pattern that should be added
   * @param queryBuilder the query builder to which the pattern and the filter should be added
   */
  protected void addPatternWithFilter(int propertyId, String subject, String object,
      StringBuilder queryBuilder) {
    queryBuilder.append(subject);
    queryBuilder.append(" ?");
    queryBuilder.append(propertyVariables[propertyId - 1]);
    queryBuilder.append(' ');
    queryBuilder.append(object);
    queryBuilder.append(" . ");
    queryBuilder.append(propertyFilter[propertyId - 1]);
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

  /**
   * This method takes the previously generated search queries and searches for the paths within the
   * reference knowledge graph.
   * 
   * @param queries the previously generated queries
   * @return all q-restricted paths that could be selected using the given queries.
   */
  protected List<QRestrictedPath> searchPaths(List<SearchQuery> queries) {
    List<QRestrictedPath> paths = new ArrayList<QRestrictedPath>();
    QueryExecution qe;
    QuerySolution qs;
    ResultSet rs;
    BitSet directions;
    List<Pair<Property, Boolean>> pathElements;
    for (SearchQuery query : queries) {
      LOGGER.info("Executing query \"{}\"", query.getQuery());
      qe = qef.createQueryExecution(query.getQuery());
      directions = query.getDirections();
      rs = qe.execSelect();
      int count = 0;
      while (rs.hasNext()) {
        qs = rs.next();
        // collect the properties of the path and their direction
        pathElements = new ArrayList<>(query.getLength());
        for (int i = 0; i < query.getLength(); ++i) {
          pathElements.add(new Pair<Property, Boolean>(
              ResourceFactory.createProperty(qs.getResource(propertyVariables[i]).getURI()),
              directions.get(i)));
        }
        paths.add(new QRestrictedPath(pathElements));
        ++count;
      }
      LOGGER.info("Got {} paths from the query", count);
    }
    return paths;
  }

}
