package org.dice_research.fc.paths;

import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.data.Predicate;

/**
 * Classes implementing this interface can be used to preprocess a given fact before starting the
 * fact checking.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface FactPreprocessor {

  /**
   * Generates an internal representation of the given predicate, i.e., a {@link Predicate}
   * instance.
   * 
   * @param predicate the predicate as {@link Property}
   * @return the prepared {@link Predicate} instance
   */
  Predicate generatePredicate(Property predicate);

}
