package org.dice_research.fc.paths.filter;

import java.util.function.DoublePredicate;

public class SimpleScoreFilter implements IScoreFilter {

  protected DoublePredicate predicate;

  public SimpleScoreFilter(DoublePredicate predicate) {
    this.predicate = predicate;
  }

  @Override
  public boolean test(double value) {
    return predicate.test(value);
  }

}
