package org.dice.fact_check.corraborative;

import java.io.FileNotFoundException;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.fact_check.corraborative.path_generator.DefaultPathGeneratorFactory;
import org.dice.fact_check.corraborative.path_generator.IPathGeneratorFactory.PathGeneratorType;
import org.dice.fact_check.corraborative.query.LocalQueryExecutioner;
import org.dice.fact_check.corraborative.query.QueryExecutioner;
import org.dice.fact_check.corraborative.query.SparqlQueryGenerator;
import org.dice.fact_check.corraborative.ui_result.CorroborativeGraph;
import org.dice.fact_check.corraborative.ui_result.create.DefaultPathFactory;
import org.junit.Assert;
import org.junit.Test;

public class FactCheckingTest {

  private final QueryExecutioner queryExecutioner =
      new LocalQueryExecutioner("src/test/resources/test_model.nt");

  @Test
  public void queryTest() throws ParseException {
    SelectBuilder qryBuilder = new SelectBuilder();
    qryBuilder.addVar("count(*)", "?c");
    qryBuilder.addWhere(NodeFactory.createVariable("s"), "<http://www.example.org/P1>",
        NodeFactory.createVariable("o"));
    Query cntQuery = qryBuilder.build();
    System.out.println(cntQuery.toString());

    QueryExecution queryExecution = queryExecutioner.getQueryExecution(cntQuery);
    double count_Occurrence = queryExecution.execSelect().next().get("?c").asLiteral().getDouble();
    queryExecution.close();
    System.out.println("Count: " + count_Occurrence);

    Assert.assertTrue(count_Occurrence > 0);
  }

  // This test shows that the vTy parameter has a positive effect and show that 'virtual types' can
  // be helpful
  @Test
  public void fcEducationTest()
      throws FileNotFoundException, InterruptedException, ParseException {

    Statement statement =
        ResourceFactory.createStatement(ResourceFactory.createResource("http://www.example.org/A"),
            ResourceFactory.createProperty("http://www.example.org/P1"),
            ResourceFactory.createResource("http://www.example.org/D"));
    
    Assert.assertTrue(fcTest(statement, 2, true) > 0);
    Assert.assertTrue(fcTest(statement, 2, false) > 0);
  }

  protected double fcTest(Statement statement, int pathLen, boolean vTy)
      throws FileNotFoundException, InterruptedException, ParseException {

    final Model model = ModelFactory.createDefaultModel();
    model.add(statement);

    FactChecking factChecking =
        new FactChecking(new SparqlQueryGenerator(), queryExecutioner, new CorroborativeGraph(),
            new DefaultPathFactory(), new DefaultPathGeneratorFactory(), "http://www.example.org");
    
    CorroborativeGraph cg = factChecking.checkFacts(model, pathLen, vTy,
        PathGeneratorType.defaultPathGenerator, false); // vTy:virtual types

    System.out.println("Subject: " + statement.getSubject());
    System.out.println("Property: " + statement.getPredicate());
    System.out.println("Object: " + statement.getObject());
    System.out.println("Count paths: " + cg.getPathList().toArray().length);
    System.out.println("virtual Types: " + vTy + " Score:" + cg.getGraphScore());
    
    return cg.getGraphScore();
  }
}