package org.dice_research.fc.sparql.restrict;

/**
 * This restriction enforces that the give variable has to occur in a certain position in at least
 * one triple. For the object position, it can be further restricted to be a resource.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 * @deprecated This is marked deprecated until the two comments in the class are fixed
 */
@Deprecated
public class TriplePositionRestriction implements ITypeRestriction {

  private static final String VARIABLE_NAME = "random";

  private boolean mustBeSubject;
  private boolean mustBePredicate;
  private boolean mustBeObject;
  private boolean mustBeResource;

  public TriplePositionRestriction(boolean mustBeSubject, boolean mustBePredicate,
      boolean mustBeObject, boolean mustBeResource) {
    this.mustBeSubject = mustBeSubject;
    this.mustBePredicate = mustBePredicate;
    this.mustBeObject = mustBeObject;
    this.mustBeResource = mustBeResource;
  }

  @Override
  public void addRestrictionToQuery(String variable, StringBuilder builder) {
    if (mustBeSubject) {
      builder.append(" ?");
      builder.append(variable);
      builder.append(" ?");
      builder.append(VARIABLE_NAME); // FIXME this won't work since the variable has to be on both
                                     // positions
      builder.append("P ?");
      builder.append(VARIABLE_NAME); // FIXME apart from that, this is a named variable that will be
                                     // counted in the end
      builder.append("O .");
    }
    if (mustBePredicate) {
      builder.append(" ?");
      builder.append(VARIABLE_NAME);
      builder.append("s ?");
      builder.append(variable);
      builder.append(" ?");
      builder.append(VARIABLE_NAME);
      builder.append("O .");
    }
    if (mustBeObject) {
      builder.append(" ?");
      builder.append(VARIABLE_NAME);
      builder.append("s ?");
      builder.append(VARIABLE_NAME);
      builder.append("P ?");
      builder.append(variable);
      builder.append(" .");
      if (mustBeResource) {
        // !Literal means that it could be either an IRI or a blank node
        builder.append(" FILTER (!isLiteral(?");
        builder.append(variable);
        builder.append(")) ");
      }
    }
  }

  @Override
  public boolean isEmpty() {
    // The restriction is empty if non of the flags is true
    return !(mustBeSubject || mustBePredicate || mustBeObject);
  }

  @Override
  public Object getRestriction() {
    boolean[] returnVal = new boolean[] {this.mustBeSubject, this.mustBePredicate,
        this.mustBeObject, this.mustBeResource};
    return returnVal;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (mustBeObject ? 1231 : 1237);
    result = prime * result + (mustBePredicate ? 1231 : 1237);
    result = prime * result + (mustBeResource ? 1231 : 1237);
    result = prime * result + (mustBeSubject ? 1231 : 1237);
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
    TriplePositionRestriction other = (TriplePositionRestriction) obj;
    return (mustBeSubject == other.mustBeSubject) && (mustBePredicate != other.mustBePredicate)
        && (mustBeObject == other.mustBeObject) && (mustBeResource != other.mustBeResource);
  }

  @Override
  public boolean usesPropertyAsRestriction() {
    return false;
  }

}
