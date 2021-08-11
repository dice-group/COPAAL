package org.dice.FactCheck.Corraborative;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice.FactCheck.Corraborative.filter.npmi.NPMIFilter;
import org.dice.FactCheck.Corraborative.filter.npmi.NPMIFilterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the approximation of the NPMI.
 *
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 */
public class NPMICalculator implements Callable<Result> {

  private static final Logger LOGGER = LoggerFactory.getLogger(NPMICalculator.class);
  int timeOut = 5000;

  private final String path;
  private final String intermediateNodes;
  private final Statement inputStatement;
  private final int pathLength;
  private final String builder;
  private final int count_predicate_Occurrence;
  private final int count_subject_Triples;
  private final int count_object_Triples;
  private final Set<Node> SubjectType;
  private final Set<Node> ObjectType;
  private final QueryExecutioner queryExecutioner;
  private final NPMIFilter filter;
  private final boolean vTy;

  public NPMICalculator(String path, String builder, Statement inputStatement,
      String intermediateNodes, int pathLength, int count_predicate_Occurrence,
      int count_subject_Triples, int count_object_Triples, Set<Node> SubjectType,
      Set<Node> ObjectType, QueryExecutioner queryExecutioner) {
    this(path, builder, inputStatement, intermediateNodes, pathLength, count_predicate_Occurrence,
        count_subject_Triples, count_object_Triples, SubjectType, ObjectType, queryExecutioner,
        null, false);
  }

  public NPMICalculator(String path, String builder, Statement inputStatement,
      String intermediateNodes, int pathLength, int count_predicate_Occurrence,
      int count_subject_Triples, int count_object_Triples, Set<Node> SubjectType,
      Set<Node> ObjectType, QueryExecutioner queryExecutioner, NPMIFilter filter) {
    this(path, builder, inputStatement, intermediateNodes, pathLength, count_predicate_Occurrence,
        count_subject_Triples, count_object_Triples, SubjectType, ObjectType, queryExecutioner,
        filter, false);
  }

  public NPMICalculator(String path, String builder, Statement inputStatement,
      String intermediateNodes, int pathLength, int count_predicate_Occurrence,
      int count_subject_Triples, int count_object_Triples, Set<Node> SubjectType,
      Set<Node> ObjectType, QueryExecutioner queryExecutioner, NPMIFilter filter, boolean vTy) {
    this.path = path;
    this.builder = builder;
    this.inputStatement = inputStatement;
    this.pathLength = pathLength;
    this.count_predicate_Occurrence = count_predicate_Occurrence;
    this.count_subject_Triples = count_subject_Triples;
    this.count_object_Triples = count_object_Triples;
    this.SubjectType = SubjectType;
    this.ObjectType = ObjectType;
    this.intermediateNodes = intermediateNodes;
    this.queryExecutioner = queryExecutioner;
    this.filter = filter;
    this.vTy = vTy;
  }

  private String generatePathPredicateQueryString(String subTypeTriples, String objTypeTriples,
      String[] querySequence, String predicateTriple, int pathLength) {

    if (pathLength == 1) {
      String firstPath = generatePath(querySequence, 0);

      return "Select (count(*) as ?c) where {\n" + firstPath + " .\n" + subTypeTriples
          + objTypeTriples + predicateTriple + "\n" + "}\n";
    }

    if (pathLength == 2) {
      String firstPath = generatePath(querySequence, 0);
      String secondPath = generatePath(querySequence, 1);

      return "Select (count(*) as ?c) where {\n" + firstPath + " .\n" + subTypeTriples + secondPath
          + " .\n" + objTypeTriples + predicateTriple + "\n" + "}\n";
    }

    if (pathLength == 3) {
      String firstPath = generatePath(querySequence, 0);
      String secondPath = generatePath(querySequence, 1);
      String thirdPath = generatePath(querySequence, 2);

      return new StringBuilder().append("Select (count(*) as ?c) where {\n").append(firstPath)
          .append(" .\n").append(subTypeTriples).append(secondPath).append(" .\n").append(thirdPath)
          .append(" .\n").append(objTypeTriples).append(predicateTriple).append("\n").append("}\n")
          .toString();
    }
    return null;
  }

