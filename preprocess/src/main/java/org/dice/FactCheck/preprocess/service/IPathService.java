package org.dice.FactCheck.preprocess.service;

import org.dice.FactCheck.preprocess.model.Path;
import org.dice.FactCheck.preprocess.model.Predicate;

import java.util.Collection;
/**
 * Interface for a path service
 *
 * @author Farshad Afshari
 *
 */


public interface IPathService {
    Collection<Path> generateAllPaths(Collection<Predicate> predicates);
}
