package org.dice_research.fc.paths.scorer.count.max;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.EmptyPredicateFactory;
import org.dice_research.fc.paths.FactPreprocessor;
import org.dice_research.fc.paths.PredicateFactory;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.paths.scorer.NPMIBasedScorer;
import org.dice_research.fc.paths.scorer.count.ApproximatingCountRetriever;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Testing <http://dbpedia.org/ontology/birthPlace>/<http://dbpedia.org/ontology/country> with and
 * without virtual type configuration.
 * 
 * @author Alexandra Silva
 *
 */
@RunWith(Parameterized.class)
public class MaxCounterTest {
  private Statement fact = ResourceFactory.createStatement(
      ResourceFactory.createResource("http://dbpedia.org/resource/Bill_Gates"),
      ResourceFactory.createProperty("http://dbpedia.org/ontology/birthPlace"),
      ResourceFactory.createResource("http://dbpedia.org/resource/United_States"));

  private Property[] properties =
      {ResourceFactory.createProperty("http://dbpedia.org/ontology/birthPlace"),
          ResourceFactory.createProperty("http://dbpedia.org/ontology/country")};
  private boolean[] directions = {true, true};

  private static QueryExecutionFactory qef =
      new QueryExecutionFactoryHttp("https://synthg-fact.dice-research.org/sparql");

  @Test
  public void test() {
    QRestrictedPath path = QRestrictedPath.create(properties, directions);
    ICountRetriever counter = new ApproximatingCountRetriever(qef, maxCounter);
    NPMIBasedScorer scorer = new NPMIBasedScorer(counter);
    double score = scorer.calculateScore(fact.getSubject(), preprocessor.generatePredicate(fact),
        fact.getObject().asResource(), path);
    
    // confirm is not NaN
    Assert.assertFalse(Double.isNaN(score));
  }
  
  
  @Parameters
  public static Collection<Object[]> data() {
    List<Object[]> testConfigs = new ArrayList<Object[]>();
    testConfigs.add(new Object[] {new PredicateFactory(qef), new DefaultMaxCounter(qef)}); // no virtual types
    testConfigs.add(new Object[] {new EmptyPredicateFactory(), new VirtualTypesMaxCounter(qef)}); // virtual types
    return testConfigs;
  }
  
  private FactPreprocessor preprocessor;
  private MaxCounter maxCounter;
  
  public MaxCounterTest(FactPreprocessor preprocessor, MaxCounter maxCounter) {
    this.preprocessor = preprocessor;
    this.maxCounter = maxCounter;
  }
}
