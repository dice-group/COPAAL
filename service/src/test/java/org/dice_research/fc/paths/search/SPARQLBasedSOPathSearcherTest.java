package org.dice_research.fc.paths.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.dice.FactCheck.Corraborative.filter.npmi.NPMIFilterException;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.sparql.filter.EqualsFilter;
import org.dice_research.fc.sparql.filter.IRIFilter;
import org.dice_research.fc.sparql.filter.NamespaceFilter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SPARQLBasedSOPathSearcherTest {

  private QueryExecutionFactory qef;
  private int maximumLength;
  private Collection<IRIFilter> propertyFilter;
  private Resource subject;
  private Predicate predicate;
  private Resource object;
  private Set<QRestrictedPath> expectedPaths;

  public SPARQLBasedSOPathSearcherTest(Dataset dataset, Integer maximumLength,
      Collection<IRIFilter> propertyFilter, Resource subject, Predicate predicate, Resource object,
      Set<QRestrictedPath> expectedPaths) {
    qef = new QueryExecutionFactoryDataset(dataset);
    this.maximumLength = maximumLength;
    this.propertyFilter = propertyFilter;
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
    this.expectedPaths = expectedPaths;
  }

  @Test
  public void test() throws NPMIFilterException {
    SPARQLBasedSOPathSearcher searcher =
        new SPARQLBasedSOPathSearcher(qef, maximumLength, propertyFilter);
    Collection<QRestrictedPath> paths = searcher.search(subject, predicate, object);
    StringBuilder builder = new StringBuilder();
    for (QRestrictedPath path : paths) {
      if (!expectedPaths.contains(path)) {
        builder.append("The expected path ");
        builder.append(path.toString());
        builder.append(" couldn't be found in the set of expected paths.\n");
      } else {
        expectedPaths.remove(path);
      }
    }
    if (expectedPaths.size() > 0) {
      builder.append("The following paths were expected but are are missing in the result:\n");
      for (QRestrictedPath path : expectedPaths) {
        builder.append(path.toString());
        builder.append("\n");
      }
    }
    // If errors have been encountered, let the test fail
    if (builder.length() > 0) {
      Assert.fail(builder.toString());
    }
  }

  @Parameters
  public static Collection<Object[]> data() {
    List<Object[]> testConfigs = new ArrayList<Object[]>();
    Model model = null;
    Dataset dataset = null;
    Resource subject = null;
    Predicate predicate = null;
    Resource object = null;
    Property p1 = null;
    Property p2 = null;
    Resource x1 = null;
    Resource x2 = null;
    Resource x3 = null;

    // Create a simple model and ask for direct paths between s and o
    model = ModelFactory.createDefaultModel();
    subject = model.createResource("http://example.org/subject");
    object = model.createResource("http://example.org/object");
    predicate = new Predicate(model.createProperty("http://example.org/predicate"), null, null);
    p1 = model.createProperty("http://example.org/property1");
    p2 = model.createProperty("http://example.org/other/property2");
    x1 = model.createProperty("http://example.org/node1");
    model.add(subject, predicate.getProperty(), object);
    model.add(subject, p1, object);
    model.add(subject, p2, object);
    model.add(object, p2, subject);
    model.add(subject, p1, x1);
    model.add(x1, p2, object);
    dataset = DatasetFactory.create(model);

    testConfigs.add(new Object[] {dataset, 1, Collections.EMPTY_LIST, subject, predicate, object,
        new HashSet<QRestrictedPath>(Arrays.asList(
            QRestrictedPath.create(new Property[] {predicate.getProperty()}, new boolean[] {true}),
            QRestrictedPath.create(new Property[] {p1}, new boolean[] {true}),
            QRestrictedPath.create(new Property[] {p2}, new boolean[] {true}),
            QRestrictedPath.create(new Property[] {p2}, new boolean[] {false})))});

    // Ask again but this time with an IRI filter
    testConfigs.add(new Object[] {dataset, 1,
        Arrays
            .asList((IRIFilter) new EqualsFilter(new String[] {predicate.getProperty().getURI()})),
        subject, predicate, object,
        new HashSet<QRestrictedPath>(
            Arrays.asList(QRestrictedPath.create(new Property[] {p1}, new boolean[] {true}),
                QRestrictedPath.create(new Property[] {p2}, new boolean[] {true}),
                QRestrictedPath.create(new Property[] {p2}, new boolean[] {false})))});

    // Ask again but this time with a name space filter
    testConfigs.add(new Object[] {dataset, 1,
        Arrays.asList((IRIFilter) new NamespaceFilter("http://example.org/other/", false)), subject,
        predicate, object,
        new HashSet<QRestrictedPath>(
            Arrays.asList(QRestrictedPath.create(new Property[] {p2}, new boolean[] {true}),
                QRestrictedPath.create(new Property[] {p2}, new boolean[] {false})))});


    // Create a simple model and ask for paths of length 2 between s and o
    model = ModelFactory.createDefaultModel();
    subject = model.createResource("http://example.org/subject");
    object = model.createResource("http://example.org/object");
    predicate = new Predicate(model.createProperty("http://example.org/predicate"), null, null);
    p1 = model.createProperty("http://example.org/property1");
    p2 = model.createProperty("http://example.org/other/property2");
    x1 = model.createProperty("http://example.org/node1");
    x2 = model.createProperty("http://example.org/node2");
    x3 = model.createProperty("http://example.org/node3");
    model.add(subject, predicate.getProperty(), object);
    model.add(subject, p1, object);
    model.add(subject, p2, object);
    model.add(object, p2, subject);
    model.add(subject, p1, x1);
    model.add(x1, p2, object);
    model.add(subject, p1, x2);
    model.add(x2, p2, object);
    model.add(object, p2, x2);
    // Add a path of length 3
    model.add(x1, p1, x3);
    model.add(x3, p1, x2);
    dataset = DatasetFactory.create(model);

    testConfigs.add(new Object[] {dataset, 2, Collections.EMPTY_LIST, subject, predicate, object,
        new HashSet<QRestrictedPath>(Arrays.asList(
            QRestrictedPath.create(new Property[] {predicate.getProperty()}, new boolean[] {true}),
            QRestrictedPath.create(new Property[] {p1}, new boolean[] {true}),
            QRestrictedPath.create(new Property[] {p2}, new boolean[] {true}),
            QRestrictedPath.create(new Property[] {p2}, new boolean[] {false}),
            QRestrictedPath.create(new Property[] {p1, p2}, new boolean[] {true, true}),
            QRestrictedPath.create(new Property[] {p1, p2}, new boolean[] {true, false})))});



    return testConfigs;
  }
}
