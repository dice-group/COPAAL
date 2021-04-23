package org.dice_research.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;

/**
 * Class containing SPARQL queries 
 * TODO: not really an util class, maybe rename it to QueryHolder?
 *
 */
public class SPARQLUtil {
	
	

	/**
	 * Retrieves the objects of statements with a given subject and predicate
	 * 
	 * @param executioner the query executioner
	 * @param subject     the statement's subject
	 * @param predicate   the statement's predicate
	 * @return
	 */
	public static Set<Node> getObjects(QueryExecutioner executioner, Node subject, Property predicate) {
		Set<Node> types = new HashSet<Node>();
		SelectBuilder typeBuilder = buildSelect(null, null, subject, predicate, NodeFactory.createVariable("x"));

		Query typeQuery = typeBuilder.build();
		try (QueryExecution queryExecution = executioner.getQueryExecution(typeQuery)) {
			ResultSet resultSet = queryExecution.execSelect();
			while (resultSet.hasNext()) {
				types.add(resultSet.next().get("x").asNode());
			}
		}
		return types;
	}

	/**
	 * Retrieves the count of all occurrences of a given subject and predicate with
	 * a given object
	 * 
	 * @param executioner the query executioner
	 * @param subject     the statement's subject
	 * @param predicate   the statement's predicate
	 * @param object      the statement's object
	 * @return
	 */
	public static int countPredicateOccurrences(QueryExecutioner executioner, Node subject, Property predicate,
			Node object) {
		SelectBuilder occurrenceBuilder = buildSelect("count(*)", "?c", subject, predicate, object);
		return returnCount(executioner, occurrenceBuilder, "?c");
	}

	/**
	 * Retrieves the count of all occurrences of a given subject and predicate with
	 * a set of different objects
	 * 
	 * @param executioner the query executioner
	 * @param subject     the statement's subject
	 * @param predicate   the statement's predicate
	 * @param objectTypes the set of objects we want to count with
	 * @return
	 */
	public static int countOccurrences(QueryExecutioner executioner, Node subject, Property predicate,
			Set<Node> objectTypes) {
		SelectBuilder occurrenceBuilder = buildSelectMultipleObj("count(*)", "?c", subject, predicate, objectTypes);
		return returnCount(executioner, occurrenceBuilder, "?c");
	}

	/**
	 * Retrieves the count of distinct subjects present in the graph with a given
	 * property
	 * 
	 * @param executioner the query executioner
	 * @param predicate   the property we want to check
	 * @return
	 */
	public static int countSubjectsWithProperty(QueryExecutioner executioner, Property predicate) {
		SelectBuilder occurrenceBuilder = buildSelect("count (distinct ?s)", "?c", NodeFactory.createVariable("s"),
				predicate, NodeFactory.createVariable("o"));
		return returnCount(executioner, occurrenceBuilder, "?c");
	}

	/**
	 * Retrieves the count of distinct subjects present in the graph with a given
	 * property
	 * 
	 * @param executioner the query executioner
	 * @param predicate   the property we want to check
	 * @return
	 */
	public static int countObjectsWithProperty(QueryExecutioner executioner, Property predicate) {
		SelectBuilder occurrenceBuilder = buildSelect("count (distinct ?o)", "?c", NodeFactory.createVariable("s"),
				predicate, NodeFactory.createVariable("o"));
		return returnCount(executioner, occurrenceBuilder, "?c");
	}

	/**
	 * Retrieves the result of a select count query as an integer
	 * 
	 * @param executioner
	 * @param builder
	 * @return
	 */
	public static int returnCount(QueryExecutioner executioner, SelectBuilder builder, String holdingVar) {
		Query queryOccurrence = builder.build();
		int count_Occurrence = 0;
		try (QueryExecution queryExecution = executioner.getQueryExecution(queryOccurrence)) {
			ResultSet resultSet = queryExecution.execSelect();
			if (resultSet.hasNext())
				count_Occurrence = resultSet.next().get(holdingVar).asLiteral().getInt();
		}
		return count_Occurrence;
	}

	/**
	 * Builds a select query with a single statement in the where clause
	 * 
	 * @param param    what do we want to select
	 * @param var      the variable name we want param to return as
	 * @param subject  the subject of the statement
	 * @param property the predicate of the statement
	 * @param object   the object of the statement
	 * @return a select query
	 */
	private static SelectBuilder buildSelect(String param, String var, Node subject, Property property, Node object) {
		SelectBuilder selectBuilder = new SelectBuilder();
		addVarClause(selectBuilder, param, var);
		selectBuilder.addWhere(subject, property, object);
		return selectBuilder;
	}

	/**
	 * Builds a select query with a multiple statements inside the where clause
	 * 
	 * @param param    what do we want to select
	 * @param var      the variable name we want param to return as
	 * @param subject  the subject of the statements
	 * @param property the predicate of the statements
	 * @param objects  the objects of the statements
	 * @return a select query
	 */
	private static SelectBuilder buildSelectMultipleObj(String param, String var, Node subject, Property property,
			Set<Node> objects) {
		SelectBuilder selectBuilder = new SelectBuilder();
		addVarClause(selectBuilder, param, var);

		Iterator<Node> objIterator = objects.iterator();
		while (objIterator.hasNext()) {
			selectBuilder.addWhere(subject, property, objIterator.next());
		}
		return selectBuilder;
	}

	/**
	 * Adds the var clause to a select query
	 * 
	 * @param selectBuilder the select query
	 * @param param         what do we want to select
	 * @param var           the variable name we want param to return as
	 */
	private static void addVarClause(SelectBuilder selectBuilder, String param, String var) {
		try {
			if (param != null || var != null) {
				selectBuilder.addVar(param, var);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
