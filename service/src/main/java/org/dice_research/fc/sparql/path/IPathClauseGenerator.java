package org.dice_research.fc.sparql.path;

import org.dice_research.fc.data.QRestrictedPath;

/**
 * Classes that implement this interface offer methods to transform a given q-restricted path into a
 * String that can be used within a SPARQL WHERE clause.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface IPathClauseGenerator {

  public static final String DEFAULT_INTERMEDIATE_VARIABLE_NAME = "in";

  /**
   * Transforms the given q-restricted path into a String representation for a SPARQL query using
   * the given variable names and adds it to the given builder. Note that the given variable name
   * for the intermediate nodes will be extended using IDs to distinguish between different
   * intermediate nodes.
   * 
   * @param path the q-restricted path that should be transformed
   * @param queryBuilder the builder that is used to create the SPARQL query and to which the path
   *        will be added
   * @param subjectVariable the name of the subject variable (i.e. the node at the beginning of the
   *        path)
   * @param objectVariable the name of the object variable (i.e. the node at the end of the path)
   * @param intermediateName the name of the intermediate nodes (will be extended using numerical
   *        IDs)
   */
  public void addPath(QRestrictedPath path, StringBuilder queryBuilder, String subjectVariable,
      String objectVariable, String intermediateName);

  /**
   * Transforms the given q-restricted path into a String representation for a SPARQL query using
   * the given variable names and adds it to the given builder. Note that it will use
   * {@link #DEFAULT_INTERMEDIATE_VARIABLE_NAME} for the variable name of the intermediate nodes.
   * 
   * @param path the q-restricted path that should be transformed
   * @param queryBuilder the builder that is used to create the SPARQL query and to which the path
   *        will be added
   * @param subjectVariable the name of the subject variable (i.e. the node at the beginning of the
   *        path)
   * @param objectVariable the name of the object variable (i.e. the node at the end of the path)
   */
  public default void addPath(QRestrictedPath path, StringBuilder queryBuilder,
      String subjectVariable, String objectVariable) {
    addPath(path, queryBuilder, subjectVariable, objectVariable,
        DEFAULT_INTERMEDIATE_VARIABLE_NAME);
  }
}
