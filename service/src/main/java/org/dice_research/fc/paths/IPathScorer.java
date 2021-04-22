package org.dice_research.fc.paths;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.dice.FactCheck.Corraborative.UIResult.Path;

/**
 * Classes implementing this interface can calculate the score for a given path with respect to its
 * corroboration of triples with the given predicate.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface IPathScorer {

  /**
   * Scores the given path with respect to the given triple.
   * 
   * @param subject the subject of the triple
   * @param predicate the predicate of the triple
   * @param Object the object of the triple
   * @param path the path that should be scored
   * @return the path with the score assigned (might be the same object as the given path)
   */
  Path score(Resource subject, Property predicate, Resource Object, Path path);
}
