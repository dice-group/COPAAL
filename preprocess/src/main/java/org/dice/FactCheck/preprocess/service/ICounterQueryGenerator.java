package org.dice.FactCheck.preprocess.service;

import org.dice.FactCheck.preprocess.model.Path;
import org.dice_research.fc.data.Predicate;

import java.util.Collection;

public interface ICounterQueryGenerator {
    public Collection<String> cooccurenceCount(Predicate predicate, Collection<Path> paths);
}