  private String generatePathPredicateQueryString_vTy(String subjType, String objType,
      String[] querySequence, String predicateTriple, int pathLength) {

    if (pathLength == 1) {
      String firstPath = generatePath(querySequence, 0);

      return "Select (count(*) as ?c) where {\n" + firstPath + " .\n" + predicateTriple + "\n"
          + "}\n";
    }

    if (pathLength == 2) {
      String firstPath = generatePath(querySequence, 0);
      String secondPath = generatePath(querySequence, 1);
      return "Select (count(*) as ?c) where {select distinct ?s ?o {\n" + firstPath + " .\n"
          + secondPath + " .\n" + predicateTriple + "\n" + "}}\n";
    }

    if (pathLength == 3) {
      String firstPath = generatePath(querySequence, 0);
      String secondPath = generatePath(querySequence, 1);
      String thirdPath = generatePath(querySequence, 2);

      return new StringBuilder().append("Select (count(*) as ?c) where {select distinct ?s ?o {\n")
          .append(firstPath).append(" .\n").append(secondPath).append(" .\n").append(thirdPath)
          .append(" .\n").append(predicateTriple).append("\n").append("}}\n").toString();
    }
    // TODO: throw Exception for unsupported pathLength
    return "";
  }

  private String generatePathQueryString(String subTypeTriples, String objTypeTriples,
      String[] querySequence, int pathLength) {

    if (pathLength == 1) {
      String firstPath = generatePath(querySequence, 0);
      // TODO : why not as a ?c
      return "Select (count(*) as ?sum) where {\n" + firstPath + " .\n" + subTypeTriples
          + objTypeTriples + "}\n";
    }

    if (pathLength == 2) {
      String firstPath = generatePath(querySequence, 0);
      String secondPath = generatePath(querySequence, 1);

      return new StringBuilder().append("Select (sum(?b1*?b2) as ?c) where {\n")
          .append("select (count(*) as ?b2) ?b1 where { \n").append(firstPath).append(" .\n")
          .append(subTypeTriples).append("{ \n").append("select (count(*) as ?b1) ?x1 where { \n")
          .append(secondPath).append(" .\n").append(objTypeTriples).append("} group by ?x1\n")
          .append("}\n").append("} group by ?b1\n").append("}\n").toString();
    }

    if (pathLength == 3) {
      String firstPath = generatePath(querySequence, 0);
      String secondPath = generatePath(querySequence, 1);
      String thirdPath = generatePath(querySequence, 2);

      return new StringBuilder().append("select (sum(?b3*?k) as ?c) where { \n")
          .append("select (count(*) as ?b3) (?b2*?b1 as ?k) ?x1 where { \n").append(firstPath)
          .append(" .\n").append(subTypeTriples).append("{ \n")
          .append("Select (count(*) as ?b2) ?x1 ?b1 where { \n").append(secondPath).append("{ \n")
          .append("select (count(*) as ?b1) ?x2 where { \n").append(thirdPath).append(". \n")
          .append(objTypeTriples).append("} group by ?x2\n").append("}\n")
          .append("} group by ?b1 ?x1\n").append("}\n").append("} group by ?x1 ?b2 ?b1\n")
          .append("}\n").toString();
    }

    return null;
  }

  private String generatePathQueryString_vTy(String subjType, String objType,
      String[] querySequence, int pathLength) {

    if (pathLength == 1) {
      String firstPath = generatePath(querySequence, 0);
      return "Select (count(*) as ?c) where {\n" + firstPath + " .\n" + subjType + objType + "}\n";
    }

    if (pathLength == 2) {
      String firstPath = generatePath(querySequence, 0);
      String secondPath = generatePath(querySequence, 1);

      return "Select (count(*) as ?c) where {select distinct ?s ?o { " + firstPath + " . "
          + subjType + secondPath + " . " + objType + "}} ";
    }

    if (pathLength == 3) {
      String firstPath = generatePath(querySequence, 0);
      String secondPath = generatePath(querySequence, 1);
      String thirdPath = generatePath(querySequence, 2);

      return new StringBuilder().append("Select (count(*) as ?c) where {select distinct ?s ?o { ")
          .append(firstPath).append(" . ").append(subjType).append("{select distinct ?o1 ?o{")
          .append(secondPath).append(" . ").append(thirdPath).append(" . ").append(objType)
          .append(" } }").append("}} ").toString();
    }
    // TODO: throw Exception for unsupported pathLength
    return "";
  }

  private String generatePath(String[] querySequence, int order) {
    return querySequence[order].split(" ")[0].trim() + " <" + path.split(";")[order] + "> "
        + querySequence[order].split(" ")[2].trim();
  }

