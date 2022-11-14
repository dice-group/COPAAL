package org.dice_research.fc.sparql.filter;

/**
 * An IRI filter that excludes all IRIs that equal one of the given IRIs.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class EqualsFilter implements IRIFilter {

  /**
   * The excluded IRIs.
   */
  protected String[] excludedIRIs;

  /**
   * Constructor.
   * 
   * @param excludedIRIs the excluded IRIs
   */
  public EqualsFilter(String[] excludedIRIs) {
    super();
    this.excludedIRIs = excludedIRIs;
  }

  @Override
  public void addFilter(String variableName, StringBuilder queryBuilder) {
    if (excludedIRIs.length > 0) {
      queryBuilder.append(" FILTER( ");
      for (int i = 0; i < excludedIRIs.length; ++i) {
        if (i > 0) {
          queryBuilder.append("&&");
        }
        queryBuilder.append(" ?");
        queryBuilder.append(variableName);
        queryBuilder.append(" != <");
        queryBuilder.append(excludedIRIs[i]);
        queryBuilder.append("> ");
      }
      queryBuilder.append(")\n");
    }
  }
}
