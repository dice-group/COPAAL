package org.dice_research.fc.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.apache.jena.atlas.lib.ProgressMonitor;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.system.ProgressStreamRDF;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is more or less a simple script which connects entities with a given property that are
 * already connected with a given property path. This is mainly used to link entities that share the
 * same label or something similar and is a very very very basic variant of the typically very
 * complex task of link discovery.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class SimpleShortcutAdder {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleShortcutAdder.class);

  private QueryExecutionFactory qef;

  public SimpleShortcutAdder(QueryExecutionFactory qef) {
    super();
    this.qef = qef;
  }

  public void run(String propertyPath, String propertyIri, String outputFile) {
    Property property = ResourceFactory.createProperty(propertyIri);

    try (Writer writer = new FileWriter(outputFile)) {
      // Create output stream
      StreamRDF outStream = StreamRDFLib.writer(writer);
      ProgressMonitor monitor = ProgressMonitor.create(LOGGER, "Created triples", 10000, 10);
      outStream = new ProgressStreamRDF(outStream, monitor);

      // Execute the query
      monitor.start();
      outStream.start();
      LOGGER.info("Starting insertion of {} for {}...", propertyIri, propertyPath);
      run(propertyPath, property, outStream);
      monitor.finish();
      outStream.finish();
      LOGGER.info("Finished insertion of {} for {}.", propertyIri, propertyPath);
    } catch (IOException e) {
      LOGGER.error("Error while writing output file.", e);
    }
  }

  private void run(String propertyPath, Property property, StreamRDF outStream) {
    QueryExecution qe = null;
    Node pNode = property.asNode();
    try {
      qe = qef.createQueryExecution(createQuery(propertyPath));
      ResultSet rs = qe.execSelect();
      QuerySolution qs;
      while (rs.hasNext()) {
        qs = rs.next();
        // for each result, create a new triple and add it to the output stream
        outStream
            .triple(new Triple(qs.getResource("s").asNode(), pNode, qs.getResource("o").asNode()));
      }
    } finally {
      qe.close();
    }
  }

  private String createQuery(String propertyPath) {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ?s ?o WHERE { ?s ");
    builder.append(propertyPath);
    builder.append(" ?o }");
    return builder.toString();
  }

  public static void main(String[] args) {
    String endpoint = "https://frockg.ontotext.com/repositories/COPAAL";
    // List the property paths and the property IRIs which should be inserted for this path here as
    // pair ([0]=path,[1]=property)
    String[][] path2Property = new String[][] {
            {"<http://linkedlifedata.com/resource/drugcentral/synonym>/^<http://www.w3.org/2004/02/skos/core#prefLabel>",
                    "http://frockg.eu/similarLabel"},
            {"<http://linkedlifedata.com/resource/drugcentral/synonym>/^<http://www.w3.org/2004/02/skos/core#altLabel>",
                    "http://frockg.eu/similarLabel"},
            {"<http://linkedlifedata.com/resource/drugcentral/structName>/^<http://www.w3.org/2004/02/skos/core#prefLabel>",
                    "http://frockg.eu/similarLabel"},
            {"<http://linkedlifedata.com/resource/drugcentral/structName>/^<http://www.w3.org/2004/02/skos/core#altLabel>",
                    "http://frockg.eu/similarLabel"},
            {"<http://linkedlifedata.com/resource/drugcentral/synonym_lower>/^<http://www.w3.org/2004/02/skos/core#prefLabel>",
                    "http://frockg.eu/similarLabel"},
            {"<http://linkedlifedata.com/resource/drugcentral/synonym_lower>/^<http://www.w3.org/2004/02/skos/core#altLabel>",
                    "http://frockg.eu/similarLabel"},
            {"<http://linkedlifedata.com/resource/drugcentral/definition>/^<http://www.w3.org/2004/02/skos/core#definition>",
                    "http://frockg.eu/similarDefinition"}
        };

    File outputDir = new File("addedTriples");
    if (!outputDir.exists()) {
      outputDir.mkdir();
    }

    try (QueryExecutionFactory qef =
        new QueryExecutionFactoryPaginated(new QueryExecutionFactoryHttp(endpoint))) {
      SimpleShortcutAdder adder = new SimpleShortcutAdder(qef);
      // For each pair ...
      for (int i = 0; i < path2Property.length; ++i) {
        // ... create a new output file name
        String outputFile =
            outputDir.getAbsolutePath() + File.separator + path2Property[i][0].hashCode() + ".nt";
        LOGGER.info("Results are written to {}.", outputFile);
        // ... execute the adder
        adder.run(path2Property[i][0], path2Property[i][1], outputFile);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
