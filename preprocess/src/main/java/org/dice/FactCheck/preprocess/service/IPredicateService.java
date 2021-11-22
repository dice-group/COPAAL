package org.dice.FactCheck.preprocess.service;

import java.util.Collection;

import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;

public interface IPredicateService {

    public Collection<Predicate> allPredicates(Collection<String> predicateFilter);
    public Collection<Predicate> allPredicates(String fileName);
}
