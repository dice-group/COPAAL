package org.dice_research.fc.sparql.restrict;

/**
 * This interface is implemented by classes that express a type-based restriction for a variable in
 * a SPARQL query.
 * 
 * Instances of this interface <b>must</b> override the {@link #equals(Object)} and
 * {@link #hashCode()} methods to ensure that they can be used within collections.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface ITypeRestriction {

  /**
   * When this method is called, the restriction is added to the SPARQL query. It is assumed that
   * the given variable name represents the node for which the type should be restricted and that
   * the given {@link StringBuilder} contains the SPARQL query in a state in which the restriction
   * can be added.
   * 
   * @param variable the variable for which the restriction should be generated (Excluding the
   *        {@code '?'} symbol of SPARQL
   * @param builder the unfinished SPARQL query to which the restriction should be added
   */
  public void addRestrictionToQuery(String variable, StringBuilder builder);

  /**
   * Returns {@code true} if adding the restriction to a query will not change the content of the
   * query.
   *
   * @return {@code true} if the restriction has not effect, else {@code false}
   */
  public boolean isEmpty();

  /**
   *
   * @return this return the restriction as object while the restriction may has different type in
   *         different implementations
   */
  public Object getRestriction();

  /**
   * This method should return true in case the restriction simply uses the property of the
   * predicate as restriction, e.g., it adds a triple ?s <property> _:someNode. This method is used
   * to speed up the SPARQL queries that already have this restriction within their other triples.
   * 
   * @return
   */
  boolean usesPropertyAsRestriction();
}
