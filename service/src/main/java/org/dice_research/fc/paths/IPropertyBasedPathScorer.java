package org.dice_research.fc.paths;

import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;

/**
 * Classes implementing this interface are {@link IPathScorer} that solely rely on the property of
 * the given triple.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface IPropertyBasedPathScorer extends IPathScorer {

  @Override
  default QRestrictedPath score(Resource subject, Predicate predicate, Resource Object,
      QRestrictedPath path) {
    return score(predicate, path);
  }

  /**
   * Scores the given path with respect to the given triple.
   * 
   * @param subject the subject of the triple
   * @param predicate the predicate of the triple
   * @param Object the object of the triple
   * @param path the path that should be scored
   * @return the path with the score assigned (might be the same object as the given path)
   */
  QRestrictedPath score(Predicate predicate, QRestrictedPath path);
}
