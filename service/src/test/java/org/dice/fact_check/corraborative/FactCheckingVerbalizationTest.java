package org.dice.fact_check.corraborative;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.fact_check.corraborative.ui_result.Path;
import org.dice.fact_check.corraborative.ui_result.create.IPathBuilder;
import org.dice.fact_check.corraborative.ui_result.create.VerbalizingPathBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests the verbalization feature.
 *
 */
@RunWith(Parameterized.class)
public class FactCheckingVerbalizationTest {

  /**
   * The path with the intermediate nodes
   */
  private Result result;
  /**
   * The fact's subject
   */
  private Resource subject;
  /**
   * The fact's object
   */
  private Resource object;
  /**
   * True if we expect an empty result
   */
  private boolean isEmptyExpected;

  public FactCheckingVerbalizationTest(Resource subject, Resource object, Result result,
      boolean isEmptyExpected) {
    this.result = result;
    this.subject = subject;
    this.object = object;
    this.isEmptyExpected = isEmptyExpected;
  }

  @Test
  public void testVerbalization()
      throws FileNotFoundException, InterruptedException, ParseException {
    IPathBuilder verbalizer = new VerbalizingPathBuilder();
    Path path = verbalizer.createPath(subject, object, result);
    Assert.assertEquals(path.getPathText().isEmpty(), isEmptyExpected);
  }

  @Parameters
  public static Collection<Object[]> data() {
    List<Object[]> testConfigs = new ArrayList<Object[]>();
    Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Bill_Gates");
    Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/birthPlace");
    Resource object = ResourceFactory.createResource("http://dbpedia.org/resource/United_States");

    Resource subject2 = ResourceFactory.createResource("http://dbpedia.org/resource/Nia_Gill");
    Resource object2 =
        ResourceFactory.createResource("http://dbpedia.org/resource/Bachelor_of_Arts");
    Property property2 = ResourceFactory.createProperty("http://dbpedia.org/ontology/education");

    Resource randomRes = ResourceFactory.createResource("http://dbpedia.org/resource/randomRes");

    String builder = "?s ?p1 ?x1;?x1 ?p2 ?o";
    String pathStr = "http://dbpedia.org/ontology/birthPlace;http://dbpedia.org/ontology/country";
    String intermediateNodes = "http://dbpedia.org/resource/Washington_(state)";

    String builder2 = "?s ?p1 ?o";
    String pathStr2 = "http://dbpedia.org/ontology/almaMater";
    String intermediateNodes2 = "";

    Result result = new Result(pathStr, property, builder, intermediateNodes, 2);
    Result result2 = new Result(pathStr2, property2, builder2, intermediateNodes2, 1);

    testConfigs.add(new Object[] {subject, object, result, false});
    testConfigs.add(new Object[] {subject2, object2, result2, false});
    testConfigs.add(new Object[] {randomRes, randomRes, result, true});
    return testConfigs;
  }
}
