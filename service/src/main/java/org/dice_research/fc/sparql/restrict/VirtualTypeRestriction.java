package org.dice_research.fc.sparql.restrict;

/**
 * This restriction represents a virtual type, i.e., the subjects (or objects) are identified based
 * on the existence of a triple that involves them and a given property.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class VirtualTypeRestriction implements ITypeRestriction {

  private static final String VARIABLE_NAME = "random";

  private boolean variableIsSubject;
  private String propertyIri;

  public VirtualTypeRestriction(boolean variableIsSubject, String propertyIri) {
    this.variableIsSubject = variableIsSubject;
    this.propertyIri = propertyIri;
  }

  @Override
  public void addRestrictionToQuery(String variable, StringBuilder builder) {
    builder.append("FILTER EXISTS { ");
    if (variableIsSubject) {
      builder.append(" ?");
      builder.append(variable);
    } else {
      builder.append(" _:");
      builder.append(VARIABLE_NAME);
      builder.append("s");
    }
    builder.append(" <");
    builder.append(propertyIri);
    builder.append(">");
    if (variableIsSubject) {
      builder.append(" _:");
      builder.append(VARIABLE_NAME);
      builder.append("o");
    } else {
      builder.append(" ?");
      builder.append(variable);
    }
    builder.append(" . } \n");
  }

  @Override
  public boolean isEmpty() {
    // The restriction is empty if non of the flags is true
    return false;
  }

  @Override
  public Object getRestriction() {
    return this.propertyIri;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((propertyIri == null) ? 0 : propertyIri.hashCode());
    result = prime * result + (variableIsSubject ? 1231 : 1237);
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
    VirtualTypeRestriction other = (VirtualTypeRestriction) obj;
    if (propertyIri == null) {
      if (other.propertyIri != null)
        return false;
    } else if (!propertyIri.equals(other.propertyIri)) {
      return false;
    }

    return (variableIsSubject == other.variableIsSubject);
  }

  @Override
  public boolean usesPropertyAsRestriction() {
    return true;
  }

}
