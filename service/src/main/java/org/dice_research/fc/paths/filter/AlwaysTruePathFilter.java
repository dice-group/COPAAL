package org.dice_research.fc.paths.filter;

import org.dice.FactCheck.Corraborative.UIResult.Path;

/**
 * A simple filter that always returns {@code true}.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class AlwaysTruePathFilter implements IPathFilter {

  @Override
  public boolean test(Path t) {
    return true;
  }

}
