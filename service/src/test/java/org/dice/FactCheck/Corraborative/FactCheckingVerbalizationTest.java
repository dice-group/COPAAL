package org.dice.FactCheck.Corraborative;

import static org.junit.Assert.assertTrue;
import java.io.FileNotFoundException;
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
public class FactCheckingVerbalizationTest {

  @Autowired private QueryExecutioner queryExecutioner;

  @Autowired private FactChecking factChecking;

  @Autowired private Config config;

  @Test
  public void VerbalizationForWikiPathGenerationShouldWork()
      throws FileNotFoundException, InterruptedException, ParseException {
    final Model model = ModelFactory.createDefaultModel();
    // https://en.wikibooks.org/wiki/SPARQL/Prefixes
    Resource subject = ResourceFactory.createResource("http://www.wikidata.org/entity/Q76");
    Resource object = ResourceFactory.createResource("http://www.wikidata.org/entity/Q61");
    Property property = ResourceFactory.createProperty("http://www.wikidata.org/prop/direct/P551");

    Statement statement = ResourceFactory.createStatement(subject, property, object);

    model.add(statement);

    CorroborativeGraph cg =
        factChecking.checkFacts(model, 2, true, PathGeneratorType.wikidataPathGenerator, true);

    System.out.println("Subject: " + statement.getSubject());
    System.out.println("Property: " + statement.getPredicate());
    System.out.println("Object: " + statement.getObject());
    System.out.println("Count paths: " + cg.getPathList().toArray().length);
    System.out.println(" Score:" + cg.getGraphScore());

    assertTrue(!cg.getPathList().get(0).getPathText().equals("Not available"));
  }

  @Test
  public void VerbalizationNotNeccesseryForWikiPathGenerationShouldNotBringResult()
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

    assertTrue(cg.getPathList().get(0).getPathText().equals("Not available"));
  }

  @Test
  public void VerbalizationForDefaultPathGenerationShouldWork()
      throws FileNotFoundException, InterruptedException, ParseException {
    final Model model = ModelFactory.createDefaultModel();
    // https://en.wikibooks.org/wiki/SPARQL/Prefixes
    Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Nia_Gill");
    Resource object =
        ResourceFactory.createResource("http://dbpedia.org/resource/Bachelor_of_Arts");
    Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/education");

    Statement statement = ResourceFactory.createStatement(subject, property, object);

    model.add(statement);

    CorroborativeGraph cg =
        factChecking.checkFacts(model, 2, true, PathGeneratorType.defaultPathGenerator, true);

    System.out.println("Subject: " + statement.getSubject());
    System.out.println("Property: " + statement.getPredicate());
    System.out.println("Object: " + statement.getObject());
    System.out.println("Count paths: " + cg.getPathList().toArray().length);
    System.out.println(" Score:" + cg.getGraphScore());

    assertTrue(!cg.getPathList().get(0).getPathText().equals("Not available"));
  }

  @Test
  public void VerbalizationNotNeccesseryForDefaultPathGenerationShouldNotBringResult()
      throws FileNotFoundException, InterruptedException, ParseException {
    final Model model = ModelFactory.createDefaultModel();
    // https://en.wikibooks.org/wiki/SPARQL/Prefixes
    Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Nia_Gill");
    Resource object =
        ResourceFactory.createResource("http://dbpedia.org/resource/Bachelor_of_Arts");
    Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/education");

    Statement statement = ResourceFactory.createStatement(subject, property, object);

    model.add(statement);

    CorroborativeGraph cg =
        factChecking.checkFacts(model, 2, true, PathGeneratorType.defaultPathGenerator, false);

    System.out.println("Subject: " + statement.getSubject());
    System.out.println("Property: " + statement.getPredicate());
    System.out.println("Object: " + statement.getObject());
    System.out.println("Count paths: " + cg.getPathList().toArray().length);
    System.out.println(" Score:" + cg.getGraphScore());

    assertTrue(cg.getPathList().get(0).getPathText().equals("Not available"));
  }
}
