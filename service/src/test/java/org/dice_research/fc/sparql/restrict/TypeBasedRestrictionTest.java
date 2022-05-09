package org.dice_research.fc.sparql.restrict;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TypeBasedRestrictionTest extends AbstractRestrictionTest {

  @Parameters
  public static List<Object[]> testCases() {
    Model model = ModelFactory.createDefaultModel();
    model.add(model.getResource("http://example.org/r1"), RDF.type,
        model.getResource("http://example.org/c1"));
    model.add(model.getResource("http://example.org/r1"), RDF.type,
        model.getResource("http://example.org/c2"));
    model.add(model.getResource("http://example.org/r3"),
        model.getProperty("http://example.org/p2"), model.getResource("http://example.org/r4"));
    model.add(model.getResource("http://example.org/r5"), RDF.type,
        model.getResource("http://example.org/c2"));
    model.add(model.getResource("http://example.org/r7"),
        model.getProperty("http://example.org/p2"), model.getResource("http://example.org/r8"));
    model.add(model.getResource("http://example.org/r9"), RDF.type, "Text");
    Resource oBlankNode = model.createResource();
    model.add(model.getResource("http://example.org/r11"), RDF.type, oBlankNode);
    Resource sBlankNode = model.createResource();
    model.add(sBlankNode, RDF.type, model.getResource("http://example.org/c1"));
    Dataset dataset = DatasetFactory.create(model);
    QueryExecutionFactory qef = new QueryExecutionFactoryDataset(dataset);

    List<Object[]> parameters = new ArrayList<>();
    // Select instances of c1
    parameters.add(new Object[] {qef,
        new HashSet<String>(
            Arrays.asList("http://example.org/r1", sBlankNode.getId().getLabelString())),
        new TypeBasedRestriction(new HashSet<String>(Arrays.asList("http://example.org/c1")))});
    // Select instances of c2
    parameters.add(new Object[] {qef,
        new HashSet<String>(Arrays.asList("http://example.org/r1", "http://example.org/r5")),
        new TypeBasedRestriction(new HashSet<String>(Arrays.asList("http://example.org/c2")))});
    // Select instances of c1 and c2
    parameters.add(new Object[] {qef, new HashSet<String>(Arrays.asList("http://example.org/r1")),
        new TypeBasedRestriction(
            new HashSet<String>(Arrays.asList("http://example.org/c1", "http://example.org/c2")))});
    // Select instances of c3 (does not exist)
    parameters.add(new Object[] {qef, Collections.EMPTY_SET,
        new TypeBasedRestriction(new HashSet<String>(Arrays.asList("http://example.org/c3")))});

    return parameters;
  }

  public TypeBasedRestrictionTest(QueryExecutionFactory qef, Set<String> expectedSet,
      ITypeRestriction restriction) {
    super(qef, expectedSet, restriction);
  }

}
