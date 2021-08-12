package org.dice.fact_check.corraborative;

import java.util.HashSet;
import java.util.Set;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.fact_check.corraborative.filter.npmi.NPMIFilterException;
import org.dice.fact_check.corraborative.query.LocalQueryExecutioner;
import org.dice.fact_check.corraborative.query.QueryExecutioner;
import org.junit.Assert;
import org.junit.Test;

public class NPMICalculator_vTyTest {

  private final QueryExecutioner queryExecutioner =
      new LocalQueryExecutioner("src/test/resources/test_model.nt");

  @Test
  public void testQueries() throws NPMIFilterException, ParseException {
    Statement statement =
        ResourceFactory.createStatement(ResourceFactory.createResource("http://www.example.org/A"),
            ResourceFactory.createProperty("http://www.example.org/P1"),
            ResourceFactory.createResource("http://www.example.org/D"));

    // virtual types count
    int predicateOccurrencesVT = 5;
    int subjectTriplesVT = 3;
    int objectTriplesVT = 4;

    // non virtual types count
    int predicateOccurrences = 5;
    int subjectTriples = 5;
    int objectTriples = 5;

    // path
    String path = "http://www.example.org/P1;http://www.example.org/P1";
    String builder = "?s ?p1 ?x1;?x1 ?p2 ?o";

    // calculate NPMI for virtual types
    NPMICalculator calculatorVT = new NPMICalculator(path, builder, statement, "", 2,
        predicateOccurrencesVT, subjectTriplesVT, objectTriplesVT, null, null, queryExecutioner);
    double scoreVT = calculatorVT.calculatePMIScore_vTy();

    // calculate NPMI for non virtual types
    Set<Node> subjectType = new HashSet<>();
    subjectType.add(ResourceFactory.createResource("http://www.example.org/C1").asNode());

    NPMICalculator calculator =
        new NPMICalculator(path, builder, statement, "", 2, predicateOccurrences, subjectTriples,
            objectTriples, subjectType, subjectType, queryExecutioner);
    double score = calculator.calculatePMIScore();

    // assert both differ from each other
    Assert.assertNotEquals(score, scoreVT);
  }
}
