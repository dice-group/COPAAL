package org.dice_research.fc.paths;

import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.paths.scorer.count.CountApproximatingQueryGenerator;
import org.dice_research.fc.paths.scorer.count.PairCountingQueryGenerator;
import org.dice_research.fc.paths.scorer.count.QueryGenerator;
import org.dice_research.util.SPARQLUtil;
import org.dice.FactCheck.Corraborative.UIResult.Path;

public class InstanceCounter implements ICountRetriever {

	private QueryExecutioner executioner;
	private boolean vTy;
	private ResourceTypeEnquirer typeEnquirer;

	public InstanceCounter(QueryExecutioner executioner, boolean vTy, Statement fact) {
		this.executioner = executioner;
		this.vTy = vTy;
		this.typeEnquirer = new ResourceTypeEnquirer(executioner, vTy, fact);
	}

	@Override
	public int countTriplesSameTypeSubj(Set<Node> types, Property predicate) {
		if (vTy) {
			return countTriplesSubjvTy(predicate);
		} else {
			return SPARQLUtil.countOccurrences(executioner, NodeFactory.createVariable("s"), RDF.type, types);
		}
	}
	
	@Override
	public int countTriplesSameTypeObj(Set<Node> types, Property predicate) {
		if (vTy) {
			return countTriplesObjvTy(predicate);
		} else {
			return SPARQLUtil.countOccurrences(executioner, NodeFactory.createVariable("s"), RDF.type, types);
		}
	}

	private int countTriplesSubjvTy(Property predicate) {
		return SPARQLUtil.countSubjectsWithProperty(executioner, predicate);
	}
	
	private int countTriplesObjvTy(Property predicate) {
		return SPARQLUtil.countObjectsWithProperty(executioner, predicate);
	}

	@Override
	public int countPathInstances(Path path) {
		QueryGenerator generator = new CountApproximatingQueryGenerator();
		
		Set<Node> subjectTypes = typeEnquirer.getSubjectTypes();
		Set<Node> objectTypes = typeEnquirer.getObjectTypes();
		
		String queryStr = generator.createCountQuery(path.getPath(), path.getPathText(), null, subjectTypes, objectTypes);
		Query query = QueryFactory.create(queryStr);
		QueryExecution queryExecution = executioner.getQueryExecution(query);
		return queryExecution.execSelect().next().get("?sum").asLiteral().getInt();
	}

	@Override
	public int countPredicateInstances(Property predicate) {
		return SPARQLUtil.countPredicateOccurrences(executioner, NodeFactory.createVariable("s"), predicate,
				NodeFactory.createVariable("o"));
	}

	@Override
	public int countPredicateInstances(Property predicate, Path path) {
		QueryGenerator generator = new PairCountingQueryGenerator();
		
		Set<Node> subjectTypes = typeEnquirer.getSubjectTypes();
		Set<Node> objectTypes = typeEnquirer.getObjectTypes();
		
		String queryStr = generator.createCountQuery(path.getPath(), path.getPathText(), predicate, subjectTypes, objectTypes);
		Query query = QueryFactory.create(queryStr);
		QueryExecution queryExecution = executioner.getQueryExecution(query);
		return queryExecution.execSelect().next().get("?sum").asLiteral().getInt();
	}

	@Override
	public int deriveMaxCount(Property predicate) {
		int subjectTriplesCount = countTriplesSameTypeSubj(typeEnquirer.getSubjectTypes(), predicate);
		int objectTriplesCount = countTriplesSameTypeObj(typeEnquirer.getObjectTypes(), predicate);
		return subjectTriplesCount*objectTriplesCount;
	}

}
