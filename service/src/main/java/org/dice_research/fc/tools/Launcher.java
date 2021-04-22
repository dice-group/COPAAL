package org.dice_research.fc.tools;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
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
import org.dice.FactCheck.Corraborative.Query.LocalQueryExecutioner;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice.FactCheck.Corraborative.Query.RemoteQueryExecutioner;
import org.dice.FactCheck.Corraborative.Query.SparqlQueryGenerator;
import org.dice.FactCheck.Corraborative.UIResult.CorroborativeGraph;
import org.dice.FactCheck.Corraborative.UIResult.create.DefaultPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {

	private static Property VERACITY_PROPERTY = ResourceFactory.createProperty("http://swc2017.aksw.org/hasTruthValue");

	private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

	public static void main(String[] args)
			throws FileNotFoundException, IOException, InterruptedException, ParseException {

		PropertyConfigurator.configure(Launcher.class.getClassLoader().getResource("log4j.properties"));
		ProgramParams pArgs = new ProgramParams();
		pArgs.logArgs();
		QueryExecutioner queryExecutioner = getQueryExecutioner(pArgs);

		// Read facts to check
		Model facts = ModelFactory.createDefaultModel();
		facts.read(pArgs.facts);
		
		FactChecking factChecking = new FactChecking(new SparqlQueryGenerator(), queryExecutioner,
				new CorroborativeGraph(), new DefaultPathFactory(), new DefaultPathGeneratorFactory());
		factChecking.setConfig(new Config());

		try (OutputStream os = new BufferedOutputStream(new FileOutputStream(pArgs.outputFile))) {
			StreamRDF writer = StreamRDFWriter.getWriterStream(os, Lang.TTL);
			writer.prefix("rdf", RDF.getURI());
			writer.prefix("xsd", XSD.getURI());
			//checkFactsInParallel(factChecking, facts, writer);
			writer.finish();
		}
	}

//	private static void checkFacts(FactChecking factChecking, Model facts, StreamRDF writer)
//			throws FileNotFoundException, InterruptedException, ParseException {
//		LOGGER.info("Starting fact checking...");
//		StmtIterator stmts = facts.listStatements(null, RDF.type, RDF.Statement);
//		int count = 0;
//		Model singleFact = ModelFactory.createDefaultModel();
//		Statement stmt;
//		Resource stmtR;
//		while (stmts.hasNext()) {
//			stmt = stmts.next();
//			stmtR = stmt.getSubject();
//			singleFact.add(stmtR.getPropertyResourceValue(RDF.subject),
//					stmtR.getPropertyResourceValue(RDF.predicate).as(Property.class),
//					stmtR.getPropertyResourceValue(RDF.object));
//			CorroborativeGraph graph = factChecking.checkFacts(singleFact, PATH_LENGTH, VIRTUAL_TYPES,
//					PathGeneratorType.defaultPathGenerator, false);
//
//			singleFact.removeAll();
//
//			writer.triple(new Triple(stmtR.asNode(), VERACITY_PROPERTY.asNode(), ResourceFactory
//					.createTypedLiteral(Double.toString(graph.getGraphScore()), XSDDatatype.XSDdouble).asNode()));
//
//			LOGGER.info("processed facts: " + count);
//			++count;
//		}
//	}
//
//	private static void checkFactsInParallel(FactChecking factChecking, Model facts, StreamRDF writer)
//			throws FileNotFoundException, InterruptedException, ParseException {
//		LOGGER.info("Starting fact checking...");
//		StmtIterator stmtIter = facts.listStatements(null, RDF.type, RDF.Statement);
//		List<Statement> stmts = new ArrayList<Statement>();
//		while (stmtIter.hasNext()) {
//			stmts.add(stmtIter.next());
//		}
//
//		stmts.stream().parallel().map(s -> s.getSubject()).forEach(r -> checkFact(factChecking, r, writer));
//	}
//
//	private static void checkFact(FactChecking factChecking, Resource stmtR, StreamRDF writer) {
//		Statement statement = new StatementImpl(stmtR.getPropertyResourceValue(RDF.subject),
//				stmtR.getPropertyResourceValue(RDF.predicate).as(Property.class),
//				stmtR.getPropertyResourceValue(RDF.object));
//		CorroborativeGraph graph;
//		try {
//			LOGGER.info("Checking fact: " + statement);
//			graph = factChecking.checkFacts(statement, PATH_LENGTH, VIRTUAL_TYPES,
//					PathGeneratorType.defaultPathGenerator, false);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//
//		synchronized (writer) {
//			writer.triple(new Triple(stmtR.asNode(), VERACITY_PROPERTY.asNode(), ResourceFactory
//					.createTypedLiteral(Double.toString(graph.getGraphScore()), XSDDatatype.XSDdouble).asNode()));
//		}
//		LOGGER.info("processed fact: " + stmtR.getURI());
//	}

	private static QueryExecutioner getQueryExecutioner(ProgramParams pArgs) {
		if (pArgs.model != null) {
			Model model = ModelFactory.createDefaultModel();
			model.read(pArgs.model);
			return new LocalQueryExecutioner(model);
		} else if (pArgs.endpoint != null) {
			return new RemoteQueryExecutioner(pArgs.endpoint);
		} else {
			LOGGER.error("Either the local file or the SPARQL endpoint of the KG needs to be provided.");
			throw new IllegalArgumentException();
		}
	}
}
