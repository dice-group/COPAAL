package org.dice_research.fc.paths.filter;

/**
 * A simple filter that always returns {@code true}.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class AlwaysTrueScoreFilter implements IScoreFilter {

  @Override
  public boolean test(double value) {
    return true;
  }

}
