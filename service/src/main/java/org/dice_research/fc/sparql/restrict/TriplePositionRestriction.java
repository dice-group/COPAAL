package org.dice_research.fc.sparql.restrict;

/**
 * This restriction enforces that the give variable has to occur in a certain position in at least
 * one triple.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class TriplePositionRestriction implements ITypeRestriction {

  private boolean mustBeSubject;
  private boolean mustBePredicate;
  private boolean mustBeObject;

  public TriplePositionRestriction(boolean mustBeSubject, boolean mustBePredicate,
      boolean mustBeObject) {
    this.mustBeSubject = mustBeSubject;
    this.mustBePredicate = mustBePredicate;
    this.mustBeObject = mustBeObject;
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
    }
  }

  @Override
  public boolean isEmpty() {
    // The restriction is empty if non of the flags is true
    return !(mustBeSubject || mustBePredicate || mustBeObject);
  }

}
