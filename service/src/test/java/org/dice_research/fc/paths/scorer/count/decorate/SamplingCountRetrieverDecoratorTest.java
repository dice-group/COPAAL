package org.dice_research.fc.paths.scorer.count.decorate;

import java.util.ArrayList;
import java.util.List;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.FactPreprocessor;
import org.dice_research.fc.paths.PredicateFactory;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.paths.scorer.count.PairCountRetriever;
import org.dice_research.fc.paths.scorer.count.max.DefaultMaxCounter;
import org.dice_research.fc.sparql.query.ListBaseQueryValidator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SamplingCountRetrieverDecoratorTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SamplingCountRetrieverDecoratorTest.class);

  @Test
  public void test() {
    Model model = ModelFactory.createDefaultModel();
    model.read("test_graph.nt");
    QueryExecutionFactory qef = new QueryExecutionFactoryModel(model);

    Statement triple =
        ResourceFactory.createStatement(ResourceFactory.createResource("http://www.example.org/A"),
            ResourceFactory.createProperty("http://www.example.org/P1"),
            ResourceFactory.createResource("http://www.example.org/D"));

    FactPreprocessor predFactory = new PredicateFactory(qef);
    Predicate predicate = predFactory.generatePredicate(triple);

    List<Pair<Property, Boolean>> pathElements = new ArrayList<Pair<Property, Boolean>>();
    pathElements.add(new Pair<Property, Boolean>(
        ResourceFactory.createProperty("http://www.example.org/P1"), true));
    pathElements.add(new Pair<Property, Boolean>(
        ResourceFactory.createProperty("http://www.example.org/P1"), true));
    QRestrictedPath path = new QRestrictedPath(pathElements);

    long seed = 1631026416727L;// System.currentTimeMillis();
    LOGGER.info("seed={}", seed);
    ICountRetriever retriever = createCountRetriever(seed, qef);
    long predCount = retriever.countPredicateInstances(predicate);
    long maxCount = retriever.deriveMaxCount(predicate);

    long pathCount =
        retriever.countPathInstances(path, predicate.getDomain(), predicate.getRange());
    long predPathCount = retriever.countCooccurrences(predicate, path);

    long predCount2 = retriever.countPredicateInstances(predicate);
    long pathCount2 =
        retriever.countPathInstances(path, predicate.getDomain(), predicate.getRange());
    long predPathCount2 = retriever.countCooccurrences(predicate, path);

    long predCount3 = retriever.countPredicateInstances(predicate);
    long pathCount3 =
        retriever.countPathInstances(path, predicate.getDomain(), predicate.getRange());
    long predPathCount3 = retriever.countCooccurrences(predicate, path);

    Assert.assertTrue(predCount >= 0);
    Assert.assertTrue(predCount <= 3);
    Assert.assertEquals(maxCount, 9);// 3*3
    Assert.assertTrue(pathCount >= 0);
    Assert.assertTrue(pathCount <= 2);
    Assert.assertTrue(predPathCount >= 0);
    Assert.assertTrue(predPathCount <= 1);
    // The counts are the same even when requested several times
    Assert.assertEquals(predCount, predCount2);
    Assert.assertEquals(pathCount, pathCount2);
    Assert.assertEquals(predPathCount, predPathCount2);
    Assert.assertEquals(predCount, predCount3);
    Assert.assertEquals(pathCount, pathCount3);
    Assert.assertEquals(predPathCount, predPathCount3);
  }

  protected ICountRetriever createCountRetriever(long seed, QueryExecutionFactory qef) {
    return new SamplingCountRetrieverDecorator(
        new PairCountRetriever(qef, new DefaultMaxCounter(qef), new ListBaseQueryValidator(new ArrayList<>())), seed, 3, qef);
  }
}
