package org.dice_research.fc.paths.imprt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.export.DefaultExporter;
import org.dice_research.fc.paths.export.IPathExporter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PathImporterTest {
  private Entry<Property, List<QRestrictedPath>> predicateToPaths;

  public PathImporterTest(Entry<Property, List<QRestrictedPath>> pathMap) {
    this.predicateToPaths = pathMap;
  }

  @Test
  public void test() throws IOException {
    File file = File.createTempFile("temp", null);
    file.deleteOnExit();

    IPathExporter pathExporter = new DefaultExporter(file.getAbsolutePath());
    String savedIn = pathExporter.exportPaths(predicateToPaths);
    
    IPathImporter pathImporter = new DefaultImporter();
    Entry<Property, List<QRestrictedPath>> p = pathImporter.importPaths(savedIn);
    
    
    Assert.assertEquals(predicateToPaths,  p);

  }

  @Parameters
  public static Collection<Object[]> data() {
    List<Object[]> testConfigs = new ArrayList<Object[]>();

    Property prop = ResourceFactory.createProperty("http://example.org#randomProperty");
    Property prop2 = ResourceFactory.createProperty("http://example.org#randomProperty2");
    Property prop3 = ResourceFactory.createProperty("http://example.org#randomProperty3");
    Property prop3m = ResourceFactory.createProperty("http://example.org#randomProperty3,We");

    List<Pair<Property, Boolean>> exp1 = new ArrayList<Pair<Property, Boolean>>();
    exp1.add(new Pair<Property, Boolean>(prop2, true));

    List<Pair<Property, Boolean>> exp2 = new ArrayList<Pair<Property, Boolean>>();
    exp2.add(new Pair<Property, Boolean>(prop2, false));

    List<Pair<Property, Boolean>> exp3 = new ArrayList<Pair<Property, Boolean>>();
    exp3.add(new Pair<Property, Boolean>(prop2, true));
    exp3.add(new Pair<Property, Boolean>(prop3, false));

    List<Pair<Property, Boolean>> exp4 = new ArrayList<Pair<Property, Boolean>>();
    exp4.add(new Pair<Property, Boolean>(prop2, true));
    exp4.add(new Pair<Property, Boolean>(prop3, true));

    List<Pair<Property, Boolean>> exp5 = new ArrayList<Pair<Property, Boolean>>();
    exp5.add(new Pair<Property, Boolean>(prop2, true));
    exp5.add(new Pair<Property, Boolean>(prop3, true));
    exp5.add(new Pair<Property, Boolean>(prop3m, false));

    List<QRestrictedPath> paths = new ArrayList<QRestrictedPath>();
    paths.add(new QRestrictedPath(exp1));
    paths.add(new QRestrictedPath(exp2, 0.5));
    paths.add(new QRestrictedPath(exp3, 0.1));
    paths.add(new QRestrictedPath(exp4, 0.0));
    paths.add(new QRestrictedPath(exp5, -0.5));

    List<Pair<Property, Boolean>> exp6 = new ArrayList<Pair<Property, Boolean>>();
    exp6.add(new Pair<Property, Boolean>(prop3, true));
    List<QRestrictedPath> paths2 = new ArrayList<QRestrictedPath>();
    paths2.add(new QRestrictedPath(exp6));

    Map<Property, List<QRestrictedPath>> map = new HashMap<Property, List<QRestrictedPath>>();
    map.put(prop, paths);
    map.put(prop2, paths2);
    map.put(prop3, new ArrayList<QRestrictedPath>());

    for(Entry<Property, List<QRestrictedPath>> entry: map.entrySet()) {
      testConfigs.add(new Object[] {entry});
    }
    return testConfigs;
  }
}
