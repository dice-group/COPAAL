package org.dice_research.fc.sparql.query;

/**
 * Instances of this interface can validate the sparqlQuery
 *
 * @author Farshad Afshari
 *
 */

public interface IQueryValidator {
    boolean validate(String query);
}
