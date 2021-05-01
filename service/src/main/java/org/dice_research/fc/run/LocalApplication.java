package org.dice_research.fc.run;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFWriter;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.apache.log4j.PropertyConfigurator;
import org.dice_research.fc.IFactChecker;
import org.dice_research.fc.data.FactCheckingResult;
import org.dice_research.fc.tools.ProgramParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Launches the application locally. <p>
 * It can read a file with reified facts and write the predictions to file.
 */
@SpringBootApplication
@ComponentScan("org.dice_research.fc.config")
public class LocalApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalApplication.class);

  public static void main(String[] args) throws FileNotFoundException, IOException {
    PropertyConfigurator.configure(LocalApplication.class.getClassLoader().getResource("log4j.properties"));
    ProgramParams pArgs = new ProgramParams();

    // Get context
    ConfigurableApplicationContext app =
        new SpringApplicationBuilder(LocalApplication.class).web(WebApplicationType.NONE).run(args);
    IFactChecker factChecker = app.getBean(IFactChecker.class);

    // Read facts to check
    Model facts = ModelFactory.createDefaultModel();
    facts.read(pArgs.facts);

    // write predictions one by one
    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(pArgs.outputFile))) {
      StreamRDF writer = StreamRDFWriter.getWriterStream(os, Lang.TTL);
      writer.prefix("rdf", RDF.getURI());
      writer.prefix("xsd", XSD.getURI());
      checkFacts(factChecker, facts, writer);
      writer.finish();
    }
  }

  private static void checkFacts(IFactChecker factChecker, Model facts, StreamRDF writer) {
    LOGGER.info("Starting fact checking...");
    Property VERACITY_PROPERTY =
        ResourceFactory.createProperty("http://swc2017.aksw.org/hasTruthValue");
    StmtIterator stmts = facts.listStatements(null, RDF.type, RDF.Statement);
    for (int c = 0; stmts.hasNext(); c++) {
      Statement stmt = stmts.next();
      Resource stmtR = stmt.getSubject();
      Statement fact = ResourceFactory.createStatement(stmtR.getPropertyResourceValue(RDF.subject),
          stmtR.getPropertyResourceValue(RDF.predicate).as(Property.class),
          stmtR.getPropertyResourceValue(RDF.object));

      FactCheckingResult result = factChecker.check(fact);

      writer.triple(new Triple(stmtR.asNode(), VERACITY_PROPERTY.asNode(),
          ResourceFactory
              .createTypedLiteral(Double.toString(result.getVeracityValue()), XSDDatatype.XSDdouble)
              .asNode()));

      LOGGER.info("processed facts: " + c);
    }
  }
}
