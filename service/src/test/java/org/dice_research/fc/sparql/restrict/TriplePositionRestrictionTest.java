package org.dice_research.fc.sparql.restrict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mindswap.pellet.utils.SetUtils;

@RunWith(Parameterized.class)
public class TriplePositionRestrictionTest {

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
        new TriplePositionRestriction(true, false, false, false)});
    parameters.add(new Object[] {qef,
        new HashSet<String>(Arrays.asList("http://example.org/r1", "http://example.org/r3",
            "http://example.org/r5", "http://example.org/r7", "http://example.org/r9",
            "http://example.org/r11", sBlankNode.getId().getLabelString())),
        new TriplePositionRestriction(true, false, false, true)});
    // Select all predicates
    parameters.add(new Object[] {qef,
        new HashSet<String>(Arrays.asList("http://example.org/p1", "http://example.org/p2")),
        new TriplePositionRestriction(false, true, false, false)});
    parameters.add(new Object[] {qef,
        new HashSet<String>(Arrays.asList("http://example.org/p1", "http://example.org/p2")),
        new TriplePositionRestriction(false, true, false, true)});
    // Select all objects
    parameters.add(new Object[] {qef,
        new HashSet<String>(Arrays.asList("http://example.org/r2", "http://example.org/r4",
            "http://example.org/r6", "http://example.org/r8", "http://example.org/r10", "Text",
            oBlankNode.getId().getLabelString())),
        new TriplePositionRestriction(false, false, true, false)});
    parameters.add(new Object[] {qef,
        new HashSet<String>(Arrays.asList("http://example.org/r2", "http://example.org/r4",
            "http://example.org/r6", "http://example.org/r8", "http://example.org/r10",
            oBlankNode.getId().getLabelString())),
        new TriplePositionRestriction(false, false, true, true)});
    
    return parameters;
  }

  private QueryExecutionFactory qef;
  private Set<String> expectedSet;
  private TriplePositionRestriction restriction;

  public TriplePositionRestrictionTest(QueryExecutionFactory qef, Set<String> expectedSet,
      TriplePositionRestriction restriction) {
    this.qef = qef;
    this.expectedSet = expectedSet;
    this.restriction = restriction;
  }

  @Test
  public void runTest() {
    Set<String> instances = new HashSet<>();
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ?x WHERE {");
    restriction.addRestrictionToQuery("x", builder);
    builder.append("}");
    QueryExecution qe = null;
    try {
      qe = qef.createQueryExecution(builder.toString());
      ResultSet rs = qe.execSelect();
      QuerySolution qs;
      while (rs.hasNext()) {
        qs = rs.next();
        RDFNode node = qs.get("x");
        if (node.isURIResource()) {
          instances.add(node.asResource().getURI());
        } else if (node.isAnon()) {
          instances.add(node.asResource().getId().getBlankNodeId().getLabelString());
        } else if (node.isLiteral()) {
          instances.add(node.asLiteral().getLexicalForm());
        }
      }
    } finally {
      if (qe != null) {
        qe.close();
      }
    }

    String errorMessage = expectedSet.toString() + " != " + instances.toString();
    Assert.assertTrue(errorMessage, SetUtils.equals(expectedSet, instances));
  }
}
