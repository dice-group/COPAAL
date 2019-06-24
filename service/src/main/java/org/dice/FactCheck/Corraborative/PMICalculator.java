package org.dice.FactCheck.Corraborative;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.jena.graph.Node;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PMICalculator implements Callable<Result>{

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
	final Logger LOGGER = LoggerFactory.getLogger(PMICalculator.class);
	private QueryExecutioner queryExecutioner;


	public PMICalculator(String path, String builder, Statement inputStatement, String intermediateNodes, int pathLength, int count_predicate_Occurrence,
						 int count_subject_Triples, int count_object_Triples, Set<Node> SubjectType, Set<Node> ObjectType, QueryExecutioner queryExecutioner) {
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


	public double calculatePMIScore() throws ParseException
	{
		// Find all subject and object types, we need them in query
		Iterator<Node> subTypeIterator = SubjectType.iterator();
		String subTypeTriples = "";
		while(subTypeIterator.hasNext())
		{
			subTypeTriples = subTypeTriples+"?s a <"+subTypeIterator.next()+"> . \n";
		}

		Iterator<Node> objTypeIterator = ObjectType.iterator();
		String objTypeTriples = "";
		while(objTypeIterator.hasNext())
		{
			objTypeTriples = objTypeTriples+"?o a <"+objTypeIterator.next()+"> . \n";
		}

		String predicateTriple = "?s <"+inputStatement.getPredicate()+"> ?o .";

		try {

			if(pathLength==3)
			{
				String[] querySequence = builder.split(";");

				String firstPath = querySequence[0].split(" ")[0].trim() +" <"+path.split(";")[0]+"> "+querySequence[0].split(" ")[2].trim();
				String secondPath = querySequence[1].split(" ")[0].trim()+" <"+path.split(";")[1]+"> "+querySequence[1].split(" ")[2].trim();
				String thirdPath = querySequence[2].split(" ")[0].trim()+" <"+path.split(";")[2]+"> "+querySequence[2].split(" ")[2].trim();

				String pathQueryString = "select (sum(?b3*?k) as ?sum) where { \n"
						+ "select (count(*) as ?b3) (?b2*?b1 as ?k) ?x1 where { \n"
						+ firstPath+" .\n"
						+subTypeTriples
						+"{ \n"
						+ "Select (count(*) as ?b2) ?x1 ?b1 where { \n"
						+ secondPath
						+"{ \n"
						+ "select (count(*) as ?b1) ?x2 where { \n"
						+ thirdPath+". \n"
						+objTypeTriples
						+ "} group by ?x2\n"
						+ "}\n"
						+ "} group by ?b1 ?x1\n"
						+ "}\n"
						+ "} group by ?x1 ?b2 ?b1\n"
						+ "}\n";


				Query pathQuery = QueryFactory.create(pathQueryString);
				QueryExecution pathQueryExecution = queryExecutioner.getQueryExecution(pathQuery);
				double count_Path_Occurrence = pathQueryExecution.execSelect().next().get("?sum").asLiteral().getDouble();
				pathQueryExecution.close();

				String pathPredicateQueryString = "Select (count(*) as ?c) where {\n"
						+ firstPath+" .\n"
						+subTypeTriples
						+ secondPath+" .\n"
						+thirdPath+" .\n"
						+objTypeTriples
						+predicateTriple+"\n"
						+ "}\n";

				Query pathPredicateQuery = QueryFactory.create(pathPredicateQueryString);
				QueryExecution predicatePathQueryExecution = queryExecutioner.getQueryExecution(pathPredicateQuery);

				double count_path_Predicate_Occurrence = predicatePathQueryExecution.execSelect().next().get("?c").asLiteral().getDouble();
				predicatePathQueryExecution.close();

				return pmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);
			}

			else if(pathLength==2)
			{	
				String[] querySequence = builder.split(";");

				String firstPath = querySequence[0].split(" ")[0].trim() +" <"+path.split(";")[0]+"> "+querySequence[0].split(" ")[2].trim();
				String secondPath = querySequence[1].split(" ")[0].trim()+" <"+path.split(";")[1]+"> "+querySequence[1].split(" ")[2].trim();

				String pathQueryString = "Select (sum(?b1*?b2) as ?sum) where {\n"
						+ "select (count(*) as ?b2) ?b1 where { \n"
						+ firstPath+" .\n"
						+subTypeTriples
						+ "{ \n"
						+ "select (count(*) as ?b1) ?x1 where { \n"
						+ secondPath+" .\n"
						+objTypeTriples
						+ "} group by ?x1\n"
						+ "}\n"
						+ "} group by ?b1\n"
						+ "}\n";

				Query pathQuery = QueryFactory.create(pathQueryString);
				QueryExecution pathQueryExecution = queryExecutioner.getQueryExecution(pathQuery);
				double count_Path_Occurrence = pathQueryExecution.execSelect().next().get("?sum").asLiteral().getDouble();
				pathQueryExecution.close();

				String pathPredicateQueryString = "Select (count(*) as ?c) where {\n"
						+ firstPath+" .\n"
						+subTypeTriples
						+ secondPath+" .\n"
						+objTypeTriples
						+predicateTriple+"\n"
						+ "}\n";

				Query pathPredicateQuery = QueryFactory.create(pathPredicateQueryString);


				QueryExecution pathPredicateQueryExecution = queryExecutioner.getQueryExecution(pathPredicateQuery);
				double count_path_Predicate_Occurrence = pathPredicateQueryExecution.execSelect().next().get("?c").asLiteral().getDouble();
				pathPredicateQueryExecution.close();

				return  pmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);

			}



			else
			{			

				String firstPath = builder.split(" ")[0].trim() +" <"+path.split(";")[0]+"> "+builder.split(" ")[2].trim();

				String pathQueryString = "Select (count(*) as ?sum) where {\n"
						+ firstPath+" .\n"
						+subTypeTriples
						+objTypeTriples
						+ "}\n";

				Query pathQuery = QueryFactory.create(pathQueryString);
				QueryExecution pathQueryExecution = queryExecutioner.getQueryExecution(pathQuery);
				double count_Path_Occurrence = pathQueryExecution.execSelect().next().get("?sum").asLiteral().getDouble();
				pathQueryExecution.close();

				String pathPredicateQueryString = "Select (count(*) as ?c) where {\n"
						+ firstPath+" .\n"
						+subTypeTriples
						+objTypeTriples
						+predicateTriple+"\n"
						+ "}\n";

				Query pathPredicateQuery = QueryFactory.create(pathPredicateQueryString);
				QueryExecution pathPredicateQueryExecution = queryExecutioner.getQueryExecution(pathPredicateQuery);

				double count_path_Predicate_Occurrence = pathPredicateQueryExecution.execSelect().next().get("?c").asLiteral().getDouble();
				pathPredicateQueryExecution.close();

				return pmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);
			}

		}

		catch(Exception e)
		{
			LOGGER.info("Exception in calculating PMI score"+e.toString());
			return 0.0;

		}



	}


	public double pmiValue(double count_Path_Occurrence, double count_path_Predicate_Occurrence) {
		try {

			BigDecimal NO_OF_SUBJECT_TRIPLES = new BigDecimal(Integer.toString(count_subject_Triples));
			BigDecimal NO_OF_OBJECT_TRIPLES = new BigDecimal(Integer.toString(count_object_Triples));
			BigDecimal NO_PATH_PREDICATE_TRIPLES = new BigDecimal(Double.toString(count_path_Predicate_Occurrence));
			BigDecimal SUBJECT_OBJECT_TRIPLES = NO_OF_SUBJECT_TRIPLES.multiply(NO_OF_OBJECT_TRIPLES);


			// add a small epsilon = 10 power -18 to avoid zero in logarithm
			double PROBABILITY_PATH_PREDICATE = NO_PATH_PREDICATE_TRIPLES.divide(SUBJECT_OBJECT_TRIPLES, 20, RoundingMode.HALF_EVEN).doubleValue() + 0.000000000000000001;
			BigDecimal NO_PATH_TRIPLES = new BigDecimal(Double.toString(count_Path_Occurrence));
			BigDecimal NO_OF_PREDICATE_TRIPLES = new BigDecimal(Integer.toString(count_predicate_Occurrence));
			double PROBABILITY_PATH = NO_PATH_TRIPLES.divide(SUBJECT_OBJECT_TRIPLES, 20, RoundingMode.HALF_EVEN).doubleValue();
			double PROBABILITY_PREDICATE = NO_OF_PREDICATE_TRIPLES.divide(SUBJECT_OBJECT_TRIPLES, 20, RoundingMode.HALF_EVEN).doubleValue();

			return Math.log(PROBABILITY_PATH_PREDICATE / (PROBABILITY_PATH * PROBABILITY_PREDICATE)) / -Math.log(PROBABILITY_PATH_PREDICATE);
		}

		catch (Exception ex){
			LOGGER.info("Exception in calculating PMI value "+ex.toString());
			return 0.0;
		}
	}

	public double pmiValue1(double count_Path_Occurrence, double count_path_Predicate_Occurrence) {
		try {

			BigDecimal NO_OF_SUBJECT_TRIPLES = new BigDecimal(Integer.toString(count_subject_Triples));
			BigDecimal NO_OF_OBJECT_TRIPLES = new BigDecimal(Integer.toString(count_object_Triples));
			BigDecimal NO_PATH_PREDICATE_TRIPLES = new BigDecimal(Double.toString(count_path_Predicate_Occurrence));
			BigDecimal SUBJECT_OBJECT_TRIPLES = NO_OF_SUBJECT_TRIPLES.multiply(NO_OF_OBJECT_TRIPLES);


			// add a small epsilon = 10 power -10 to avoid zero in logarithm
			double PROBABILITY_PATH_PREDICATE = NO_PATH_PREDICATE_TRIPLES.divide(SUBJECT_OBJECT_TRIPLES, 20, RoundingMode.HALF_EVEN).doubleValue() + 0.00000000001;
			BigDecimal NO_PATH_TRIPLES = new BigDecimal(Double.toString(count_Path_Occurrence));
			BigDecimal NO_OF_PREDICATE_TRIPLES = new BigDecimal(Integer.toString(count_predicate_Occurrence));
			double PROBABILITY_PATH = NO_PATH_TRIPLES.divide(SUBJECT_OBJECT_TRIPLES, 20, RoundingMode.HALF_EVEN).doubleValue();
			double PROBABILITY_PREDICATE = NO_OF_PREDICATE_TRIPLES.divide(SUBJECT_OBJECT_TRIPLES, 20, RoundingMode.HALF_EVEN).doubleValue();

			return Math.log(Math.log(PROBABILITY_PATH_PREDICATE) - Math.log(PROBABILITY_PATH) - Math.log(PROBABILITY_PREDICATE)) / -Math.log(PROBABILITY_PATH_PREDICATE);
		}

		catch (Exception ex){
			LOGGER.info("Exception in calculating PMI value "+ex.toString());
			return 0.0;
		}
	}


	public Result call() throws Exception {

		double score = calculatePMIScore();
		Result result = new Result(this.path, this.inputStatement.getPredicate(), score, this.builder, this.intermediateNodes, this.pathLength);
		return result;

	}

}
