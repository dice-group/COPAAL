package org.dice_research.fc.paths.ext;

import java.util.List;
import org.dice_research.fc.data.QRestrictedPath;

/**
 * Interface of a class that can search and score paths solely based on a given property. This
 * allows the execution of this class as pre-processing before the actual fact checking takes place.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface IPathExtractor {

  /**
   * This method searches and scores q-restricted paths that can be used to validate facts with the
   * given property.
   * 
   * @param propertyURI the URI of the property for which paths should be searched
   * @return scores q-restricted paths
   */
  public List<QRestrictedPath> extract(String propertyURI);

}
