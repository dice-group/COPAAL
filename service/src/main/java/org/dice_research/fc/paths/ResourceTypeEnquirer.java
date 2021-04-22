package org.dice_research.fc.paths;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice_research.util.SPARQLUtil;

public class ResourceTypeEnquirer implements ITypeEnquirer {

	private QueryExecutioner executioner;
	private boolean vTy;

	public ResourceTypeEnquirer(QueryExecutioner executioner, boolean vTy) {
		this.executioner = executioner;
		this.vTy = vTy;
	}

	@Override
	public Set<Node> getSubjectTypes(Statement fact) {
		if (vTy) {
			return new HashSet<Node>();
		}
		Set<Node> subjectTypes = SPARQLUtil.getObjects(executioner, fact.getPredicate(), RDFS.domain);
		if (subjectTypes.isEmpty()) {
			subjectTypes = SPARQLUtil.getObjects(executioner, fact.getSubject(), RDF.type);
		}
		return subjectTypes;
	}

	@Override
	public Set<Node> getObjectTypes(Statement fact) {
		if (vTy) {
			return new HashSet<Node>();
		}
		Set<Node> objectTypes = SPARQLUtil.getObjects(executioner, fact.getPredicate(), RDFS.range);
		if (objectTypes.isEmpty() && fact.getObject().isResource()) {
			objectTypes = SPARQLUtil.getObjects(executioner, fact.getObject().asResource(), RDF.type);
		}
		return objectTypes;
	}

	public QueryExecutioner getExecutioner() {
		return executioner;
	}

	public boolean isvTy() {
		return vTy;
	}
	
	

}
