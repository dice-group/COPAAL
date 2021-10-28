package org.dice.FactCheck.preprocess.service;

import org.dice.FactCheck.preprocess.model.Path;
import org.dice.FactCheck.preprocess.model.Predicate;

import java.util.Collection;
import java.util.Set;

/**
 * Interface for a path service
 *
 * @author Farshad Afshari
 *
 */


public interface IPathService {
    Set<Path> getAllPathWithAllLength();
    Collection<Path> generateAllPaths(Collection<Predicate> predicates, int maximumLengthOfPaths) throws CloneNotSupportedException;
}
