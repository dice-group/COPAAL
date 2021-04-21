package org.dice_research.fc.paths;

/**
 * Classes implementing this interface can calculate the score for a given path with respect to its
 * corroboration of triples with the given predicate.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface IPathScorer {

  double score(String predicate, Object path);
}
