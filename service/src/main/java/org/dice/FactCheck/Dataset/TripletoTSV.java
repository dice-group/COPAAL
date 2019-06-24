package org.dice.FactCheck.Dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.retry.core.QueryExecutionFactoryRetry;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.FactCheck.Corraborative.FactChecking;

public class TripletoTSV {
	
public static void main(String[] args) throws InterruptedException, ParseException, IOException {
		
	BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/home/datascienceadmin/dbpedia.tsv")));
	
	ParameterizedSparqlString pss = new ParameterizedSparqlString("Select ?s ?p ?o {"
			+ "Graph <http://dbpedia.org> {"
			+ "?s ?p ?o } }");
	QueryExecutionFactory qef = new QueryExecutionFactoryHttp("http://131.234.29.111:8890/sparql");
	qef = new QueryExecutionFactoryRetry(qef, 5, 1000);
	QueryExecution qe = qef.createQueryExecution(pss.asQuery());
	ResultSet result = qe.execSelect();
	
	while(result.hasNext())
	{
		StringBuilder sb = new StringBuilder();
		QuerySolution qs = result.next();
		sb.append("<"+qs.get("?s")+">");
		sb.append("\t");
		sb.append("<"+qs.get("?p")+">");
		sb.append("\t");
		sb.append("<"+qs.get("?o")+">");
		sb.append("\n");
		writer.write(sb.toString());
	}

	writer.flush();
	writer.close();
	}	

}
