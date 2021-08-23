package org.dice.fact_check.corraborative.ui_result;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CorroborativeGraph {
	
	private List<Path> pathList;
	private double graphScore;
	private CorroborativeTriple inputTriple;

	public CorroborativeTriple getInputTriple() {
		return inputTriple;
	}

	public void setInputTriple(CorroborativeTriple inputTriple) {
		this.inputTriple = inputTriple;
	}

	public double getGraphScore() {
		return graphScore;
	}

	public void setGraphScore(double graphScore) {
		this.graphScore = graphScore;
	}

	public List<Path> getPathList() {
		return pathList;
	}

	public void setPathList(List<Path> pathList) {
		this.pathList = pathList;
	}

}
