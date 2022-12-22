package org.dice_research.fc.paths;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.fc.IFactChecker;
import org.dice_research.fc.data.FactCheckingResult;
import org.dice_research.fc.paths.scorer.NPMIBasedScorer;
import org.dice_research.fc.paths.scorer.count.ApproximatingCountRetriever;
import org.dice_research.fc.paths.scorer.count.max.DefaultMaxCounter;
import org.dice_research.fc.paths.search.SPARQLBasedSOPathSearcher;
import org.dice_research.fc.sparql.filter.EqualsFilter;
import org.dice_research.fc.sparql.filter.IRIFilter;
import org.dice_research.fc.sparql.query.ListBaseQueryValidator;
import org.dice_research.fc.sum.CubicMeanSummarist;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PathBasedFactCheckerTest {

  private QueryExecutionFactory qef;
  private int maximumLength;
  private Collection<IRIFilter> propertyFilter;
  private Statement triple;
  private double expectedScore;

  public PathBasedFactCheckerTest(QueryExecutionFactory qef, int maximumLength,
      Collection<IRIFilter> propertyFilter, Statement triple, double expectedScore) {
    this.qef = qef;
    this.maximumLength = maximumLength;
    this.propertyFilter = propertyFilter;
    this.triple = triple;
    this.expectedScore = expectedScore;
  }

  @Test
  public void test() {
    IFactChecker factChecker = new PathBasedFactChecker(new PredicateFactory(qef),
        new SPARQLBasedSOPathSearcher(qef, maximumLength, propertyFilter),
        new NPMIBasedScorer(new ApproximatingCountRetriever(qef, new DefaultMaxCounter(qef),new ListBaseQueryValidator(new ArrayList<>()))),
        new CubicMeanSummarist(),0.0,null);
    FactCheckingResult result = factChecker.check(triple);
    Assert.assertEquals(expectedScore, result.getVeracityValue(), 0.001);
  }

  @Parameters
  public static Collection<Object[]> data() {
    List<Object[]> testConfigs = new ArrayList<Object[]>();

    Model model = ModelFactory.createDefaultModel();
    model.read("test_graph.nt");
    QueryExecutionFactory qef = new QueryExecutionFactoryModel(model);

    String[] filteredProps = {RDF.type.getURI()};

    List<IRIFilter> filter = new ArrayList<IRIFilter>();
    filter.add(new EqualsFilter(filteredProps));

    Statement triple =
        ResourceFactory.createStatement(ResourceFactory.createResource("http://www.example.org/A"),
            ResourceFactory.createProperty("http://www.example.org/P1"),
            ResourceFactory.createResource("http://www.example.org/D"));

    // Same fact, different lengths
    testConfigs.add(new Object[] {qef, 1, filter, triple, 0});
    testConfigs.add(new Object[] {qef, 2, filter, triple, 0.222});
    testConfigs.add(new Object[] {qef, 3, filter, triple, 0.171});
    return testConfigs;
  }

}
