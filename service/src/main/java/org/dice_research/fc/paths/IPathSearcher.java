package org.dice_research.fc.paths;

import java.util.Collection;
import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;

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
  Collection<QRestrictedPath> search(Resource subject, Predicate predicate, Resource object);
}
