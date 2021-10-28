package org.dice.FactCheck.preprocess.service;

import org.dice.FactCheck.preprocess.model.Path;
import org.dice.FactCheck.preprocess.model.Predicate;

import java.util.*;

/**
 * implementation of IPathService
 *
 * @author Farshad Afshari
 *
 */

public class PathService implements IPathService{

    Set<Path> allPathWithAllLength = new HashSet<>();

    public Set<Path> getAllPathWithAllLength() {
        return allPathWithAllLength;
    }

    public Set<Path> getAllLists(Set<Predicate> elements, int lengthOfList) throws CloneNotSupportedException {
        //initialize our returned list with the number of elements calculated above
        Set<Path> allLists = new HashSet<>();

        //lists of length 1 are just the original elements
        if(lengthOfList == 1) {
            for(Predicate p : elements){
                allLists.add(new Path(p,true));
                allLists.add(new Path(p,false));
            }
            allPathWithAllLength.addAll(allLists);
            return allLists;
        }
        else
        {
            //the recursion--get all lists of length n,..., length 2, all the way up to 1
            Set<Path> allSubset = getAllLists(elements, lengthOfList - 1);

            List<Path> allSublistsList = new ArrayList<>(allSubset);
            List<Predicate> elementList = new ArrayList<>(elements);

            for(int i = 0; i < elementList.size(); i++)
            {
                for(int j = 0; j < allSublistsList.size(); j++)
                {
                    //add the newly appended combination to the list
                    Path tempPath1 = (Path) allSublistsList.get(j).clone();
                    tempPath1.addPart(elementList.get(i),true);
                    allLists.add(tempPath1);
                    Path tempPath2 = (Path) allSublistsList.get(j).clone();
                    tempPath2.addPart(elementList.get(i),false);
                    allLists.add(tempPath2);
                }
            }
            allPathWithAllLength.addAll(allLists);
            return allLists;
        }
    }

    @Override
    public Collection<Path> generateAllPaths(Collection<Predicate> predicates, int maximumLengthOfPaths) throws CloneNotSupportedException {
        // convert to set because we dont want to have duplicated items
        Set<Predicate> setOfPredicates = new HashSet<>(predicates);
        maximumLengthOfPaths = Math.min(maximumLengthOfPaths,setOfPredicates.size());
        return getAllLists(setOfPredicates,maximumLengthOfPaths);
    }
}
