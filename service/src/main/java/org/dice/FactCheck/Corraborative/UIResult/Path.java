package org.dice.FactCheck.Corraborative.UIResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.rdf.model.Statement;

public class Path {


	private List<CorroborativeTriple> path = new ArrayList<CorroborativeTriple>();

	private double pathScore;

	private String pathText;

	public Path(List<CorroborativeTriple> path, double pathScore, String pathText)
	{
		this.path = path;
		this.pathScore = pathScore;
		this.pathText = pathText;
	}

	public List<CorroborativeTriple> getPath() {
		return path;
	}

	public void setPath(List<CorroborativeTriple> path) {
		this.path = path;
	}


	public double getPathScore() {
		return pathScore;
	}

	public void setPathScore(double pathScore) {
		this.pathScore = pathScore;
	}


	public String getPathText() {
		return pathText;
	}

	public void setPathText(String pathText) {
		this.pathText = pathText;
	}

}
