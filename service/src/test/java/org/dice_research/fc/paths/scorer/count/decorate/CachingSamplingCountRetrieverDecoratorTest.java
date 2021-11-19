package org.dice_research.fc.paths.scorer.count.decorate;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.paths.scorer.count.PairCountRetriever;
import org.dice_research.fc.paths.scorer.count.max.DefaultMaxCounter;

public class CachingSamplingCountRetrieverDecoratorTest
    extends SamplingCountRetrieverDecoratorTest {

  @Override
  protected ICountRetriever createCountRetriever(long seed, QueryExecutionFactory qef) {
    return new CachingSamplingCountRetrieverDecorator(
        new PairCountRetriever(qef, new DefaultMaxCounter(qef, false, null)), seed, 3, qef);
  }

}
