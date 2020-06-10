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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the approximation of the NPMI.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class NPMICalculator implements Callable<Result> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NPMICalculator.class);

    public String path;
    public String intermediateNodes;
    public Statement inputStatement;
    public int pathLength;
    public String builder;
    public int count_predicate_Occurrence;
    public int count_subject_Triples;
    public int count_object_Triples;
    public Set<Node> SubjectType;
    public Set<Node> ObjectType;
    private QueryExecutioner queryExecutioner;

    public NPMICalculator(String path, String builder, Statement inputStatement, String intermediateNodes,
            int pathLength, int count_predicate_Occurrence, int count_subject_Triples, int count_object_Triples,
            Set<Node> SubjectType, Set<Node> ObjectType, QueryExecutioner queryExecutioner) {
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
    }

    public double calculatePMIScore() throws ParseException {
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

        try {
            if (pathLength == 3) {
                String[] querySequence = builder.split(";");

                String firstPath = querySequence[0].split(" ")[0].trim() + " <" + path.split(";")[0] + "> "
                        + querySequence[0].split(" ")[2].trim();
                String secondPath = querySequence[1].split(" ")[0].trim() + " <" + path.split(";")[1] + "> "
                        + querySequence[1].split(" ")[2].trim();
                String thirdPath = querySequence[2].split(" ")[0].trim() + " <" + path.split(";")[2] + "> "
                        + querySequence[2].split(" ")[2].trim();

                String pathQueryString = "select (sum(?b3*?k) as ?sum) where { \n"
                        + "select (count(*) as ?b3) (?b2*?b1 as ?k) ?x1 where { \n" + firstPath + " .\n"
                        + subTypeTriples + "{ \n" + "Select (count(*) as ?b2) ?x1 ?b1 where { \n" + secondPath + "{ \n"
                        + "select (count(*) as ?b1) ?x2 where { \n" + thirdPath + ". \n" + objTypeTriples
                        + "} group by ?x2\n" + "}\n" + "} group by ?b1 ?x1\n" + "}\n" + "} group by ?x1 ?b2 ?b1\n"
                        + "}\n";

                Query pathQuery = QueryFactory.create(pathQueryString);
                QueryExecution pathQueryExecution = queryExecutioner.getQueryExecution(pathQuery);
                double count_Path_Occurrence = pathQueryExecution.execSelect().next().get("?sum").asLiteral()
                        .getDouble();
                pathQueryExecution.close();

                String pathPredicateQueryString = "Select (count(*) as ?c) where {\n" + firstPath + " .\n"
                        + subTypeTriples + secondPath + " .\n" + thirdPath + " .\n" + objTypeTriples + predicateTriple
                        + "\n" + "}\n";

                Query pathPredicateQuery = QueryFactory.create(pathPredicateQueryString);
                QueryExecution predicatePathQueryExecution = queryExecutioner.getQueryExecution(pathPredicateQuery);

                double count_path_Predicate_Occurrence = predicatePathQueryExecution.execSelect().next().get("?c")
                        .asLiteral().getDouble();
                predicatePathQueryExecution.close();

                return npmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);
            } else if (pathLength == 2) {
                String[] querySequence = builder.split(";");

                String firstPath = querySequence[0].split(" ")[0].trim() + " <" + path.split(";")[0] + "> "
                        + querySequence[0].split(" ")[2].trim();
                String secondPath = querySequence[1].split(" ")[0].trim() + " <" + path.split(";")[1] + "> "
                        + querySequence[1].split(" ")[2].trim();

                String pathQueryString = "Select (sum(?b1*?b2) as ?sum) where {\n"
                        + "select (count(*) as ?b2) ?b1 where { \n" + firstPath + " .\n" + subTypeTriples + "{ \n"
                        + "select (count(*) as ?b1) ?x1 where { \n" + secondPath + " .\n" + objTypeTriples
                        + "} group by ?x1\n" + "}\n" + "} group by ?b1\n" + "}\n";

                Query pathQuery = QueryFactory.create(pathQueryString);
                QueryExecution pathQueryExecution = queryExecutioner.getQueryExecution(pathQuery);
                double count_Path_Occurrence = pathQueryExecution.execSelect().next().get("?sum").asLiteral()
                        .getDouble();
                pathQueryExecution.close();

                String pathPredicateQueryString = "Select (count(*) as ?c) where {\n" + firstPath + " .\n"
                        + subTypeTriples + secondPath + " .\n" + objTypeTriples + predicateTriple + "\n" + "}\n";

                Query pathPredicateQuery = QueryFactory.create(pathPredicateQueryString);

                QueryExecution pathPredicateQueryExecution = queryExecutioner.getQueryExecution(pathPredicateQuery);
                double count_path_Predicate_Occurrence = pathPredicateQueryExecution.execSelect().next().get("?c")
                        .asLiteral().getDouble();
                pathPredicateQueryExecution.close();

                return npmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);

            } else {

                String firstPath = builder.split(" ")[0].trim() + " <" + path.split(";")[0] + "> "
                        + builder.split(" ")[2].trim();

                String pathQueryString = "Select (count(*) as ?sum) where {\n" + firstPath + " .\n" + subTypeTriples
                        + objTypeTriples + "}\n";

                Query pathQuery = QueryFactory.create(pathQueryString);
                QueryExecution pathQueryExecution = queryExecutioner.getQueryExecution(pathQuery);
                double count_Path_Occurrence = pathQueryExecution.execSelect().next().get("?sum").asLiteral()
                        .getDouble();
                pathQueryExecution.close();

                String pathPredicateQueryString = "Select (count(*) as ?c) where {\n" + firstPath + " .\n"
                        + subTypeTriples + objTypeTriples + predicateTriple + "\n" + "}\n";

                Query pathPredicateQuery = QueryFactory.create(pathPredicateQueryString);
                QueryExecution pathPredicateQueryExecution = queryExecutioner.getQueryExecution(pathPredicateQuery);

                double count_path_Predicate_Occurrence = pathPredicateQueryExecution.execSelect().next().get("?c")
                        .asLiteral().getDouble();
                pathPredicateQueryExecution.close();

                return npmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);
            }

        } catch (Exception e) {
            LOGGER.info("Exception in calculating PMI score" + e.toString());
            return 0.0;
        }
    }

    /**
     * Method calculating the NPMI value of for the given path and path-predicate
     * counts based on the counts stored in the attributes of this class. The NPMI
     * is not calculated if the common occurrence of path and predicate is 0. In
     * that case, {@code -1.0} is returned.
     * 
     * @param count_Path_Occurrence           the occurrence of the path within the
     *                                        graph
     * @param count_path_Predicate_Occurrence common occurrence of the path and the
     *                                        predicate within the graph
     * @return the NPMI value for the given path and the predicate
     * @throws IllegalArgumentException thrown if one of the following counts is 0:
     *                                  the given {@code count_Path_Occurrence}, the
     *                                  {@link #count_predicate_Occurrence}, the
     *                                  {@link #count_subject_Triples} or the
     *                                  {@link #count_object_Triples}
     */
    public double npmiValue(double count_Path_Occurrence, double count_path_Predicate_Occurrence)
            throws IllegalArgumentException {
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
        // Path and predicate never occur together
        if (count_path_Predicate_Occurrence == 0) {
            // Since we know that A and B exist, there is a chance that they should occur
            // together. Since it never happens, we have to return -1
            return -1;
        }

        double logSubObjTriples = Math.log(count_subject_Triples) + Math.log(count_object_Triples);

        return calculateNPMI(Math.log(count_path_Predicate_Occurrence), logSubObjTriples,
                Math.log(count_Path_Occurrence), logSubObjTriples, Math.log(count_predicate_Occurrence),
                logSubObjTriples);
    }

    /**
     * Calculates the NPMI value for an event A, an event B and their common
     * occurrence AB based on the given <b>logarithmic</b> count values.
     * 
     * @param logCountAB the number of common occurrences of A and B.
     * @param logNormAB  the theoretical maximum of the common occurrence of A and
     *                   B.
     * @param logCountA  the occurrence count of A.
     * @param logNormA   the theoretical maximum of the occurrence count of A.
     * @param logCountB  the occurrence count of B.
     * @param logNormB   the theoretical maximum of the occurrence count of B.
     * @return the NPMI value
     */
    public static double calculateNPMI(double logCountAB, double logNormAB, double logCountA, double logNormA,
            double logCountB, double logNormB) {
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
        double score = calculatePMIScore();
        Result result = new Result(this.path, this.inputStatement.getPredicate(), score, this.builder,
                this.intermediateNodes, this.pathLength);
        return result;
    }

}
