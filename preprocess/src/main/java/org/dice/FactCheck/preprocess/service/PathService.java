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

    public List<Path> getAllLists(List<Predicate> elements, int lengthOfList) throws CloneNotSupportedException {
        //initialize our returned list with the number of elements calculated above
        List<Path> allLists = new ArrayList<>();

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
            List<Path> allSublists = getAllLists(elements, lengthOfList - 1);

            for(int i = 0; i < elements.size(); i++)
            {
                for(int j = 0; j < allSublists.size(); j++)
                {
                    //add the newly appended combination to the list
                    Path tempPath1 = (Path) allSublists.get(j).clone();
                    tempPath1.addPart(elements.get(i),true);
                    allLists.add(tempPath1);
                    Path tempPath2 = (Path) allSublists.get(j).clone();
                    tempPath2.addPart(elements.get(i),false);
                    allLists.add(tempPath2);
                }
            }
            allPathWithAllLength.addAll(allLists);
            return allLists;
        }
    }

    @Override
    public Collection<Path> generateAllPaths(Collection<Predicate> predicates) throws CloneNotSupportedException {
        // convert to set because we dont want to have duplicated items
        Set<Predicate> setOfPredicates = new HashSet<>(predicates);
        List<Predicate> listOfPredicates = new ArrayList<>(setOfPredicates);
        return getAllLists(listOfPredicates,listOfPredicates.size());
    }
}
