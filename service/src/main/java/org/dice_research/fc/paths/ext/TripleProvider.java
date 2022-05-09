package org.dice_research.fc.paths.ext;

import java.util.List;
import org.dice_research.fc.data.StringTriple;

/**
 * A class that provides up to N triples with a given property where N is the given number of
 * triples.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface TripleProvider {

  /**
   * This method provides up to N triples with a given property where N is the given number of
   * triples. If less than N triples have the given property, all triples are returned.
   * 
   * @param propertyIri the IRI of the predicate of the triples 
   * @return the triples that could be retrieved
   */
  default List<StringTriple> provideTriples(String propertyIri) {
    return provideTriples(propertyIri, -1);
  }

  /**
   * This method provides up to N triples with a given property where N is the given number of
   * triples. If less than N triples have the given property or N < 0, all triples are returned. 
   * 
   * @param propertyIri the IRI of the predicate of the triples 
   * @param numberOfTriples the number of triples that should be provided.
   * @return the triples that could be retrieved
   */
  List<StringTriple> provideTriples(String propertyIri, int numberOfTriples);
}
