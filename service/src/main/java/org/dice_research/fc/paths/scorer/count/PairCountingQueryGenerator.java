package org.dice_research.fc.paths.scorer.count;

import java.util.List;

import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertyHelper;

public class PairCountingQueryGenerator implements QueryGenerator {

	protected static final String COUNT_VARIABLE_NAME = "?sum";
	protected static final String SUBJECT_VARIABLE_NAME = "?s";
	protected static final String OBJECT_VARIABLE_NAME = "?o";
	protected static final String INTERMEDIATE_NODE_VARIABLE_NAME = "?x";

	@Override
	public String getCountVariableName() {
		return COUNT_VARIABLE_NAME;
	}

	@Override
	public String createCountQuery(List<Property> pathProperties, String[] propURIs,
			org.apache.jena.rdf.model.Property predicate) {
		return createPropertyQueryRecursively(pathProperties, propURIs, predicate.toString());
	}

	private String createPropertyQueryRecursively(List<Property> pathProperties, String[] propURIs, String predicate) {
		StringBuilder queryBuilder = new StringBuilder();
		// This is the first property in the list
		queryBuilder.append("Select (count(*) as " + COUNT_VARIABLE_NAME + ") where { \n");
		createPropertyQuery_Recursion(0, pathProperties, propURIs, predicate, queryBuilder);
		queryBuilder.append("} }\n");
		
		return queryBuilder.toString();
	}

	private void createPropertyQuery_Recursion(int propId, List<Property> pathProperties, String[] propURIs , String predicate,
			StringBuilder queryBuilder) {
		String localSubject = propId == 0 ? SUBJECT_VARIABLE_NAME : INTERMEDIATE_NODE_VARIABLE_NAME + propId;
		if(propId != 0)
			queryBuilder.append("{ ");
		
		queryBuilder.append("Select distinct ");
		queryBuilder.append(localSubject);
		queryBuilder.append(" " + OBJECT_VARIABLE_NAME + " where { \n");
		
		if(propId == 0)
			queryBuilder.append("?s <").append(predicate).append("> ?o . \n");

		// If this is the end of the recursion
		if (propId == pathProperties.size() - 1) {
			// Use object variable name
			addTriplePattern(pathProperties.get(propId), propURIs[propId], localSubject, OBJECT_VARIABLE_NAME,
					queryBuilder);
			// Add object types
			// queryBuilder.append(oTypeTriples);
		} else {
			addTriplePattern(pathProperties.get(propId), propURIs[propId], localSubject,
					INTERMEDIATE_NODE_VARIABLE_NAME + (propId + 1), queryBuilder);
			// Start the recursion
			createPropertyQuery_Recursion(propId + 1, pathProperties, propURIs, predicate, queryBuilder);
			queryBuilder.append("} }\n");
		}
	}

	private void addTriplePattern(Property property, String propUri, String firstVariable, String secondVariable,
			StringBuilder builder) {
		String subject, object;
		if (property.isInverse()) {
			object = firstVariable;
			subject = secondVariable;
		} else {
			object = secondVariable;
			subject = firstVariable;
		}
		builder.append(subject);
		builder.append(" <");
		builder.append(propUri);
		builder.append("> ");
		builder.append(object);
		builder.append(" .\n");
	}
}
