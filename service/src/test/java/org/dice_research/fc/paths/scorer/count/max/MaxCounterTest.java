package org.dice_research.fc.paths.scorer.count.max;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.fc.paths.EmptyPredicateFactory;
import org.dice_research.fc.paths.FactPreprocessor;
import org.dice_research.fc.paths.PredicateFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Testing maximum counts calculation with and without virtual type configuration.
 * 
 * @author Alexandra Silva
 *
 */
@RunWith(Parameterized.class)
public class MaxCounterTest {
  private FactPreprocessor preprocessor;
  private MaxCounter maxCounter;
  private Statement fact;
  private double expectedScore;

  public MaxCounterTest(FactPreprocessor preprocessor, MaxCounter maxCounter, Statement fact,
                             double expectedScore) {
    this.preprocessor = preprocessor;
    this.maxCounter = maxCounter;
    this.fact = fact;
    this.expectedScore = expectedScore;
  }

  @Test
  public void test() {
    double maxCount = maxCounter.deriveMaxCount(preprocessor.generatePredicate(fact));
    Assert.assertEquals(expectedScore, maxCount, 0);
  }

  @Parameters
  public static Collection<Object[]> data() {
    final String NS = "http://example.org#";
    final Resource resA = ResourceFactory.createResource(NS + "a");
    final Resource resB = ResourceFactory.createResource(NS + "b");
    final Resource resC = ResourceFactory.createResource(NS + "c");
    final Resource resD = ResourceFactory.createResource(NS + "d");
    final Property prop = ResourceFactory.createProperty(NS + "p");
    final Property propQ = ResourceFactory.createProperty(NS + "q");
    final Resource class1 = ResourceFactory.createResource(NS + "c1");
    final Resource class2 = ResourceFactory.createResource(NS + "c2");

    Statement fact = ResourceFactory.createStatement(resA, prop, resC);

    // build models
    Model model = ModelFactory.createDefaultModel();
    model.add(resA, prop, resB);
    model.add(resA, prop, resC);
    model.add(resA, RDF.type, class1);
    model.add(resC, RDF.type, class2);
    
    Model model2 = ModelFactory.createDefaultModel();
    model2.add(model);
    model2.add(resD, prop, resC);
    model2.add(resB, RDF.type, class2);
    model2.add(resD, RDF.type, class2);
    
    Model model3 = ModelFactory.createDefaultModel();
    model3.add(resA, propQ, resB);
    model3.add(resA, RDF.type, class2);
    
    // create qefs
    QueryExecutionFactory qef = new QueryExecutionFactoryModel(model);
    QueryExecutionFactory qef2 = new QueryExecutionFactoryModel(model2);
    QueryExecutionFactory qef3 = new QueryExecutionFactoryModel(model3);

    List<Object[]> testConfigs = new ArrayList<Object[]>();
    // virtual types
    testConfigs.add(new Object[] {new EmptyPredicateFactory(), new VirtualTypesMaxCounter(qef), fact, 2});
    testConfigs.add(new Object[] {new EmptyPredicateFactory(), new VirtualTypesMaxCounter(qef2), fact, 4});
    testConfigs.add(new Object[] {new EmptyPredicateFactory(), new VirtualTypesMaxCounter(qef3), fact, 0});

    // no virtual types
    testConfigs.add(new Object[] {new PredicateFactory(qef), new DefaultMaxCounter(qef), fact, 1});
    testConfigs.add(new Object[] {new PredicateFactory(qef2), new DefaultMaxCounter(qef2), fact, 3});
    testConfigs.add(new Object[] {new PredicateFactory(qef3), new DefaultMaxCounter(qef3), fact, 0});

    return testConfigs;
  }
}
