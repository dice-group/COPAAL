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
    if (!values.isEmpty()) {
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
  }

  public Collection<String> getValues() {
    return values;
  }

  public int getNumberOfValues() {
    return values.size();
  }

  @Override
  public boolean isEmpty() {
    return !values.isEmpty();
  }

  @Override
  public Object getRestriction() {
    return this.values;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((values == null) ? 0 : values.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    OneOfRestriction other = (OneOfRestriction) obj;
    if (values == null) {
      if (other.values != null)
        return false;
    } else if (!values.equals(other.values))
      return false;
    return true;
  }

  @Override
  public boolean usesPropertyAsRestriction() {
    return false;
  }

}
