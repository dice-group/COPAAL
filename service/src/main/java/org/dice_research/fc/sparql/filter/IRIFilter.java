package org.dice_research.fc.sparql.filter;

/**
 * Instances of this interface can add a FILTER condition to a SPARQL query
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface IRIFilter {

  /**
   * Adds a filter to the SPARQL query that is created using the given query builder.
   * 
   * @param variable the name of the variable for which the filter should hold (only the name
   *        without the preceding '?')
   * @param queryBuilder the builder that is used to build the query
   */
  void addFilter(String variableName, StringBuilder queryBuilder);
}
