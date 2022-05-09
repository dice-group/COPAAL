package org.dice_research.fc.sparql.restrict;

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
import org.apache.jena.rdf.model.Resource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class OneOfRestrictionTest extends AbstractRestrictionTest {

  @SuppressWarnings("unchecked")
  @Parameters
  public static List<Object[]> testCases() {
    Model model = ModelFactory.createDefaultModel();
    model.add(model.getResource("http://example.org/r1"),
        model.getProperty("http://example.org/p1"), model.getResource("http://example.org/r2"));
    model.add(model.getResource("http://example.org/r3"),
        model.getProperty("http://example.org/p2"), model.getResource("http://example.org/r4"));
    model.add(model.getResource("http://example.org/r5"),
        model.getProperty("http://example.org/p1"), model.getResource("http://example.org/r6"));
    model.add(model.getResource("http://example.org/r7"),
        model.getProperty("http://example.org/p2"), model.getResource("http://example.org/r8"));
    model.add(model.getResource("http://example.org/r9"),
        model.getProperty("http://example.org/p1"), "Text");
    Resource oBlankNode = model.createResource();
    model.add(model.getResource("http://example.org/r11"),
        model.getProperty("http://example.org/p1"), oBlankNode);
    Resource sBlankNode = model.createResource();
    model.add(sBlankNode, model.getProperty("http://example.org/p1"),
        model.getResource("http://example.org/r10"));
    Dataset dataset = DatasetFactory.create(model);
    QueryExecutionFactory qef = new QueryExecutionFactoryDataset(dataset);

    List<Object[]> parameters = new ArrayList<>();
    // Select all subjects
    parameters.add(new Object[] {qef,
        new HashSet<String>(Arrays.asList("http://example.org/r1", "http://example.org/r3",
            "http://example.org/r5", "http://example.org/r7", "http://example.org/r9",
            "http://example.org/r11", sBlankNode.getId().getLabelString())),
        new OneOfRestriction(new HashSet<String>(Arrays.asList("http://example.org/r1",
            "http://example.org/r2", "http://example.org/r3", "http://example.org/r4",
            "http://example.org/r5", "http://example.org/r6", "http://example.org/r7",
            "http://example.org/r8", "http://example.org/r9", "http://example.org/r10",
            "http://example.org/r11", "_:" + sBlankNode.getId().getLabelString())))});
    // Select subjects <= r5
    parameters.add(new Object[] {qef,
        new HashSet<String>(Arrays.asList("http://example.org/r1", "http://example.org/r3",
            "http://example.org/r5")),
        new OneOfRestriction(
            new HashSet<String>(Arrays.asList("http://example.org/r1", "http://example.org/r2",
                "http://example.org/r3", "http://example.org/r4", "http://example.org/r5")))});
    // Select a single subject
    parameters.add(new Object[] {qef, new HashSet<String>(Arrays.asList("http://example.org/r5")),
        new OneOfRestriction(new HashSet<String>(Arrays.asList("http://example.org/r5")))});
    // Select a single resource that is not a subject
    parameters.add(new Object[] {qef, Collections.EMPTY_SET,
        new OneOfRestriction(new HashSet<String>(Arrays.asList("http://example.org/r2")))});
    // Give an empty restriction -> leads to selecting all subjects
    parameters.add(new Object[] {qef, 
        new HashSet<String>(Arrays.asList("http://example.org/r1", "http://example.org/r3",
            "http://example.org/r5", "http://example.org/r7", "http://example.org/r9",
            "http://example.org/r11", sBlankNode.getId().getLabelString())),
        new OneOfRestriction((Collection<String>) Collections.EMPTY_SET)});

    return parameters;
  }

  public OneOfRestrictionTest(QueryExecutionFactory qef, Set<String> expectedSet,
      ITypeRestriction restriction) {
    super(qef, expectedSet, restriction);
  }

  @Override
  protected void addBeforeRestriction(StringBuilder builder) {
    // We will only select subjects
    builder.append(" ?");
    builder.append(SELECT_VARIABLE);
    builder.append(" ?p ?o . ");
  }

}
