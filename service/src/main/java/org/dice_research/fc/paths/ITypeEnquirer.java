package org.dice_research.fc.paths;

import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Statement;

public interface ITypeEnquirer {

	Set<Node> getSubjectTypes(Statement fact);

	Set<Node> getObjectTypes(Statement fact);
}
