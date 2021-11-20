package org.dice.FactCheck.preprocess.service;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.base.Sys;
import org.dice.FactCheck.preprocess.model.Path;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * implementation of IPathService
 *
 * @author Farshad Afshari
 *
 */

public class PathService implements IPathService{

    public long lastHeapFreeSize = 0;

    Map<String, ArrayList<String>> ancestorsMap;
    Set<String> allSharedPaths;

    public PathService(){
        try{
            InputStream is = getClass().getClassLoader().getResourceAsStream("ancestorsMap.ser");
            ObjectInputStream in = new ObjectInputStream(is);
            ancestorsMap = (Map<String, ArrayList<String>>) in.readObject();
            in.close();
            allSharedPaths = new HashSet<>();
        }catch (Exception ex){
            System.out.println("Error in read map of ancestors"+ex.getStackTrace() + ex.getMessage());
        }
    }

    public PathService(Map<String, ArrayList<String>> ancestorsMap){
        this.ancestorsMap = ancestorsMap;
    }


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
            showAndFreeHeapToMe("1");
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
                    if(newPredicateCompatibleWithExistingPath(allSublists.get(j), elements.get(i),true)) {
                        Path tempPath = allSublists.get(j).clone();
                        tempPath.addPart(elements.get(i),true);
                        allLists.add(tempPath);
                    }
                    if(newPredicateCompatibleWithExistingPath(allSublists.get(j), elements.get(i),false)){
                        Path tempPath = allSublists.get(j).clone();
                        tempPath.addPart(elements.get(i),false);
                        allLists.add(tempPath);
                    }
                    showAndFreeHeapToMe("2");
                }
            }
            allPathWithAllLength.addAll(allLists);
            return allLists;
        }
    }

    private boolean newPredicateCompatibleWithExistingPath(Path path, Predicate predicateSecond, boolean secondInverted) {
         //Pair<Predicate,Boolean> lastNode = path.getLastNode();
        Predicate predicateFirst = path.getLastNode().getFirst();
        Boolean firstInverted = path.getLastNode().getSecond();

        if(firstInverted == false && secondInverted == false && doesHaveOverlap(predicateFirst.getRange(), predicateSecond.getDomain())){
            // the range and domain are compatible
            // P1 range-> domain P2
            return true;
        }
        if(firstInverted == false && secondInverted == true && doesHaveOverlap(predicateFirst.getRange(), predicateSecond.getRange())){
            // the range and range are compatible
            // P1 range->  P2 range
            return true;
        }
        if(firstInverted == true && secondInverted == false && doesHaveOverlap(predicateFirst.getDomain(), predicateSecond.getDomain())){
            // the range and domain are compatible
            // domain P1 ->  domain P2
            return true;

        }
        if(firstInverted == true && secondInverted == true && doesHaveOverlap(predicateFirst.getDomain(), predicateSecond.getRange())){
            // the range and domain are compatible
            // domain P1 ->   P2 range
            return true;
        }
        return false;
    }

    private boolean doesHaveOverlap(ITypeRestriction firstRestriction , ITypeRestriction secondRestriction){
        HashSet firstSet =  (HashSet)firstRestriction.getRestriction();
        HashSet secondSet =  (HashSet)secondRestriction.getRestriction();

        if(firstSet.size()>1 || secondSet.size()>1){
            System.out.println("something is not correct");
        }

        String first = (String)firstSet.iterator().next();
        String second = (String)secondSet.iterator().next();

        if(first.equals(second)){
            return true;
        }

        ArrayList<String> firstAncestors = new ArrayList<>();;
        if(ancestorsMap.containsKey(first)) {
            firstAncestors = ancestorsMap.get(first);
        }else{
            System.out.println("can not find ancestors for "+first+" in map of ancestors");
        }

        ArrayList<String> secondAncestors = new ArrayList<>();
        if(ancestorsMap.containsKey(second)) {
            secondAncestors = ancestorsMap.get(second);
        }else{
            System.out.println("can not find ancestors for "+second+" in map of ancestors");
        }

        // one is parent of another one
        if(firstAncestors.contains(second)||secondAncestors.contains(first)){
            return true;
        }

        // have share parent
        if(hasSharedPoint(firstAncestors, secondAncestors)){
            return true;
        }

        return false;
    }

    private boolean hasSharedPoint(ArrayList<String> first, ArrayList<String> second) {

        first = deleteExtras(first);
        second = deleteExtras(second);

        List<String> shared = first.stream()
                .filter(second::contains)
                .collect(Collectors.toList());

        if(shared.size()>0){
            //System.out.println(shared.iterator().next());
            allSharedPaths.add(Arrays.toString(shared.toArray()));
            return true;
        }
        return false;
    }

    private ArrayList<String> deleteExtras(ArrayList<String> list) {
        list.remove("http://www.w3.org/2002/07/owl#Thing");
        list.removeIf(element -> (element.contains("ontologydesignpatterns")));
        list.removeIf(element -> (element.contains("yago")));
        list.removeIf(element -> (element.contains("v1")));
        list.removeIf(element -> (element.contains("xmlns.com/")));
        list.removeIf(element -> (element.contains("http://dbpedia.org/ontology/Eukaryote")));
        list.removeIf(element -> (element.contains("http://dbpedia.org/ontology/Species")));
        return list;
    }

    @Override
    public Collection<Path> generateAllPaths(Collection<Predicate> predicates,int maximumLengthOfPaths,String FileName,boolean SaveTheResultInFile) throws CloneNotSupportedException {
        // convert to set because we dont want to have duplicated items
        Set<Predicate> setOfPredicates = new HashSet<>(predicates);
        List<Predicate> listOfPredicates = new ArrayList<>(setOfPredicates);
        getAllLists(listOfPredicates,maximumLengthOfPaths);

        // save all share paths
        try {
            if(SaveTheResultInFile) {
                SaveAllSharedPaths(FileName);
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        return allPathWithAllLength;
    }

    private void SaveAllSharedPaths(String FileName) throws IOException {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        Date date = new Date();
        FileName = FileName+formatter.format(date);

        FileWriter fw = new FileWriter(FileName+".txt");

        try {
            Iterator<String> iterator = allSharedPaths.iterator();
            while (iterator.hasNext()) {
                fw.write(iterator.next());
                fw.write("\n");
            }
            fw.close();
        }catch(Exception ex){

        }



        FileOutputStream fileOut = null;
        try {
            fileOut =
                    new FileOutputStream(FileName+".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(allSharedPaths);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }finally {
            try {
                if (fileOut != null) {
                    System.out.println("CloseConn");
                    fileOut.close();
                    System.out.println("CloseConnDone");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private void showAndFreeHeapToMe(String position){

        // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
        long heapFreeSize = Runtime.getRuntime().freeMemory();

        if(heapFreeSize<40000000) {

            // Get current size of heap in bytes
            long heapSize = Runtime.getRuntime().totalMemory();

            lastHeapFreeSize  = heapSize;
            System.out.println(position + " heapSize: " + heapSize);


            // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
            long heapMaxSize = Runtime.getRuntime().maxMemory();
            System.out.println(position + " heapMaxSize: " + heapMaxSize);


            System.out.println(position + " heapFreeSize: " + heapFreeSize);


            System.out.println("------------------------------");

            System.gc();
            System.out.println("free");
        }
    }
}
