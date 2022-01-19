package org.dice_research.fc.paths.scorer.count.decorate;

import org.dice_research.fc.paths.scorer.ICountRetriever;

/**
 * An interface implementing the decorator pattern for {@link ICountRetriever}.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface ICountRetrieverDecorator extends ICountRetriever {

  /**
   * Returns the decorated instance of {@link ICountRetriever}.
   * 
   * @return the decorated instance
   */
  ICountRetriever getDecorated();
}
