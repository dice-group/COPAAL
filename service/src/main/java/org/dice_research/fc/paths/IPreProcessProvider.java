package org.dice_research.fc.paths;

import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import java.util.*;

public interface IPreProcessProvider {
    public long getPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction, ITypeRestriction rangeRestriction);
    public long getPredicateInstances(Predicate predicate);
    public long getCooccurrences(Predicate predicate, QRestrictedPath path);
    public long getMaxCount(Predicate predicate);
    public Set<String> allPathsForThePredicate(Predicate predicate);
}
