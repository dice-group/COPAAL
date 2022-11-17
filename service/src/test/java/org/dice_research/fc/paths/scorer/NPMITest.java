package org.dice_research.fc.paths.scorer;

import java.util.ArrayList;
import java.util.Collection;
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
import org.dice_research.fc.paths.IPathScorer;
import org.dice_research.fc.paths.PredicateFactory;
import org.dice_research.fc.paths.scorer.count.ApproximatingCountRetriever;
import org.dice_research.fc.paths.scorer.count.PairCountRetriever;
import org.dice_research.fc.paths.scorer.count.max.DefaultMaxCounter;
import org.dice_research.fc.sparql.query.ListBaseQueryValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class NPMITest {
  private QueryExecutionFactory qef;
  private QRestrictedPath path;
  private Statement triple;
  private double expectedScore;

  public NPMITest(QueryExecutionFactory qef, QRestrictedPath path, Statement triple,
      double expectedScore) {
    this.qef = qef;
    this.path = path;
    this.triple = triple;
    this.expectedScore = expectedScore;
  }

  @Test
  public void test() {
    FactPreprocessor preprocessor = new PredicateFactory(qef);
    Predicate predicate = preprocessor.generatePredicate(triple);

    IPathScorer pathScorer = new NPMIBasedScorer(new ApproximatingCountRetriever(qef, new DefaultMaxCounter(qef),new ListBaseQueryValidator(new ArrayList<>())));
    pathScorer.score(triple.getSubject(), predicate, triple.getObject().asResource(), path);
    Assert.assertEquals(expectedScore, path.getScore(), 0.0001);

    IPathScorer pathScorer2 = new NPMIBasedScorer(new PairCountRetriever(qef, new DefaultMaxCounter(qef),new ListBaseQueryValidator(new ArrayList<>())));
    pathScorer2.score(triple.getSubject(), predicate, triple.getObject().asResource(), path);
    Assert.assertEquals(expectedScore, path.getScore(), 0.0001);

  }

  @Parameters
  public static Collection<Object[]> data() {
    List<Object[]> testConfigs = new ArrayList<Object[]>();

    Model model = ModelFactory.createDefaultModel();
    model.read("test_graph.nt");
    QueryExecutionFactory qef = new QueryExecutionFactoryModel(model);

    Statement triple =
        ResourceFactory.createStatement(ResourceFactory.createResource("http://www.example.org/A"),
            ResourceFactory.createProperty("http://www.example.org/P1"),
            ResourceFactory.createResource("http://www.example.org/D"));

    // P1/P1
    List<Pair<Property, Boolean>> pathElements = new ArrayList<Pair<Property, Boolean>>();
    pathElements.add(new Pair<Property, Boolean>(
        ResourceFactory.createProperty("http://www.example.org/P1"), true));
    pathElements.add(new Pair<Property, Boolean>(
        ResourceFactory.createProperty("http://www.example.org/P1"), true));
    QRestrictedPath path = new QRestrictedPath(pathElements);

    // P3/^P2/P1
    List<Pair<Property, Boolean>> pathElements2 = new ArrayList<Pair<Property, Boolean>>();
    pathElements2.add(new Pair<Property, Boolean>(
        ResourceFactory.createProperty("http://www.example.org/P3"), true));
    pathElements2.add(new Pair<Property, Boolean>(
        ResourceFactory.createProperty("http://www.example.org/P2"), false));
    pathElements2.add(new Pair<Property, Boolean>(
        ResourceFactory.createProperty("http://www.example.org/P1"), true));
    QRestrictedPath path2 = new QRestrictedPath(pathElements2);

    // P1/P2/P4
    List<Pair<Property, Boolean>> pathElements3 = new ArrayList<Pair<Property, Boolean>>();
    pathElements3.add(new Pair<Property, Boolean>(
        ResourceFactory.createProperty("http://www.example.org/P1"), true));
    pathElements3.add(new Pair<Property, Boolean>(
        ResourceFactory.createProperty("http://www.example.org/P2"), true));
    pathElements3.add(new Pair<Property, Boolean>(
        ResourceFactory.createProperty("http://www.example.org/P4"), true));
    QRestrictedPath path3 = new QRestrictedPath(pathElements3);

    testConfigs.add(new Object[] {qef, path, triple, 0.2846});
    testConfigs.add(new Object[] {qef, path2, triple, -0.1});
    testConfigs.add(new Object[] {qef, path3, triple, -0.1});
    return testConfigs;
  }
}
