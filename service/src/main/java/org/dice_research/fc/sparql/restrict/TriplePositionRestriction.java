package org.dice_research.fc.sparql.restrict;

/**
 * This restriction enforces that the give variable has to occur in a certain position in at least
 * one triple. For the object position, it can be further restricted to be a resource.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class TriplePositionRestriction implements ITypeRestriction {

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
      builder.append(" [] [] .");
    }
    if (mustBePredicate) {
      builder.append(" [] ?");
      builder.append(variable);
      builder.append(" [] .");
    }
    if (mustBeObject) {
      builder.append(" [] [] ?");
      builder.append(variable);
      builder.append(" .");
      if (mustBeResource) {
        // !Literal means that it could be either an IRI or a blank node
        builder.append(" FILTER ()!isLiteral(?");
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

}
