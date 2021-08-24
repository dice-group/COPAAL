package org.dice_research.fc;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.FactCheckingResult;

/**
 * This class implements the typical process for checking a given fact.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface IFactChecker {

  /**
   * Checks the given fact.
   * 
   * @param statement the fact to be checked
   * @return The result of the fact checking
   */
  default FactCheckingResult check(Statement statement) {
    if (!statement.getObject().isResource()) {
      throw new IllegalArgumentException("The given object has to be a resource.");
    }
    return check(statement.getSubject(), statement.getPredicate(),
        statement.getObject().asResource());
  }

  /**
   * Checks the given fact.
   * 
   * @param subject the subject of the fact to check
   * @param predicate the predicate of the fact to check
   * @param object the object of the fact to check
   * @return The result of the fact checking
   */
  FactCheckingResult check(Resource subject, Property predicate, Resource object);

}
