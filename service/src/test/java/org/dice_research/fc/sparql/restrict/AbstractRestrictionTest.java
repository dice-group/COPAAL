package org.dice_research.fc.sparql.restrict;

import java.util.HashSet;
import java.util.Set;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mindswap.pellet.utils.SetUtils;

@RunWith(Parameterized.class)
public abstract class AbstractRestrictionTest {
  
  public static final String SELECT_VARIABLE = "x";

  private QueryExecutionFactory qef;
  private Set<String> expectedSet;
  private ITypeRestriction restriction;

  public AbstractRestrictionTest(QueryExecutionFactory qef, Set<String> expectedSet,
      ITypeRestriction restriction) {
    this.qef = qef;
    this.expectedSet = expectedSet;
    this.restriction = restriction;
  }

  @Test
  public void runTest() {
    Set<String> instances = new HashSet<>();
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ?");builder.append(SELECT_VARIABLE);builder.append(" WHERE {");
    addBeforeRestriction(builder);
    restriction.addRestrictionToQuery("x", builder);
    addAfterRestriction(builder);
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

  protected void addAfterRestriction(StringBuilder builder) {
    // Nothing to do
  }

  protected void addBeforeRestriction(StringBuilder builder) {
    // Nothing to do
  }
}
