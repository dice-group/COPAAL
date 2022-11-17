package org.dice_research.fc.paths.scorer.count.decorate;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.paths.scorer.count.PairCountRetriever;
import org.dice_research.fc.paths.scorer.count.max.DefaultMaxCounter;
import org.dice_research.fc.sparql.query.ListBaseQueryValidator;

import java.util.ArrayList;

public class CachingSamplingCountRetrieverDecoratorTest
    extends SamplingCountRetrieverDecoratorTest {

  @Override
  protected ICountRetriever createCountRetriever(long seed, QueryExecutionFactory qef) {
    return new CachingSamplingCountRetrieverDecorator(
        new PairCountRetriever(qef, new DefaultMaxCounter(qef), new ListBaseQueryValidator(new ArrayList<>())), seed, 3, qef);
  }

}
