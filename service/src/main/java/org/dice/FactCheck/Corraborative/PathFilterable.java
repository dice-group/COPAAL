package org.dice.FactCheck.Corraborative;

import java.util.List;

public interface PathFilterable {
 
	public List<PathFilter> filterPaths(List<PathFilter> pths,int[] pathsMinJ);
}
