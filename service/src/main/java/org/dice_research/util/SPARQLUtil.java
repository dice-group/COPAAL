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
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;

/**
 * TODO: format these queries, lots of duplicate queries around
 *
 */
public class SPARQLUtil {

	public static Set<Node> getObjects(QueryExecutioner executioner, Resource subject, Property predicate) {
		Set<Node> types = new HashSet<Node>();
		SelectBuilder typeBuilder = new SelectBuilder().addPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		typeBuilder.addPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		typeBuilder.addWhere(subject, predicate, NodeFactory.createVariable("x"));

		Query typeQuery = typeBuilder.build();
		try (QueryExecution queryExecution = executioner.getQueryExecution(typeQuery)) {
			ResultSet resultSet = queryExecution.execSelect();
			while (resultSet.hasNext()) {
				types.add(resultSet.next().get("x").asNode());
			}
		}
		return types;
	}

	public static int countPredicateOccurrances(QueryExecutioner executioner, Node subject, Property property,
			Node objectType) {
		SelectBuilder occurrenceBuilder = new SelectBuilder();
		try {
			occurrenceBuilder.addVar("count(*)", "?c");
			occurrenceBuilder.addWhere(subject, property, objectType);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return returnCount(executioner, occurrenceBuilder);
	}

	public static int countOccurrances(QueryExecutioner executioner, Node subject, Property property,
			Set<Node> objectTypes) {
		SelectBuilder occurrenceBuilder = new SelectBuilder();
		Iterator<Node> typeIterator = objectTypes.iterator();
		try {
			occurrenceBuilder.addVar("count(*)", "?c");
			while (typeIterator.hasNext())
				occurrenceBuilder.addWhere(subject, property, typeIterator.next());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return returnCount(executioner, occurrenceBuilder);
	}
	
	public static int countSOOccurrances(QueryExecutioner executioner, String var, Property property) {
	    SelectBuilder occurrenceBuilder = new SelectBuilder();
	    try {
	      occurrenceBuilder.addVar(var, "?c");
	      occurrenceBuilder.addWhere(
	          NodeFactory.createVariable("s"), property, NodeFactory.createVariable("o"));
	    } catch (ParseException e) {
	      e.printStackTrace();
	    }

	    return returnCount(executioner, occurrenceBuilder);
	  }

	public static int returnCount(QueryExecutioner executioner, SelectBuilder builder) {
		Query queryOccurrence = builder.build();
		int count_Occurrence = 0;
		try (QueryExecution queryExecution = executioner.getQueryExecution(queryOccurrence)) {
			ResultSet resultSet = queryExecution.execSelect();
			if (resultSet.hasNext())
				count_Occurrence = resultSet.next().get("?c").asLiteral().getInt();
		}

		return count_Occurrence;
	}

}
