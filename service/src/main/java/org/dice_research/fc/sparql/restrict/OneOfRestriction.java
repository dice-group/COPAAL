package org.dice_research.fc.sparql.restrict;

import java.util.Collection;

public class OneOfRestriction implements ITypeRestriction {

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
