package org.dice_research.fc.paths.scorer.count;

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
import org.dice_research.fc.paths.scorer.count.max.DefaultMaxCounter;
import org.junit.Assert;
import org.junit.Test;

public class SPARQLBasedResultStreamingCountRetrieverTest {

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
    pathElements.add(new Pair<Property, Boolean>(ResourceFactory.createProperty("http://www.example.org/P1"), true));
    pathElements.add(new Pair<Property, Boolean>(ResourceFactory.createProperty("http://www.example.org/P1"), true));
    QRestrictedPath path = new QRestrictedPath(pathElements);

    ICountRetriever appCountRetriever = new SPARQLBasedResultStreamingCountRetriever(qef, new DefaultMaxCounter(qef));
    long predCount = appCountRetriever.countPredicateInstances(predicate);
    long maxCount = appCountRetriever.deriveMaxCount(predicate);

    long pathCount = appCountRetriever.countPathInstances(path, predicate.getDomain(), predicate.getRange());
    long predPathCount = appCountRetriever.countCooccurrences(predicate, path);

    ICountRetriever propPathRetriever = new PairCountRetriever(qef, new DefaultMaxCounter(qef));
    long pathCount2 = propPathRetriever.countPathInstances(path, predicate.getDomain(), predicate.getRange());
    long predPathCount2 = propPathRetriever.countCooccurrences(predicate, path);
    
    Assert.assertEquals(5, predCount);
    Assert.assertEquals(25, maxCount);// 5*5
    Assert.assertEquals(2, pathCount);
    Assert.assertEquals(1, predPathCount);
    Assert.assertEquals(2, pathCount2);
    Assert.assertEquals(1, predPathCount2);

  }
}
