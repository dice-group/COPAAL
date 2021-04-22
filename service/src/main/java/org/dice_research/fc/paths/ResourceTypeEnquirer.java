package org.dice_research.fc.paths;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice_research.util.SPARQLUtil;

public class ResourceTypeEnquirer {

	private QueryExecutioner executioner;
	private boolean vTy;
	private Set<Node> subjectTypes;
	private Set<Node> objectTypes;

	public ResourceTypeEnquirer(QueryExecutioner executioner, boolean vTy, Statement fact) {
		this.executioner = executioner;
		this.vTy = vTy;
		subjectTypes = getSubjectTypes(fact);
		objectTypes = getObjectTypes(fact);
	}

	public Set<Node> getSubjectTypes(Statement fact) {
		if (vTy) {
			return new HashSet<Node>();
		}
		Set<Node> subjectTypes = SPARQLUtil.getObjects(executioner, fact.getPredicate().asNode(), RDFS.domain);
		if (subjectTypes.isEmpty()) {
			subjectTypes = SPARQLUtil.getObjects(executioner, fact.getSubject().asNode(), RDF.type);
		}
		return subjectTypes;
	}

	public Set<Node> getObjectTypes(Statement fact) {
		if (vTy) {
			return new HashSet<Node>();
		}
		Set<Node> objectTypes = SPARQLUtil.getObjects(executioner, fact.getPredicate().asNode(), RDFS.range);
		if (objectTypes.isEmpty() && fact.getObject().isResource()) {
			objectTypes = SPARQLUtil.getObjects(executioner, fact.getObject().asResource().asNode(), RDF.type);
		}
		return objectTypes;
	}

	public QueryExecutioner getExecutioner() {
		return executioner;
	}

	public boolean isvTy() {
		return vTy;
	}

	public Set<Node> getSubjectTypes() {
		return subjectTypes;
	}

	public Set<Node> getObjectTypes() {
		return objectTypes;
	}
}
