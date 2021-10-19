package org.dice.FactCheck.preprocess.service;

import org.dice.FactCheck.preprocess.model.Path;
import org.dice.FactCheck.preprocess.model.Predicate;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;

/**
 * implementation of IPathService
 *
 * @author Farshad Afshari
 *
 */

public class PathService implements IPathService{
    @Override
    public Collection<Path> generateAllPaths(Collection<Predicate> predicates) {
        Set<Path> result = new HashSet<Path>();

        // at least we need 2 predicates
        if(predicates.size()<2){
            return result;
        }

        // it can just provide path with length two
        if(predicates.size()==2){
            for(Predicate predicateFisrt : predicates){
                for(Predicate predicateSecond : predicates){
                    if(predicateFisrt.getRange().equals(predicateSecond.getDomain())){
                        // the range and domain are compatible
                        // P1 range-> domain-> P2
                        // add as path
                        Path path = new Path();
                        path.addPart(predicateFisrt,false);
                        path.addPart(predicateSecond,false);
                        result.add(path);
                    }
                    if(predicateFisrt.getRange().equals(predicateSecond.getRange())){
                        // the range and range are compatible
                        // P1 range->  P2
                        // add as path
                        Path path = new Path();
                        path.addPart(predicateFisrt,false);
                        path.addPart(predicateSecond,true);
                        result.add(path);
                    }
                    if(predicateFisrt.getDomain().equals(predicateSecond.getDomain())){
                        // the range and domain are compatible
                        // add as path
                        Path path = new Path();
                        path.addPart(predicateFisrt,true);
                        path.addPart(predicateSecond,false);
                        result.add(path);
                    }
                    if(predicateFisrt.getDomain().equals(predicateSecond.getRange())){
                        // the range and domain are compatible
                        // add as path
                        Path path = new Path();
                        path.addPart(predicateFisrt,true);
                        path.addPart(predicateSecond,true);
                        result.add(path);
                    }
                }
            }
        }

        return result;
    }
}
