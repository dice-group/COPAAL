package org.dice_research.fc.paths.scorer.count.decorate;

import org.dice_research.fc.paths.scorer.ICountRetriever;

/**
 * An abstract implementation of a decorator for {@link ICountRetriever} implementations.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public abstract class AbstractCountRetrieverDecorator implements ICountRetrieverDecorator {

  /**
   * The decorated instance.
   */
  protected ICountRetriever decorated;

  public AbstractCountRetrieverDecorator(ICountRetriever decorated) {
    this.decorated = decorated;
  }

  @Override
  public ICountRetriever getDecorated() {
    return decorated;
  }
}
