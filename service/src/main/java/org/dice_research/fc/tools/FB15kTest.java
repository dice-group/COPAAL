package org.dice_research.fc.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFWriter;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.apache.log4j.PropertyConfigurator;
import org.dice.FactCheck.Corraborative.FactChecking;
import org.dice.FactCheck.Corraborative.Config.Config;
import org.dice.FactCheck.Corraborative.PathGenerator.DefaultPathGeneratorFactory;
import org.dice.FactCheck.Corraborative.PathGenerator.IPathGeneratorFactory.PathGeneratorType;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice.FactCheck.Corraborative.Query.SparqlQueryGenerator;
import org.dice.FactCheck.Corraborative.UIResult.CorroborativeGraph;
import org.dice.FactCheck.Corraborative.UIResult.create.DefaultPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FB15kTest {

	private static Property VERACITY_PROPERTY = ResourceFactory.createProperty("http://swc2017.aksw.org/hasTruthValue");

	/**
	 * TODO: This should be an input parameter of the program
	 */
	private static int PATH_LENGTH = 3;

	/**
	 * TODO: This should be an input parameter of the program
	 */
	private static boolean VIRTUAL_TYPES = false;

	private static final Logger LOGGER = LoggerFactory.getLogger(FB15kTest.class);

	public static void main(String[] args)
			throws FileNotFoundException, IOException, InterruptedException, ParseException {
		// Read KG
//	    Model kg = ModelFactory.createDefaultModel();
//	    try (InputStream is = new BufferedInputStream(
//	        new FileInputStream("train_valid_dr_n_types.nt"))) {
//	      kg.read(is, "", "nt");
//	    }
//	    QueryExecutioner queryExecutioner = new LocalQueryExecutioner(kg);
		PropertyConfigurator.configure(FB15kTest.class.getClassLoader().getResource("log4j.properties"));

		QueryExecutioner queryExecutioner = new QueryExecutioner();
		
		// Read facts to check
		Model facts = ModelFactory.createDefaultModel();
		try (InputStream is = new BufferedInputStream(new FileInputStream("fb15k-237.ttl"))) {
			facts.read(is, "", "ttl");
		}

		FactChecking factChecking = new FactChecking(new SparqlQueryGenerator(), queryExecutioner,
				new CorroborativeGraph(), new DefaultPathFactory(), new DefaultPathGeneratorFactory());
		queryExecutioner.setServiceRequestURL("http://lemming.cs.uni-paderborn.de:8890/sparql");
		factChecking.setConfig(new Config());

		try (OutputStream os = new BufferedOutputStream(new FileOutputStream("coopal-result.ttl"))) {
			StreamRDF writer = StreamRDFWriter.getWriterStream(os, Lang.TTL);
			writer.prefix("rdf", RDF.getURI());
			writer.prefix("xsd", XSD.getURI());
			checkFactsInParallel(factChecking, facts, writer);
			writer.finish();
		}
	}

	private static void checkFacts(FactChecking factChecking, Model facts, StreamRDF writer)
			throws FileNotFoundException, InterruptedException, ParseException {
		LOGGER.info("Starting fact checking...");
		StmtIterator stmts = facts.listStatements(null, RDF.type, RDF.Statement);
		int count = 0;
		Model singleFact = ModelFactory.createDefaultModel();
		Statement stmt;
		Resource stmtR;
		while (stmts.hasNext()) {
			stmt = stmts.next();
			stmtR = stmt.getSubject();
			singleFact.add(stmtR.getPropertyResourceValue(RDF.subject),
					stmtR.getPropertyResourceValue(RDF.predicate).as(Property.class),
					stmtR.getPropertyResourceValue(RDF.object));
			CorroborativeGraph graph = factChecking.checkFacts(singleFact, PATH_LENGTH, VIRTUAL_TYPES,
					PathGeneratorType.defaultPathGenerator, false);

			singleFact.removeAll();

			writer.triple(new Triple(stmtR.asNode(), VERACITY_PROPERTY.asNode(), ResourceFactory
					.createTypedLiteral(Double.toString(graph.getGraphScore()), XSDDatatype.XSDdouble).asNode()));

			LOGGER.info("processed facts: " + count);
			++count;
		}
	}

	private static void checkFactsInParallel(FactChecking factChecking, Model facts, StreamRDF writer)
			throws FileNotFoundException, InterruptedException, ParseException {
		LOGGER.info("Starting fact checking...");
		StmtIterator stmtIter = facts.listStatements(null, RDF.type, RDF.Statement);
		List<Statement> stmts = new ArrayList<Statement>();
		while (stmtIter.hasNext()) {
			stmts.add(stmtIter.next());
		}

		stmts.stream().parallel().map(s -> s.getSubject()).forEach(r -> checkFact(factChecking, r, writer));
	}

	private static void checkFact(FactChecking factChecking, Resource stmtR, StreamRDF writer) {
		Statement statement = new StatementImpl(stmtR.getPropertyResourceValue(RDF.subject),
				stmtR.getPropertyResourceValue(RDF.predicate).as(Property.class),
				stmtR.getPropertyResourceValue(RDF.object));
		CorroborativeGraph graph;
		try {
			LOGGER.info("Checking fact: " + statement);
			graph = factChecking.checkFacts(statement, PATH_LENGTH, VIRTUAL_TYPES,
					PathGeneratorType.defaultPathGenerator, false);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		synchronized (writer) {
			writer.triple(new Triple(stmtR.asNode(), VERACITY_PROPERTY.asNode(), ResourceFactory
					.createTypedLiteral(Double.toString(graph.getGraphScore()), XSDDatatype.XSDdouble).asNode()));
		}
		LOGGER.info("processed fact: " + stmtR.getURI());
	}
}
