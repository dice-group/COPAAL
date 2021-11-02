package org.dice.FactCheck.preprocess.service;

import java.util.Collection;

import org.dice_research.fc.data.Predicate;

public interface IPredicateService {

    public Collection<Predicate> allPredicates(Collection<String> predicateFilter);
}
