package org.dice_research.fc.paths;

import java.util.Collection;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.dice.FactCheck.Corraborative.UIResult.Path;

/**
 * Instances of this class can derive (potential) corroborative paths for a given triple.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface IPathSearcher {

  /**
   * Searches (potential) corroborative paths for a given triple.
   * 
   * @param subject the subject of the triple
   * @param predicate the predicate of the triple
   * @param object the object of the triple
   * @return an array of (potential) corroborative paths
   */
  Collection<Path> search(Resource subject, Property predicate, Resource object);
}
