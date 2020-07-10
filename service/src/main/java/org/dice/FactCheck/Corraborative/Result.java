package org.dice.FactCheck.Corraborative;

import org.apache.jena.rdf.model.Property;

public class Result {
	
	public String path;
	public Property predicate;	
	public double score;
	public String pathSpecificity;
	public String pathBuilder;
	public String intermediateNodes;
	public int pathLength;
	public boolean hasLegalScore = false;
	
	public String getPathSpecificity() {
		return pathSpecificity;
	}


	public void setPathSpecificity(String pathSpecificity) {
		this.pathSpecificity = pathSpecificity;
	}


	public Result(String path, Property predicate, String pathBuilder, String intermediateNodes, int pathLength) {
		this.path = path;
		this.predicate = predicate;
		this.score = 0;
		this.pathBuilder = pathBuilder;
		this.intermediateNodes = intermediateNodes;
		this.pathLength = pathLength;
	}

	public Result(String path, Property predicate, double score, String pathBuilder, String intermediateNodes, int pathLength) {
		this.path = path;
		this.predicate = predicate;
		this.score = score;
		this.pathBuilder = pathBuilder;
		this.intermediateNodes = intermediateNodes;
		this.pathLength = pathLength;
	}


	public String getPathBuilder() {
		return pathBuilder;
	}


	public void setPathBuilder(String pathBuilder) {
		this.pathBuilder = pathBuilder;
	}


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Property getPredicate() {
		return predicate;
	}

	public void setPredicate(Property predicate) {
		this.predicate = predicate;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

}
