package org.dice.FactCheck.preprocess.service;

import org.dice.FactCheck.preprocess.model.CountQueries;
import org.dice.FactCheck.preprocess.model.Path;
import org.dice_research.fc.data.Predicate;

import java.util.Collection;
/*
 * this interface provides method signatures for count queries
 *
 * @author Farshad Afshari
 * */
public interface ICounterQueryGenerator {
    CountQueries generateCountQueries(Predicate predicate, Collection<Path> paths);
}
