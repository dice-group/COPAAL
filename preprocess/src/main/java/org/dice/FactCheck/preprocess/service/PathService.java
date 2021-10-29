package org.dice.FactCheck.preprocess.service;

import org.apache.commons.math3.util.Pair;
import org.dice.FactCheck.preprocess.model.Path;
import org.dice_research.fc.data.Predicate;

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
                    if(newPathCompatibleWithExistingPath(allSublists.get(j), elements.get(i),true)) {
                        Path tempPath = allSublists.get(j).clone();
                        tempPath.addPart(elements.get(i),true);
                        allLists.add(tempPath);
                    }
                    if(newPathCompatibleWithExistingPath(allSublists.get(j), elements.get(i),false)){
                        Path tempPath = allSublists.get(j).clone();
                        tempPath.addPart(elements.get(i),false);
                        allLists.add(tempPath);
                    }
                }
            }
            allPathWithAllLength.addAll(allLists);
            return allLists;
        }
    }

    private boolean newPathCompatibleWithExistingPath(Path path, Predicate predicateSecond, boolean secondInverted) {
         Pair<Predicate,Boolean> lastNode = path.getLastNode();
        Predicate predicateFirst = path.getLastNode().getFirst();
        Boolean firstInverted = path.getLastNode().getSecond();

        if(firstInverted == false && secondInverted == false && predicateFirst.getRange().equals(predicateSecond.getDomain())){
            // the range and domain are compatible
            // P1 range-> domain P2
            return true;
        }
        if(firstInverted == false && secondInverted == true && predicateFirst.getRange().equals(predicateSecond.getRange())){
            // the range and range are compatible
            // P1 range->  P2 range
            return true;
        }
        if(firstInverted == true && secondInverted == false && predicateFirst.getDomain().equals(predicateSecond.getDomain())){
            // the range and domain are compatible
            // domain P1 ->  domain P2
            return true;

        }
        if(firstInverted == true && secondInverted == true && predicateFirst.getDomain().equals(predicateSecond.getRange())){
            // the range and domain are compatible
            // domain P1 ->   P2 range
            return true;
        }
        return false;
    }


    @Override
    public Collection<Path> generateAllPaths(Collection<Predicate> predicates,int maximumLengthOfPaths) throws CloneNotSupportedException {
        // convert to set because we dont want to have duplicated items
        Set<Predicate> setOfPredicates = new HashSet<>(predicates);
        List<Predicate> listOfPredicates = new ArrayList<>(setOfPredicates);
        return getAllLists(listOfPredicates,maximumLengthOfPaths);
    }
}
