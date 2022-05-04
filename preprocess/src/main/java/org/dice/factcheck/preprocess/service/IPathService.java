package org.dice.factcheck.preprocess.service;

import org.dice.factcheck.preprocess.model.Path;
import org.dice_research.fc.data.Predicate;

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
    Collection<Path> generateAllPaths(Collection<Predicate> predicates, int maximumLengthOfPaths,String fileName ,boolean SaveTheResultInFile) throws CloneNotSupportedException;
}
