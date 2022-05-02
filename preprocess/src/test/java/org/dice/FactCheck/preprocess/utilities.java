package org.dice.FactCheck.preprocess;

import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice_research.fc.sparql.restrict.TypeBasedRestriction;

import java.util.HashSet;
import java.util.Set;

// this class contains some shared methods between test classes
public class utilities {
    public static ITypeRestriction makeITypeRestriction(String input){
        Set<String> inputSet = new HashSet<String>();
        inputSet.add(input);
        ITypeRestriction returnVal = new TypeBasedRestriction(inputSet);
        return returnVal;
    }
}
