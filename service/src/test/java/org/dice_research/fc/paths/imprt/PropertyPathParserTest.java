package org.dice_research.fc.paths.imprt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.fc.data.QRestrictedPath;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PropertyPathParserTest {

  private String propertyPath;
  private QRestrictedPath path;
  private List<Pair<Property, Boolean>> expected;

  public PropertyPathParserTest(String propertyPath, List<Pair<Property, Boolean>> expected,
      QRestrictedPath path) {
    this.propertyPath = propertyPath;
    this.path = path;
    this.expected = expected;
  }

  @Test
  public void test() {
    // test if property path string to pair is correct
    Assert.assertEquals(expected, QRestrictedPath.create(propertyPath,0).getPathElements());

    // test if path to property path string is correct
    Assert.assertEquals(propertyPath, path.getEvidence());
  }

  @Parameters
  public static Collection<Object[]> data() {
    List<Object[]> testConfigs = new ArrayList<Object[]>();

    Property prop2 = ResourceFactory.createProperty("http://example.org#randomProperty2");
    Property prop3 = ResourceFactory.createProperty("http://example.org#randomProperty3");

    List<Pair<Property, Boolean>> exp1 = new ArrayList<Pair<Property, Boolean>>();
    exp1.add(new Pair<Property, Boolean>(prop2, true));
    testConfigs.add(
        new Object[] {"<http://example.org#randomProperty2>", exp1, new QRestrictedPath(exp1)});

    List<Pair<Property, Boolean>> exp2 = new ArrayList<Pair<Property, Boolean>>();
    exp2.add(new Pair<Property, Boolean>(prop2, false));
    testConfigs.add(
        new Object[] {"^<http://example.org#randomProperty2>", exp2, new QRestrictedPath(exp2)});


    List<Pair<Property, Boolean>> exp3 = new ArrayList<Pair<Property, Boolean>>();
    exp3.add(new Pair<Property, Boolean>(prop2, true));
    exp3.add(new Pair<Property, Boolean>(prop3, false));
    testConfigs.add(
        new Object[] {"<http://example.org#randomProperty2>/^<http://example.org#randomProperty3>",
            exp3, new QRestrictedPath(exp3)});


    List<Pair<Property, Boolean>> exp4 = new ArrayList<Pair<Property, Boolean>>();
    exp4.add(new Pair<Property, Boolean>(prop2, true));
    exp4.add(new Pair<Property, Boolean>(prop3, true));
    testConfigs.add(
        new Object[] {"<http://example.org#randomProperty2>/<http://example.org#randomProperty3>",
            exp4, new QRestrictedPath(exp4)});


    List<Pair<Property, Boolean>> exp5 = new ArrayList<Pair<Property, Boolean>>();
    exp5.add(new Pair<Property, Boolean>(prop2, true));
    exp5.add(new Pair<Property, Boolean>(prop3, true));
    exp5.add(new Pair<Property, Boolean>(prop3, false));
    testConfigs.add(new Object[] {
        "<http://example.org#randomProperty2>/<http://example.org#randomProperty3>/^<http://example.org#randomProperty3>",
        exp5, new QRestrictedPath(exp5)});

    return testConfigs;
  }

}
