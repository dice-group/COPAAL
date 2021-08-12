package org.dice.fact_check.corraborative.query;

import java.util.HashMap;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.springframework.stereotype.Component;

/*  A class to generate sparql queries to find paths of varying lengths
 */

@Component
public class SparqlQueryGenerator {

  public HashMap<String, Integer> sparqlQueries = new HashMap<String, Integer>();
  public Node subjectNode;
  public Node objectNode;

  public SparqlQueryGenerator() {

    this.subjectNode = NodeFactory.createVariable("s");
    this.objectNode = NodeFactory.createVariable("o");
  }

  public void generatorSparqlQueries(Statement input, int path_Length) throws ParseException {
    if (path_Length == 1) {
      // Queries of path length contain a single predicate
      Node predicate = NodeFactory.createVariable("p1");
      // To generate queries in both direction, we switch positions of subject and object
      Triple SUBJECT_OBJECT = new Triple(this.subjectNode, predicate, this.objectNode);
      Triple OBJECT_SUBJECT = new Triple(this.objectNode, predicate, this.subjectNode);

      // Generate and collect queries by extending the generic query with necessary where condition,
      // respectively
      sparqlQueries.put(SUBJECT_OBJECT.toString().replace("@", ""), path_Length);
      sparqlQueries.put(OBJECT_SUBJECT.toString().replace("@", ""), path_Length);
    } else if (path_Length == 2) {
      // For generating queries of path length 2, we need paths p1, p2
      // connecting subject and object respectively to and an intermediate node x1
      // s ---> p1 ---> x1 ---> p2 ---> o

      Node subjectPredicate = NodeFactory.createVariable("p1");
      Node objectPredicate = NodeFactory.createVariable("p2");
      Node intermediateNode = NodeFactory.createVariable("x1");

      // s ---> p1 ---> x1
      Triple INPUTSUBJECT_AS_SUBJECT =
          new Triple(this.subjectNode, subjectPredicate, intermediateNode);

      // x1---> p1 ---> s
      Triple INPUTSUBJECT_AS_OBJECT =
          new Triple(intermediateNode, subjectPredicate, this.subjectNode);

      // o ---> p2 ---> x1
      Triple INPUTOBJECT_AS_SUBJECT =
          new Triple(this.objectNode, objectPredicate, intermediateNode);

      // x1---> p2 ---> o
      Triple INPUTOBJECT_AS_OBJECT = new Triple(intermediateNode, objectPredicate, this.objectNode);

      // Generate and collect queries by extending the generic query with necessary where condition,
      // respectively
      sparqlQueries.put(
          INPUTSUBJECT_AS_SUBJECT.toString().replace("@", "")
              + ";"
              + INPUTOBJECT_AS_OBJECT.toString().replace("@", ""),
          path_Length);

      sparqlQueries.put(
          INPUTSUBJECT_AS_SUBJECT.toString().replace("@", "")
              + ";"
              + INPUTOBJECT_AS_SUBJECT.toString().replace("@", ""),
          path_Length);

      sparqlQueries.put(
          INPUTSUBJECT_AS_OBJECT.toString().replace("@", "")
              + ";"
              + INPUTOBJECT_AS_OBJECT.toString().replace("@", ""),
          path_Length);

      sparqlQueries.put(
          INPUTSUBJECT_AS_OBJECT.toString().replace("@", "")
              + ";"
              + INPUTOBJECT_AS_SUBJECT.toString().replace("@", ""),
          path_Length);

    } else if (path_Length == 3) {
      // For generating queries of path length 2, we need paths p1, p2, p3
      // connecting subject and object respectively to and an intermediate node x1, x2
      // s ---> p1 ---> x1 ---> p2 ---> x2 ---> p3 ---> o
      Node subjectPredicate = NodeFactory.createVariable("p1");
      Node objectPredicate = NodeFactory.createVariable("p3");
      Node intermediatePredicate = NodeFactory.createVariable("p2");
      Node firstIntermediateNode = NodeFactory.createVariable("x1");
      Node secondIntermediateNode = NodeFactory.createVariable("x2");

      // combinations for subject and object
      Triple INPUTSUBJECT_AS_SUBJECT =
          new Triple(this.subjectNode, subjectPredicate, firstIntermediateNode);
      Triple INPUTSUBJECT_AS_OBJECT =
          new Triple(firstIntermediateNode, subjectPredicate, this.subjectNode);
      Triple INPUTOBJECT_AS_SUBJECT =
          new Triple(this.objectNode, objectPredicate, secondIntermediateNode);
      Triple INPUTOBJECT_AS_OBJECT =
          new Triple(secondIntermediateNode, objectPredicate, this.objectNode);

      // Intermediate nodes combinations
      Triple SUBJECT_OBJECT =
          new Triple(firstIntermediateNode, intermediatePredicate, secondIntermediateNode);
      Triple OBJECT_SUBJECT =
          new Triple(secondIntermediateNode, intermediatePredicate, firstIntermediateNode);

      sparqlQueries.put(
          INPUTSUBJECT_AS_SUBJECT.toString().replace("@", "")
              + ";"
              + SUBJECT_OBJECT.toString().replace("@", "")
              + ";"
              + INPUTOBJECT_AS_SUBJECT.toString().replace("@", ""),
          path_Length);
      sparqlQueries.put(
          INPUTSUBJECT_AS_SUBJECT.toString().replace("@", "")
              + ";"
              + SUBJECT_OBJECT.toString().replace("@", "")
              + ";"
              + INPUTOBJECT_AS_OBJECT.toString().replace("@", ""),
          path_Length);
      /*			sparqlQueries.put(INPUTSUBJECT_AS_OBJECT.toString().replace("@", "")+";"+
      		SUBJECT_OBJECT.toString().replace("@", "")+";"+INPUTOBJECT_AS_SUBJECT.toString().replace("@", ""), path_Length);
      sparqlQueries.put(INPUTSUBJECT_AS_OBJECT.toString().replace("@", "")+";"+
      		SUBJECT_OBJECT.toString().replace("@", "")+";"+INPUTOBJECT_AS_OBJECT.toString().replace("@", ""), path_Length);
      sparqlQueries.put(INPUTSUBJECT_AS_SUBJECT.toString().replace("@", "")+";"+
      		OBJECT_SUBJECT.toString().replace("@", "")+";"+INPUTOBJECT_AS_SUBJECT.toString().replace("@", ""), path_Length);
      sparqlQueries.put(INPUTSUBJECT_AS_SUBJECT.toString().replace("@", "")+";"+
      		OBJECT_SUBJECT.toString().replace("@", "")+";"+INPUTOBJECT_AS_OBJECT.toString().replace("@", ""), path_Length);
      sparqlQueries.put(INPUTSUBJECT_AS_OBJECT.toString().replace("@", "")+";"+
      		OBJECT_SUBJECT.toString().replace("@", "")+";"+INPUTOBJECT_AS_SUBJECT.toString().replace("@", ""), path_Length);
      sparqlQueries.put(INPUTSUBJECT_AS_OBJECT.toString().replace("@", "")+";"+
      		OBJECT_SUBJECT.toString().replace("@", "")+";"+INPUTOBJECT_AS_OBJECT.toString().replace("@", ""), path_Length); */
    }
  }
}
