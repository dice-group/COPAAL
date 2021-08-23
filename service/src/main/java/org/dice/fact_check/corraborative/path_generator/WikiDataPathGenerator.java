package org.dice.fact_check.corraborative.path_generator;

import java.util.HashMap;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Statement;
import org.dice.fact_check.corraborative.PathQuery;
import org.dice.fact_check.corraborative.query.QueryExecutioner;

public class WikiDataPathGenerator implements IPathGenerator {

  private int pathLength;
  private String queryBuilder;
  private Statement input;
  private QueryExecutioner queryExecutioner;

  private HashMap<String, Integer> paths = new HashMap<String, Integer>();
  private HashMap<String, String> intermediateNodes = new HashMap<String, String>();

  public WikiDataPathGenerator(
      String queryBuilder, Statement input, int pathLength, QueryExecutioner queryExecutioner) {
    this.pathLength = pathLength;
    this.queryBuilder = queryBuilder;
    this.input = input;
    this.queryExecutioner = queryExecutioner;
  }

  @Override
  public PathQuery call() throws Exception {
    return returnQuery();
  }

  @Override
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
                  // + "FILTER(!ISLITERAL(?x1))"
                  // + "\n "
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
                  + "}");
      paraPathQuery.setParam("s", input.getSubject());
      paraPathQuery.setParam("o", input.getObject());
      try(QueryExecution qe = queryExecutioner.getQueryExecution(paraPathQuery.asQuery());){
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
	      }
      }
    }
    HashMap<String, HashMap<String, Integer>> pathBuilder =
            new HashMap<String, HashMap<String, Integer>>();
	pathBuilder.put(queryBuilder, paths);

    return new PathQuery(pathBuilder, intermediateNodes);
  }
}
