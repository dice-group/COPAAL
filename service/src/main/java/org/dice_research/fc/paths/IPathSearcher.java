package org.dice_research.fc.paths;

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
  Object[] search(String subject, String predicate, String object);
}
