package org.dice_research.fc.sparql.filter;

/**
 * An IRIFilter that relies on the name space of the IRIs. Note that the implementation relies on a
 * simple string comparison without an exact extraction of the IRI's name space.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class NamespaceFilter implements IRIFilter {

  /**
   * If this flag is {@code true} the filter will exclude IRIs that match the given name space.
   * Else, it will exclude all other IRIs.
   */
  protected boolean excludeMatch;
  /**
   * The name space that will be used to filter.
   */
  protected String namespace;

  /**
   * Constructor that assumes that IRIs with the given name space should be excluded.
   * 
   * @param namespace the name space that will be used to filter
   */
  public NamespaceFilter(String namespace) {
    this(namespace, true);
  }

  /**
   * Constructor.
   * 
   * @param namespace the name space that will be used to filter
   * @param excludeMatch if this flag is {@code true} the filter will exclude IRIs that match the
   *        given name space. Else, it will exclude all other IRIs.
   */
  public NamespaceFilter(String namespace, boolean excludeMatch) {
    super();
    this.excludeMatch = excludeMatch;
    this.namespace = namespace;
  }

  @Override
  public void addFilter(String variableName, StringBuilder queryBuilder) {
    if (excludeMatch) {
      queryBuilder.append(" FILTER(!strstarts(str(?");
    } else {
      queryBuilder.append(" FILTER(strstarts(str(?");
    }
    queryBuilder.append(variableName);
    queryBuilder.append("),\"");
    queryBuilder.append(namespace);
    queryBuilder.append("\")) \n");
  }

}
