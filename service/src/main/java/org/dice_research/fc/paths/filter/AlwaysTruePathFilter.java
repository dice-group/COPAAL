package org.dice_research.fc.paths.filter;

import org.dice_research.fc.data.QRestrictedPath;

/**
 * A simple filter that always returns {@code true}.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class AlwaysTruePathFilter implements IPathFilter {

  @Override
  public boolean test(QRestrictedPath path) {
    return true;
  }

}
