package org.dice_research.fc.paths;

import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.RDF;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice_research.util.SPARQLUtil;

public class InstanceCounter implements ICountRetriever {

	private QueryExecutioner executioner;
	private boolean vTy;

	public InstanceCounter(QueryExecutioner executioner, boolean vTy) {
		this.executioner = executioner;
		this.vTy = vTy;
	}

	@Override
	public int countTriplesSameTypeSubj(Set<Node> types, Property predicate) {
		if (vTy) {
			return countTriplesSubjvTy(predicate);
		} else {
			return SPARQLUtil.countOccurrances(executioner, NodeFactory.createVariable("s"), RDF.type, types);
		}
	}
	
	@Override
	public int countTriplesSameTypeObj(Set<Node> types, Property predicate) {
		if (vTy) {
			return countTriplesObjvTy(predicate);
		} else {
			return SPARQLUtil.countOccurrances(executioner, NodeFactory.createVariable("s"), RDF.type, types);
		}
	}

	public int countTriplesSubjvTy(Property predicate) {
		return SPARQLUtil.countSOOccurrances(executioner, "count(distinct ?s)", predicate);
	}
	
	public int countTriplesObjvTy(Property predicate) {
		return SPARQLUtil.countSOOccurrances(executioner, "count(distinct ?o)", predicate);
	}

	@Override
	public int countPathInstances(Object path) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int countPredicateInstances(Property predicate) {
		return SPARQLUtil.countPredicateOccurrances(executioner, NodeFactory.createVariable("s"), predicate,
				NodeFactory.createVariable("o"));
	}

	@Override
	public int countPredicateInstances(String predicate, Object path) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deriveMaxCount(String predicate) {
		// TODO Auto-generated method stub
		return 0;
	}

}
