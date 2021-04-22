package org.dice.FactCheck.Corraborative;

import static org.junit.Assert.assertTrue;
import java.io.FileNotFoundException;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.FactCheck.Corraborative.Config.Config;
import org.dice.FactCheck.Corraborative.PathGenerator.IPathGeneratorFactory.PathGeneratorType;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice.FactCheck.Corraborative.Query.RemoteQueryExecutioner;
import org.dice.FactCheck.Corraborative.UIResult.CorroborativeGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FactCheckingTest {

  @Autowired private RemoteQueryExecutioner queryExecutioner;

  @Autowired private FactChecking factChecking;

  @Autowired private Config config;

  @Test
  public void QryTest() throws ParseException {
    queryExecutioner.setServiceRequestURL(
        config.serviceURLResolve(PathGeneratorType.defaultPathGenerator));

    // String cntQuerytxt="SELECT  (count(*) AS ?c) WHERE  { ?s
    // <http://dbpedia.org/ontology/nationality>  ?o}";
    SelectBuilder qryBuilder = new SelectBuilder();
    qryBuilder.addVar("count(*)", "?c");
    qryBuilder.addWhere(
        NodeFactory.createVariable("s"),
        "<http://dbpedia.org/ontology/nationality>",
        NodeFactory.createVariable("o"));
    Query cntQuery = qryBuilder.build();
    System.out.println(cntQuery.toString());

    QueryExecution queryExecution = queryExecutioner.getQueryExecution(cntQuery);
    double count_Occurrence = queryExecution.execSelect().next().get("?c").asLiteral().getDouble();
    queryExecution.close();
    System.out.println("Count: " + count_Occurrence);

    assertTrue(count_Occurrence > 0);
  }

  // This test shows that the vTy parameter has a positive effect and show that 'virtual types' can
  // be helpful
  @Test
  public void FC_EducationTest()
      throws FileNotFoundException, InterruptedException, ParseException {

    final Model model = ModelFactory.createDefaultModel();

    Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Nia_Gill");
    Resource object =
        ResourceFactory.createResource("http://dbpedia.org/resource/Bachelor_of_Arts");
    Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/education");

    //			Resource subject =
    // ResourceFactory.createResource("http://dbpedia.org/resource/Bill_Gates");
    //	        Resource object =
    // ResourceFactory.createResource("http://dbpedia.org/resource/United_States");
    //	        Property property =
    // ResourceFactory.createProperty("http://dbpedia.org/ontology/nationality");

    Statement statement = ResourceFactory.createStatement(subject, property, object);

    FCTest(statement, 2, true, false);
    FCTest(statement, 2, false, false);
  }

  protected void FCTest(Statement statement, int pathLen, boolean vTy, boolean verbalization)
      throws FileNotFoundException, InterruptedException, ParseException {

    final Model model = ModelFactory.createDefaultModel();
    model.add(statement);

    CorroborativeGraph cg =
        factChecking.checkFacts(
            model,
            pathLen,
            vTy,
            PathGeneratorType.defaultPathGenerator,
            verbalization); // vTy:virtual types

    System.out.println("Subject: " + statement.getSubject());
    System.out.println("Property: " + statement.getPredicate());
    System.out.println("Object: " + statement.getObject());
    System.out.println("Count paths: " + cg.getPathList().toArray().length);
    System.out.println("virtual Types: " + vTy + " Score:" + cg.getGraphScore());
  }
}
