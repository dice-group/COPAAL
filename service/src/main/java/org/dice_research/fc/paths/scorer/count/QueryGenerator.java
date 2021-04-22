package org.dice_research.fc.paths.scorer.count;

import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;

public interface QueryGenerator {

	String getCountVariableName();

	String createCountQuery(List<Property> properties, String[] translate2iriArray,
			org.apache.jena.rdf.model.Property predicate, Set<Node> subjectTypes, Set<Node> objectTypes);
	
	default StringBuilder generateTypeRestrictions(Set<Node> types, String variableName) {
		StringBuilder builder = new StringBuilder();
		for (Node type : types) {
			builder.append(variableName);
			builder.append(" a <");
			builder.append(type.getURI());
			builder.append("> .\n");
		}
		return builder;
	}
}
