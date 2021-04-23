package org.dice_research.fc.paths;

import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.util.SPARQLUtil;

@Deprecated
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
	public int countPathInstances(QRestrictedPath path) {
		// TODO Auto-generated method stub
		return 0;
	}

  @Override
  public int countPredicateInstances(Predicate predicate) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int countCooccurrences(Predicate predicate, QRestrictedPath path) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int deriveMaxCount(Resource subject, Predicate predicate, Resource Object) {
    // TODO Auto-generated method stub
    return 0;
  }

}
