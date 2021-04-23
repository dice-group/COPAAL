package org.dice_research.fc.paths.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.IPathSearcher;
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

  protected QueryExecutionFactory qef;
  protected int maximumLength;
  protected String[] propertyFilter;

  public SPARQLBasedSOPathSearcher(QueryExecutionFactory qef, int maximumLength,
      Collection<String> filteredProperties) {
    super();
    this.qef = qef;
    this.maximumLength = maximumLength;

    // We can already generate all the filter statements for our predicates
    propertyFilter = new String[maximumLength];
    if (filteredProperties.size() > 0) {
      StringBuilder filterBuilder = new StringBuilder();
      boolean firstProp;
      for (int i = 1; i <= propertyFilter.length; ++i) {
        filterBuilder.append("FILTER( ");
        firstProp = true;
        for (String property : filteredProperties) {
          if (firstProp) {
            firstProp = false;
          } else {
            filterBuilder.append("&&");
          }
          filterBuilder.append(" ?p");
          filterBuilder.append(i);
          filterBuilder.append(" != <");
          filterBuilder.append(property);
          filterBuilder.append("> ");
        }
        filterBuilder.append(')');
        propertyFilter[i - 1] = filterBuilder.toString();
        filterBuilder.delete(0, filterBuilder.length());
      }
    } else {
      // The filters are not needed
      Arrays.fill(propertyFilter, "");
    }
  }

  @Override
  public Collection<QRestrictedPath> search(Resource subject, Predicate predicate,
      Resource object) {
    // Generate queries
    List<SearchQuery> queries = new ArrayList<SearchQuery>();
    for (int i = 2; i <= maximumLength; ++i) {
      generateSearchQueries(i, subject, predicate, object, queries);
    }
    List<QRestrictedPath> paths = searchPaths(queries);
    return paths;
  }

  /**
   * Generates 2^{length} queries.
   * 
   * @param length
   * @param subject
   * @param predicate
   * @param object
   * @return
   */
  protected void generateShortSearchQuery(Resource subject, Predicate predicate, Resource object,
      List<SearchQuery> queries) {
    // s p1 o
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT ?p1 WHERE { ?s ?p1 ?o . ");
    queryBuilder.append(propertyFilter[0]);
    queryBuilder.append(" }");
    BitSet directions = new BitSet(1);
    directions.set(0);
    queries.add(new SearchQuery(queryBuilder.toString(), directions));
    // o p1 s
    queryBuilder.delete(0, queryBuilder.length());
    queryBuilder.append("SELECT ?p1 WHERE { ?o ?p1 ?s . ");
    queryBuilder.append(propertyFilter[0]);
    queryBuilder.append(" }");
    directions = new BitSet(1);
    queries.add(new SearchQuery(queryBuilder.toString(), directions));
  }

  protected void addPatternWithFilter(int propertyId, String subject, String object,
      StringBuilder queryBuilder) {
    queryBuilder.append(subject);
    queryBuilder.append(" ?p");
    queryBuilder.append(propertyId);
    queryBuilder.append(' ');
    queryBuilder.append(object);
    queryBuilder.append(" . ");
    queryBuilder.append(propertyFilter[propertyId - 1]);
  }

  /**
   * Generates 2^{length} queries.
   * 
   * @param length
   * @param subject
   * @param predicate
   * @param object
   * @return
   */
  protected void generateSearchQueries(int length, Resource subject, Predicate predicate,
      Resource object, List<SearchQuery> queries) {
    SearchQueryBuilder[] builders = new SearchQueryBuilder[1 << length];
    builders[0] = new SearchQueryBuilder(length);
    generateSearchQuery_Recursion(1, length, subject, predicate, object, builders);
    for (int i = 0; i < builders.length; ++i) {
      queries.add(builders[i].build());
    }
  }

  protected void generateSearchQuery_Recursion(int step, int length, Resource subject,
      Predicate predicate, Resource object, SearchQueryBuilder[] builders) {
    // prepare some stuff
    StringBuilder localBuilder = new StringBuilder();
    String sVariable = step == 1 ? subject.getURI() : ("?x" + (step - 1));
    String oVariable = step == length ? object.getURI() : ("?x" + step);
    // Copy all parts created so far (only the lower half has been created
    int lowerHalfId = 1 << (step - 1);
    int upperHalfId = lowerHalfId << 1;
    for (int i = 0; i < lowerHalfId; ++i) {
      builders[i + lowerHalfId] = new SearchQueryBuilder(builders[i]);
    }
    // Create SELECT part
    localBuilder.append("SELECT ");
    for (int i = step; i <= length; ++i) {
      localBuilder.append("?p");
      localBuilder.append(i);
    }
    // If this is not the first select, we need to select the intermediate node of this step
    if (step > 1) {
      localBuilder.append(" ");
      localBuilder.append(step - 1);
    }
    localBuilder.append(" WHERE { ");
    
    // FIXME RECURSION SHOULD COME HERE!
    
    // store the length of the head, before we add the triple pattern
    int firstPartLength = localBuilder.length();
    addPatternWithFilter(step, sVariable, oVariable, localBuilder);
    // go through the lower half and add the content of the local builder
    for (int i = 0; i < lowerHalfId; ++i) {
      builders[i].getQueryBuilder().append(localBuilder);
      // The triple pattern is in s -> o direction, so we have to set the direction to TRUE
      builders[i].getDirections().set(step - 1);
    }
    // Set the local builder back
    localBuilder.delete(firstPartLength, localBuilder.length());
    addPatternWithFilter(step, oVariable, sVariable, localBuilder);
    // go through the lower half and add the content of the local builder
    for (int i = 0; i < lowerHalfId; ++i) {
      builders[i].getQueryBuilder().append(localBuilder);
      // The triple pattern is in s -> o direction, so we have to set the direction to TRUE
      builders[i].getDirections().set(step - 1);
    }
    // go through the upper half and add the content of the local builder
    for (int i = lowerHalfId; i < upperHalfId; ++i) {
      builders[i].getQueryBuilder().append(localBuilder);
    }
    
    // FIXME ADD FILTERS THAT ENSURE THAT THERE ARE NO CYCLES!
  }


  private List<QRestrictedPath> searchPaths(List<SearchQuery> queries) {
    // TODO Auto-generated method stub
    return null;
  }


}
