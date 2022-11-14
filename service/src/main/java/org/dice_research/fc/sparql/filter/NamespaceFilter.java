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
  protected String namespaces[];

  /**
   * Constructor that assumes that IRIs with the given name space should be excluded.
   * 
   * @param namespace the name space that will be used to filter
   */
  public NamespaceFilter(String namespace) {
    this(new String[] {namespace}, true);
  }

  /**
   * Constructor that assumes that IRIs with the given name spaces should be excluded.
   * 
   * @param namespace the name space that will be used to filter
   */
  public NamespaceFilter(String[] namespaces) {
    this(namespaces, true);
  }

  /**
   * Constructor.
   * 
   * @param namespace the name space that will be used to filter
   * @param excludeMatch if this flag is {@code true} the filter will exclude IRIs that match the
   *        given name space. Else, it will exclude all other IRIs.
   */
  public NamespaceFilter(String namespace, boolean excludeMatch) {
    this(new String[] {namespace}, excludeMatch);
  }

  /**
   * Constructor.
   * 
   * @param namespaces the name spaces that will be used to filter
   * @param excludeMatch if this flag is {@code true} the filter will exclude IRIs that match the
   *        given name space. Else, it will exclude all other IRIs.
   */
  public NamespaceFilter(String[] namespaces, boolean excludeMatch) {
    super();
    this.excludeMatch = excludeMatch;
    this.namespaces = namespaces;
  }

  @Override
  public void addFilter(String variableName, StringBuilder queryBuilder) {
    if(namespaces.length>0) {
      if (excludeMatch) {
        queryBuilder.append(" FILTER(!");
      } else {
        queryBuilder.append(" FILTER(");
      }
      boolean first = true;
      for (int i = 0; i < namespaces.length; ++i) {
        if (first) {
          first = false;
        } else {
          queryBuilder.append(excludeMatch ? " && " : " || "); // TODO!!!!
        }
        queryBuilder.append("strstarts(str(?");
        queryBuilder.append(variableName);
        queryBuilder.append("),\"");
        queryBuilder.append(namespaces[i]);
        queryBuilder.append("\")");
      }
      queryBuilder.append(") \n");
    }
  }

  public boolean isExcludeMatch() {
    return excludeMatch;
  }

  public String[] getNamespaces() {
    return namespaces;
  }
  
}
