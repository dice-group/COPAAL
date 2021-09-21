package org.dice_research.fc.paths.scorer.count.decorate;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.paths.scorer.count.PropPathBasedPairCountRetriever;
import org.dice_research.fc.paths.scorer.count.max.DefaultMaxCounter;

public class CachingSamplingCountRetrieverDecoratorTest
    extends SamplingCountRetrieverDecoratorTest {

  @Override
  protected ICountRetriever createCountRetriever(long seed, QueryExecutionFactory qef) {
    return new CachingSamplingCountRetrieverDecorator(
        new PropPathBasedPairCountRetriever(qef, new DefaultMaxCounter(qef)), seed, 3, qef);
  }

}
