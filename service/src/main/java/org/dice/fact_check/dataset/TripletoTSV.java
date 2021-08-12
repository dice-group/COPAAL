package org.dice.fact_check.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.retry.core.QueryExecutionFactoryRetry;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.lang.sparql_11.ParseException;

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
