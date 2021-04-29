package org.dice_research.fc.tools;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFWriter;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

/**
 * A very simple class that transforms given triples into a reified representation with an expected
 * true or false value.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class DataTransformator implements StreamRDF {

  private Property VERACITY_PROPERTY =
      ResourceFactory.createProperty("http://swc2017.aksw.org/hasTruthValue");

  private int stmtCounter = 0;
  private StreamRDF output;
  private String stmtUriPrefix;
  private Literal expectedResult;

  public DataTransformator(String stmtUriPrefix, StreamRDF output, double expectedResult) {
    super();
    this.stmtUriPrefix = stmtUriPrefix;
    this.output = output;
    this.expectedResult =
        ResourceFactory.createTypedLiteral(Double.toString(expectedResult), XSDDatatype.XSDdouble);
  }

  public static void transform(String[] inputFiles, String outputFile, String stmtUriPrefix,
      double[] expectedResults) {
    DataTransformator transformator;
    int stmtId = 0;
    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile))) {
      StreamRDF writer = StreamRDFWriter.getWriterStream(os, Lang.TTL);
      writer.prefix("rdf", RDF.getURI());
      writer.prefix("xsd", XSD.getURI());
      writer.prefix("stmt", stmtUriPrefix);
      for (int i = 0; i < inputFiles.length; ++i) {
        transformator = new DataTransformator(stmtUriPrefix, writer, expectedResults[i]);
        transformator.setStmtCounter(stmtId);
        RDFDataMgr.parse(transformator, inputFiles[i]);
        stmtId = transformator.getStmtCounter();
      }
      writer.finish();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void start() {
    output.start();
  }

  @Override
  public void triple(Triple triple) {
    Resource stmt = ResourceFactory.createResource(stmtUriPrefix + stmtCounter);
    ++stmtCounter;
    output.triple(new Triple(stmt.asNode(), RDF.type.asNode(), RDF.Statement.asNode()));
    output.triple(new Triple(stmt.asNode(), RDF.subject.asNode(), triple.getSubject()));
    output.triple(new Triple(stmt.asNode(), RDF.predicate.asNode(), triple.getPredicate()));
    output.triple(new Triple(stmt.asNode(), RDF.object.asNode(), triple.getObject()));
    output.triple(new Triple(stmt.asNode(), VERACITY_PROPERTY.asNode(), expectedResult.asNode()));
  }

  @Override
  public void quad(Quad quad) {
    triple(quad.asTriple());
  }

  @Override
  public void base(String base) {
    output.base(base);
  }

  @Override
  public void prefix(String prefix, String iri) {
    output.prefix(prefix, iri);
  }

  @Override
  public void finish() {
    output.finish();
  }

  /**
   * @return the stmtCounter
   */
  public int getStmtCounter() {
    return stmtCounter;
  }

  /**
   * @param stmtCounter the stmtCounter to set
   */
  public void setStmtCounter(int stmtCounter) {
    this.stmtCounter = stmtCounter;
  }

  public static void main(String[] args) {
    String[] inputFiles = new String[] {"/home/micha/data/fb15k-237/true_triples_750.nt",
        "/home/micha/data/fb15k-237/false_triples_750.nt"};
    double[] expectedResults = new double[] {1.0, 0.0};
    String outputFile = "/home/micha/data/fb15k-237/fb15k-237.ttl";
    String stmtUriPrefix = "http://dice-research.org/data/fb15k-237.ttl#";

    transform(inputFiles, outputFile, stmtUriPrefix, expectedResults);
  }
}