  public double calculatePMIScore() throws ParseException, NPMIFilterException {
    // Find all subject and object types, we need them in query
    Iterator<Node> subTypeIterator = SubjectType.iterator();
    String subTypeTriples = "";
    while (subTypeIterator.hasNext()) {
      subTypeTriples = subTypeTriples + "?s a <" + subTypeIterator.next() + "> . \n";
    }

    Iterator<Node> objTypeIterator = ObjectType.iterator();
    String objTypeTriples = "";
    while (objTypeIterator.hasNext()) {
      objTypeTriples = objTypeTriples + "?o a <" + objTypeIterator.next() + "> . \n";
    }

    String predicateTriple = "?s <" + inputStatement.getPredicate() + "> ?o .";
    String[] querySequence = builder.split(";");

    String pathQueryString =
        generatePathQueryString(subTypeTriples, objTypeTriples, querySequence, pathLength);

    long queryElapseTime = System.nanoTime();

    Query pathQuery = QueryFactory.create(pathQueryString);

    double count_Path_Occurrence = 0;
    try (QueryExecution pathQueryExecution = queryExecutioner.getQueryExecution(pathQuery);) {
      pathQueryExecution.setTimeout(timeOut, timeOut);
      count_Path_Occurrence =
          pathQueryExecution.execSelect().next().get("?c").asLiteral().getDouble();
    } catch (Exception ex) {
      LOGGER.info(ex.getMessage());
      count_Path_Occurrence = 0;
    }

    LOGGER.debug("---Query is : " + pathQueryString + " it tooks "
        + (double) (System.nanoTime() - queryElapseTime) / 1_000_000_000
        + " seconds count_Path_Occurrence is : " + count_Path_Occurrence + " ---------");
    queryElapseTime = System.nanoTime();

    String pathPredicateQueryString = generatePathPredicateQueryString(subTypeTriples,
        objTypeTriples, querySequence, predicateTriple, pathLength);

    Query pathPredicateQuery = QueryFactory.create(pathPredicateQueryString);

    double count_path_Predicate_Occurrence = 0;
    try (QueryExecution predicatePathQueryExecution =
        queryExecutioner.getQueryExecution(pathPredicateQuery);) {
      predicatePathQueryExecution.setTimeout(timeOut, timeOut);
      count_path_Predicate_Occurrence =
          predicatePathQueryExecution.execSelect().next().get("?c").asLiteral().getDouble();
    } catch (Exception ex) {
      LOGGER.info(ex.getMessage());
      count_path_Predicate_Occurrence = 0;
    }

    LOGGER.debug("---Query is : " + pathPredicateQuery + " it tooks "
        + (double) (System.nanoTime() - queryElapseTime) / 1_000_000_000
        + " seconds count_path_Predicate_Occurrence is : " + count_path_Predicate_Occurrence
        + " ---------");

    return npmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);
  }

  public double calculatePMIScore_vTy() throws ParseException, NPMIFilterException {
    // ignore types, consider subjects/objects of input-triple-predicate as virtual types

    String predicateTriple = "?s <" + inputStatement.getPredicate() + "> ?o .";
    String pathQueryString, pathPredicateQueryString;
    String[] querySequence = builder.split(";");
    String subjType = " filter(exists {?s <" + inputStatement.getPredicate() + ">  []}).";
    String objType = " filter(exists {[] <" + inputStatement.getPredicate() + ">  ?o}).";

    pathQueryString = generatePathQueryString_vTy(subjType, objType, querySequence, pathLength);

    Query pathQuery = QueryFactory.create(pathQueryString);

    LOGGER.info(pathQuery.toString());

    double count_Path_Occurrence = 0;
    try (QueryExecution pathQueryExecution = queryExecutioner.getQueryExecution(pathQuery);) {
      pathQueryExecution.setTimeout(timeOut, timeOut);

      LOGGER.info("timeOut : " + pathQueryExecution.getTimeout2());
      count_Path_Occurrence =
          pathQueryExecution.execSelect().next().get("?c").asLiteral().getDouble();
    } catch (Exception ex) {
      LOGGER.info(ex.getMessage());
      count_Path_Occurrence = 0;
    }

    LOGGER.info("count_Path_Occurrence : " + count_Path_Occurrence);


    pathPredicateQueryString = generatePathPredicateQueryString_vTy(subjType, objType,
        querySequence, predicateTriple, pathLength);

    Query pathPredicateQuery = QueryFactory.create(pathPredicateQueryString);

    LOGGER.info(pathPredicateQueryString.toString());

    double count_path_Predicate_Occurrence = 0;
    try (QueryExecution predicatePathQueryExecution =
        queryExecutioner.getQueryExecution(pathPredicateQuery);) {
      predicatePathQueryExecution.setTimeout(timeOut, timeOut);
      count_path_Predicate_Occurrence =
          predicatePathQueryExecution.execSelect().next().get("?c").asLiteral().getDouble();
    } catch (Exception ex) {
      LOGGER.info(ex.getMessage());
      count_path_Predicate_Occurrence = 0;
    }

    LOGGER.info("count_path_Predicate_Occurrence : " + count_path_Predicate_Occurrence);

    return npmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);
  }

  /**
   * Method calculating the NPMI value of for the given path and path-predicate counts based on the
   * counts stored in the attributes of this class. The NPMI is not calculated if the common
   * occurrence of path and predicate is 0. In that case, {@code 0} is returned.
   *
   * @param count_Path_Occurrence the occurrence of the path within the graph
   * @param count_path_Predicate_Occurrence common occurrence of the path and the predicate within
   *        the graph
   * @return the NPMI value for the given path and the predicate
   * @throws IllegalArgumentException thrown if one of the following counts is 0: the given {@code
   *     count_Path_Occurrence}, the {@link #count_predicate_Occurrence}, the
   *         {@link #count_subject_Triples} or the {@link #count_object_Triples}
   * @throws NPMIFilterException if the {@link NPMIFilter} rejects the calculated value.
   */
  public double npmiValue(double count_Path_Occurrence, double count_path_Predicate_Occurrence)
      throws IllegalArgumentException, NPMIFilterException {
    // If the predicate never occurs
    if (count_predicate_Occurrence == 0) {
      throw new IllegalArgumentException(
          "The given predicate does never occur. The NPMI is not defined for this case.");
    }
    // If the path never occurs
    if (count_Path_Occurrence == 0) {
      throw new IllegalArgumentException(
          "The given path does never occur. The NPMI is not defined for this case.");
    }
    // If subject or object types never occur
    if ((count_subject_Triples == 0) || (count_object_Triples == 0)) {
      throw new IllegalArgumentException(
          "The given number of triples for the subject or object type is 0. The NPMI is not defined for this case. Given occurrences is subject="
              + count_subject_Triples + " and object=" + count_object_Triples);
    }
    double logSubObjTriples = Math.log(count_subject_Triples) + Math.log(count_object_Triples);
    double npmi;

    // Path and predicate never occur together
    if (count_path_Predicate_Occurrence == 0) {
      // Since we know that A and B exist, there is a chance that they should occur
      // together. Since it never happens, we have to return -1
      // npmi = -1;
      npmi = 0;
    } else {
      npmi = calculateNPMI(Math.log(count_path_Predicate_Occurrence), logSubObjTriples,
          Math.log(count_Path_Occurrence), logSubObjTriples, Math.log(count_predicate_Occurrence),
          logSubObjTriples);
    }
    if ((filter != null) && (!filter.npmiIsOk(npmi, pathLength, count_path_Predicate_Occurrence,
        count_Path_Occurrence, count_predicate_Occurrence))) {
      throw new NPMIFilterException("The NPMI filter rejected the calculated NPMI.");
    }
    return npmi;
  }

  /**
   * Calculates the NPMI value for an event A, an event B and their common occurrence AB based on
   * the given <b>logarithmic</b> count values.
   *
   * @param logCountAB the number of common occurrences of A and B.
   * @param logNormAB the theoretical maximum of the common occurrence of A and B.
   * @param logCountA the occurrence count of A.
   * @param logNormA the theoretical maximum of the occurrence count of A.
   * @param logCountB the occurrence count of B.
   * @param logNormB the theoretical maximum of the occurrence count of B.
   * @return the NPMI value
   */
  public static double calculateNPMI(double logCountAB, double logNormAB, double logCountA,
      double logNormA, double logCountB, double logNormB) {
    // Calculate probabilities
    double logProbA = logCountA - logNormA;
    double logProbB = logCountB - logNormB;
    double logProbAB = logCountAB - logNormAB;

    // If the probability of AB is 1.0 (i.e., its log is 0.0)
    if (logProbAB == 0) {
      return 1.0;
    } else {
      return (logProbAB - logProbA - logProbB) / -logProbAB;
    }
  }

  public Result call() throws Exception {
    long startTime = System.nanoTime();
    Result result = new Result(this.path, this.inputStatement.getPredicate(), this.builder,
        this.intermediateNodes, this.pathLength);
    try {
      if (this.vTy) {
        result.score = calculatePMIScore_vTy();
      } else {
        result.score = calculatePMIScore();
      }
      result.hasLegalScore = true;
    } catch (Exception scoreCalcFailed) {
      result.hasLegalScore = false;
    }
    result.setElapsedTime(System.nanoTime() - startTime);
    return result;
  }
}
