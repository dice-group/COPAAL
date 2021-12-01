package org.dice_research.fc.paths.scorer.count.max;

import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.paths.scorer.ICountRetriever;

public interface IMaxCounter {

  /**
   * Derives a maximum count that can be used to create probabilities from the counts retrieved by
   * the other methods of the {@link ICountRetriever} interface.
   * 
   * @param predicate the predicate for which the counts should be normalized
   * @return a maximum count
   */
  public abstract long deriveMaxCount(Predicate predicate);
}
