package org.dice.FactCheck.preprocess.service;

import java.util.Collection;

import org.dice_research.fc.data.Predicate;

/**
 * this interface provides method signatures for predicate service
 *
 * @author Farshad Afshari
 *
 */

public interface IPredicateService {
    // collection of filters which based on them a query generated and run
    Collection<Predicate> allPredicates(Collection<String> predicateFilter);
    // the KG file name
    Collection<Predicate> allPredicates(String fileName);
}
