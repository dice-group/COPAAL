package org.dice.fact_check.corraborative;

import java.util.HashMap;

/*
 * A data structure to generate paths and remember the directions in graph that lead to the path
 * and also the path length
 */

public class PathQuery {
	
	
	// A data structure to generate paths and remember the directions in graph that lead to the path
	private HashMap<String, HashMap<String, Integer>> pathBuilder = new HashMap<String, HashMap<String, Integer>>();
	
	private HashMap<String, String> intermediateNodes = new HashMap<String, String>();
	
	public HashMap<String, String> getIntermediateNodes() {
		return intermediateNodes;
	}

	public void setIntermediateNodes(HashMap<String, String> intermediateNodes) {
		this.intermediateNodes = intermediateNodes;
	}

	public PathQuery(HashMap<String, HashMap<String, Integer>> pathBuilder, HashMap<String, String> intermediateNodes) {
		this.pathBuilder = pathBuilder;
		this.intermediateNodes = intermediateNodes;
	}

	public HashMap<String, HashMap<String, Integer>> getPathBuilder() {
		return this.pathBuilder;
	}

	public void setPathBuilder(HashMap<String, HashMap<String, Integer>> pathBuilder) {
		this.pathBuilder = pathBuilder;
	}
	
}
