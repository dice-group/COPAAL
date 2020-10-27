package org.dice.FactCheck.Corraborative;

import static org.junit.Assert.assertTrue;
import java.io.FileNotFoundException;
import org.apache.jena.arq.querybuilder.SelectBuilder;
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
import org.dice.FactCheck.Corraborative.UIResult.CorroborativeGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FactCheckWikiDataTest {

  @Autowired private QueryExecutioner queryExecutioner;

  @Autowired private FactChecking factChecking;

  @Autowired private Config config;

  @Test
  public void ShouldRunQueryFromWikiData() throws ParseException {
    queryExecutioner.setServiceRequestURL(
        config.serviceURLResolve(PathGeneratorType.wikidataPathGenerator));

    /* PREFIX wdt: <http://www.wikidata.org/prop/direct/>
    PREFIX wd: <http://www.wikidata.org/entity/>
    SELECT  (count(*) AS ?item)
    WHERE
    { ?item  wdt:P31  wd:Q146}*/

    SelectBuilder qryBuilder = new SelectBuilder();
    qryBuilder.addPrefix("wdt:", "http://www.wikidata.org/prop/direct/");
    qryBuilder.addPrefix("wd:", "http://www.wikidata.org/entity/");
    qryBuilder.addVar("count(*)", "?item").addWhere("?item", "wdt:P31", "wd:Q146");
    Query cntQuery = qryBuilder.build();
    System.out.println(cntQuery.toString());

    QueryExecution queryExecution = queryExecutioner.getQueryExecution(cntQuery);
    double count_Occurrence =
        queryExecution.execSelect().next().get("?item").asLiteral().getDouble();
    queryExecution.close();
    System.out.println("Count: " + count_Occurrence);

    assertTrue(count_Occurrence > 0);
  }

  @Test
  public void ShouldRunFactCheckBasedOnWikiData()
      throws FileNotFoundException, InterruptedException, ParseException {
    final Model model = ModelFactory.createDefaultModel();
    // https://en.wikibooks.org/wiki/SPARQL/Prefixes
    Resource subject = ResourceFactory.createResource("http://www.wikidata.org/entity/Q76");
    Resource object = ResourceFactory.createResource("http://www.wikidata.org/entity/Q61");
    Property property = ResourceFactory.createProperty("http://www.wikidata.org/prop/direct/P551");

    Statement statement = ResourceFactory.createStatement(subject, property, object);

    model.add(statement);

    CorroborativeGraph cg =
        factChecking.checkFacts(model, 2, true, PathGeneratorType.wikidataPathGenerator, false);

    System.out.println("Subject: " + statement.getSubject());
    System.out.println("Property: " + statement.getPredicate());
    System.out.println("Object: " + statement.getObject());
    System.out.println("Count paths: " + cg.getPathList().toArray().length);
    System.out.println(" Score:" + cg.getGraphScore());

    assertTrue(cg.getGraphScore() > 0);
  }
}
