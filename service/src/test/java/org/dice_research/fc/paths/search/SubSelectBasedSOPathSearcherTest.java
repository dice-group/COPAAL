package org.dice_research.fc.paths.search;

import java.util.Collection;
import java.util.Set;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.IPathSearcher;
import org.dice_research.fc.sparql.filter.IRIFilter;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class SubSelectBasedSOPathSearcherTest extends SPARQLBasedSOPathSearcherTest {

  public SubSelectBasedSOPathSearcherTest(Dataset dataset, Integer maximumLength,
      Collection<IRIFilter> propertyFilter, Resource subject, Predicate predicate, Resource object,
      Set<QRestrictedPath> expectedPaths) {
    super(dataset, maximumLength, propertyFilter, subject, predicate, object, expectedPaths);
  }

  public IPathSearcher createSearcher() {
    return new SubSelectBasedSOPathSearcher(qef, maximumLength, propertyFilter);
  }
}
