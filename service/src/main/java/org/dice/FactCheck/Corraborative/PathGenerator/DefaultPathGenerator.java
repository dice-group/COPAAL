package org.dice.FactCheck.Corraborative.PathGenerator;

import java.util.HashMap;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Statement;
import org.dice.FactCheck.Corraborative.PathQuery;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * A class implementing callable to generate paths in parallel and returns PathQuery, a
 * a structure for realizing the path
 */

public class DefaultPathGenerator implements IPathGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPathGenerator.class);

  private final String queryBuilder;
  private final Statement input;
  private final int pathLength;
  private final QueryExecutioner queryExecutioner;
  HashMap<String, Integer> paths = new HashMap<String, Integer>();
  HashMap<String, String> intermediateNodes = new HashMap<String, String>();
  public PathQuery pathQuery;
  public String ontology = "\'http://dbpedia.org/ontology\'";

  public DefaultPathGenerator(
      String queryBuilder, Statement input, int pathLength, QueryExecutioner queryExecutioner) {
    this.queryBuilder = queryBuilder;
    this.input = input;
    this.pathLength = pathLength;
    this.queryExecutioner = queryExecutioner;
  }

  public PathQuery call() throws Exception {
    return returnQuery();
  }

  public PathQuery returnQuery() {
    if (pathLength == 1) {
      ParameterizedSparqlString paraPathQuery =
          new ParameterizedSparqlString(
              "SELECT ?p1 where "
                  + "\n { \n"
                  + queryBuilder
                  + " . \n"
                  + "FILTER(?p1 != <"
                  + input.getPredicate()
                  + ">)"
                  + "\n"
                  + "FILTER(strstarts(str(?p1),"
                  + ontology
                  + ")) \n "
                  + "}");
      paraPathQuery.setParam("s", input.getSubject());
      paraPathQuery.setParam("o", input.getObject());
      try(QueryExecution qe = queryExecutioner.getQueryExecution(paraPathQuery.asQuery());){
	      ResultSet result = qe.execSelect();
	
	      while (result.hasNext()) {
	        QuerySolution qs = result.next();
	        String path = qs.get("?p1").toString();
	        if (!paths.containsKey(path)) {
	          paths.put(path, pathLength);
	          intermediateNodes.put(path, "");
	        }
	      }
      }

    } else if (pathLength == 2) {

      String[] querySequence = queryBuilder.split(";");
      ParameterizedSparqlString paraPathQuery =
          new ParameterizedSparqlString(
              "SELECT ?p1 ?x1 ?p2 where \n"
                  + "{ \n "
                  + querySequence[0]
                  + "."
                  + querySequence[1]
                  + "."
                  + "\n"
                  + "FILTER(strstarts(str(?p1),"
                  + ontology
                  + "))"
                  + "FILTER(strstarts(str(?p2),"
                  + ontology
                  + "))"
                  + "FILTER(!ISLITERAL(?x1))"
                  + "\n "
                  + "}");

      paraPathQuery.setParam("s", input.getSubject());
      paraPathQuery.setParam("o", input.getObject());
      try(QueryExecution qe = queryExecutioner.getQueryExecution(paraPathQuery.asQuery());){
	      ResultSet result = qe.execSelect();
	
	      while (result.hasNext()) {
	        QuerySolution qs = result.next();
	        String path = qs.get("?p1").toString() + ";" + qs.get("?p2").toString();
	        if (!paths.containsKey(path)) {
	          paths.put(path, pathLength);
	          intermediateNodes.put(path, qs.get("?x1").toString());
	        }
	      }
      }

    } else if (pathLength == 3) {

      String[] querySequence = queryBuilder.split(";");
      ParameterizedSparqlString paraPathQuery =
          new ParameterizedSparqlString(
              "SELECT ?p1 ?x1 ?p2 ?x2 ?p3 where \n"
                  + "{ \n"
                  + querySequence[0]
                  + ".\n"
                  + querySequence[1]
                  + ".\n"
                  + querySequence[2]
                  + ".\n"
                  + "FILTER(?x1 != <"
                  + input.getObject().asNode()
                  + ">) \n"
                  + "FILTER(?x2 != <"
                  + input.getSubject().asNode()
                  + ">) \n"
                  + "FILTER(strstarts(str(?p1),"
                  + ontology
                  + "))"
                  + "FILTER(strstarts(str(?p2),"
                  + ontology
                  + "))"
                  + "FILTER(strstarts(str(?p3),"
                  + ontology
                  + "))"
                  + "}");
      paraPathQuery.setParam("s", input.getSubject());
      paraPathQuery.setParam("o", input.getObject());
      try(QueryExecution qe = queryExecutioner.getQueryExecution(paraPathQuery.asQuery());){
	      try {
	        ResultSet result = qe.execSelect();
	        while (result.hasNext()) {
	          QuerySolution qs = result.next();
	          String path =
	              qs.get("?p1").toString()
	                  + ";"
	                  + qs.get("?p2").toString()
	                  + ";"
	                  + qs.get("?p3").toString();
	          if (!paths.containsKey(path)) {
	            paths.put(path, pathLength);
	            intermediateNodes.put(path, qs.get("?x1").toString() + ";" + qs.get("?x2").toString());
	          }
	
	          break;
	        }
	
	      } catch (Exception ex) {
	        LOGGER.error("error in run this query" + qe.toString());
	        LOGGER.error(ex.getMessage());
	      }
      }
    }
    
    HashMap<String, HashMap<String, Integer>> pathBuilder =
            new HashMap<String, HashMap<String, Integer>>();
    pathBuilder.put(queryBuilder, paths);
    this.pathQuery = new PathQuery(pathBuilder, intermediateNodes);

    return this.pathQuery;
  }
}
