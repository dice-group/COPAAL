package org.dice.fact_check.corraborative;

import java.io.FileNotFoundException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.fact_check.corraborative.path_generator.DefaultPathGeneratorFactory;
import org.dice.fact_check.corraborative.path_generator.IPathGeneratorFactory.PathGeneratorType;
import org.dice.fact_check.corraborative.query.QueryExecutioner;
import org.dice.fact_check.corraborative.query.SparqlQueryGenerator;
import org.dice.fact_check.corraborative.ui_result.CorroborativeGraph;
import org.dice.fact_check.corraborative.ui_result.create.DefaultPathFactory;

public class FactCheckingDemo {

  public static void main(String[] args)
      throws InterruptedException, FileNotFoundException, ParseException {

    org.apache.log4j.PropertyConfigurator.configure(
        FactChecking.class.getClassLoader().getResource("properties/log4j.properties"));
    FactChecking factChecking =
        new FactChecking(
            new SparqlQueryGenerator(),
            new QueryExecutioner("http://131.234.29.111:8890/sparql"),
            new CorroborativeGraph(),
            new DefaultPathFactory(),
            new DefaultPathGeneratorFactory(),
            "http://dbpedia.org");

    factChecking.checkFacts(getTestModel(), 2, true, PathGeneratorType.defaultPathGenerator, false);
  }

  public static Model getTestModel() {
    final Model model = ModelFactory.createDefaultModel();
    Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Bill_Gates");
    Resource object = ResourceFactory.createResource("http://dbpedia.org/resource/United_States");
    Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/nationality");
    Statement statement = ResourceFactory.createStatement(subject, property, object);
    model.add(statement);
    return model;
  }
}