package org.dice_research.fc.paths.scorer;

import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.IPreProcessProvider;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;

public class PreCalculationScorer implements ICountRetriever{

    IPreProcessProvider service;

    public PreCalculationScorer(IPreProcessProvider service){
        this.service= service;
    }

    @Override
    public long countPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction, ITypeRestriction rangeRestriction) {
        return service.getPathInstances(path, domainRestriction, rangeRestriction);
    }

    @Override
    public long countPredicateInstances(Predicate predicate) {
        return service.getPredicateInstances(predicate);
    }

    @Override
    public long countCooccurrences(Predicate predicate, QRestrictedPath path) {
        return service.getCooccurrences(predicate, path);
    }

    @Override
    public long deriveMaxCount(Predicate predicate) {
        return service.getMaxCount(predicate);
    }
}
