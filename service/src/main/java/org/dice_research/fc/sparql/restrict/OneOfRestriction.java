package org.dice_research.fc.sparql.restrict;

import java.util.Collection;

/**
 * This restriction represents the {@code VALUES} keyword from SPARQL. It means that a variable has
 * to have one of the listed values (IRIs).
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class OneOfRestriction implements ITypeRestriction {
  
  /**
   * The values that are allowed.
   */
  protected Collection<String> values;

  public OneOfRestriction(Collection<String> values) {
    this.values = values;
  }

  @Override
  public void addRestrictionToQuery(String variable, StringBuilder builder) {
    builder.append("VALUES ?");
    builder.append(variable);
    builder.append(" {");
    for (String value : values) {
      builder.append(" <");
      builder.append(value);
      builder.append('>');
    }
    builder.append(" } .");
  }

  public Collection<String> getValues() {
    return values;
  }

  public int getNumberOfValues() {
    return values.size();
  }

}
