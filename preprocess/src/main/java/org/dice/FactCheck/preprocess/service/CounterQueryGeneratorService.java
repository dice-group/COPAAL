package org.dice.FactCheck.preprocess.service;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.dice.FactCheck.preprocess.model.CountQueries;
import org.dice.FactCheck.preprocess.model.Path;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;


import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.stream.Collectors;

/*
* this class provide methods for generate queries which their purpose is counting
* */
public class CounterQueryGeneratorService implements ICounterQueryGenerator {

    Map<String, ArrayList<String>> ancestorsMap;

    public CounterQueryGeneratorService() {
        try{
            InputStream is = getClass().getClassLoader().getResourceAsStream("ancestorsMap.ser");
            ObjectInputStream in = new ObjectInputStream(is);
            ancestorsMap = (Map<String, ArrayList<String>>) in.readObject();
            in.close();
        }catch (Exception ex){
            System.out.println("Error in read map of ancestors"+ex.getStackTrace() + ex.getMessage());
        }
    }

    /**
     * The variable name of counts in SPARQL queries.
     */
    protected final String COUNT_VARIABLE_NAME = "sum";

    /*
    * this method generate all queries
    * */
    @Override
    public CountQueries generateCountQueries(Predicate predicate, Collection<Path> paths) {
        CountQueries queries = new CountQueries();
        for(Path path: paths){
            if(isFitWithPath(predicate, path)) {
                queries.addToCoOccurrenceCountQueries(generateQueryCoOccurrence(predicate, path)+"\t"+path.toString()+"\t"+predicate.getProperty().getURI());
                queries.addToPathInstancesCountQueries(generateQueryPathInstancesCount(predicate, path)+"\t"+path.toString()+"\t "+predicate.getProperty().getURI());
                queries.addToMaxCountQueries(generateMaxQuery(predicate.getDomain())+"\t \t"+predicate.getProperty().getURI());
                queries.addToMaxCountQueries(generateMaxQuery(predicate.getRange())+"\t \t"+predicate.getProperty().getURI());
                queries.addToPredicateInstancesCountQueries(generateCountPredicateInstances(predicate)+"\t \t"+predicate.getProperty().getURI());
                queries.addToTypeInstancesCountQueries(generateCountTypeInstances(predicate.getDomain())+"\t \t"+predicate.getProperty().getURI());
                queries.addToTypeInstancesCountQueries(generateCountTypeInstances(predicate.getRange())+"\t \t"+predicate.getProperty().getURI());
            }
        }
        return queries;
    }

    private boolean isFitWithPath(Predicate predicate, Path path) {
        Pair<Predicate, Boolean> firstElement = path.getPaths().getFirst();
        Pair<Predicate, Boolean> lastElement = path.getPaths().getLast();
        boolean isDomainFit = false;
        boolean isRangeFit = false;


        if(firstElement.getSecond()){
            // is inverted range should be equal with predicate domain
            if(doesHaveOverlap(firstElement.getFirst().getRange(),predicate.getDomain())){
                isDomainFit = true;
            }
        }else{
            // is not inverted domain should be equal with predicate domain
            if(doesHaveOverlap(firstElement.getFirst().getDomain(),predicate.getDomain())){
                isDomainFit = true;
            }
        }

        if(lastElement.getSecond()){
            // is inverted domain should be equal with predicate range
            if(doesHaveOverlap(lastElement.getFirst().getDomain(),predicate.getRange())){
                isRangeFit = true;
            }
        }else{
            // is not inverted range should be equal with predicate range
            if(doesHaveOverlap(lastElement.getFirst().getRange(),predicate.getRange())){
                isRangeFit = true;
            }
        }

        return isDomainFit & isRangeFit;
    }

    private String generateQueryCoOccurrence(Predicate predicate, Path path) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT ?s ?o ");
        queryBuilder.append("WHERE { ?s <");
        queryBuilder.append(predicate.getProperty().getURI());
        queryBuilder.append("> ?o . ");
        predicate.getDomain().addRestrictionToQuery("s", queryBuilder);
        predicate.getRange().addRestrictionToQuery("o", queryBuilder);
        addPath(path, queryBuilder, "s", "o","in");
        queryBuilder.append(" }");
        return queryBuilder.toString();
    }

    private String generateQueryPathInstancesCount(Predicate predicate, Path path) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT ?s ?o ");
        queryBuilder.append("WHERE { ");
        predicate.getDomain().addRestrictionToQuery("s", queryBuilder);
        predicate.getRange().addRestrictionToQuery("o", queryBuilder);
        addPath(path, queryBuilder, "s", "o","in");
        queryBuilder.append(" }");
        return queryBuilder.toString();
    }

    public String generateCountPredicateInstances(Predicate predicate) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT ?s ?o ");
        queryBuilder.append("WHERE { ?s <");
        queryBuilder.append(predicate.getProperty().getURI());
        queryBuilder.append("> ?o . ");
        predicate.getDomain().addRestrictionToQuery("s", queryBuilder);
        predicate.getRange().addRestrictionToQuery("o", queryBuilder);
        queryBuilder.append(" }");
        return queryBuilder.toString();
    }

    private String generateMaxQuery(ITypeRestriction restriction) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT ?s");
        queryBuilder.append(" WHERE { ");
        restriction.addRestrictionToQuery("s", queryBuilder);
        queryBuilder.append(" }");
        return queryBuilder.toString();
    }

    protected String generateCountTypeInstances(ITypeRestriction restriction) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT ?s ");
        queryBuilder.append("WHERE { ");
        restriction.addRestrictionToQuery("s", queryBuilder);
        queryBuilder.append(" }");
        return queryBuilder.toString();
    }

    private void addPath(Path path, StringBuilder queryBuilder, String subjectVariable,
                        String objectVariable, String intermediateName) {
        for(int i = 0 ; i < path.getPaths().size() ; i++){
            Pair<Predicate, Boolean> pair = path.getPaths().get(i);

            String tempSubject = intermediateName.concat(String.valueOf(i));
            String tempObject = intermediateName.concat(String.valueOf(i+1));

            if(i==0){
                tempSubject = subjectVariable;
            }

            if(i+1 == path.getPaths().size()){
                tempObject = objectVariable;
            }

            if(pair.getSecond()){
                String temp = tempSubject;
                tempSubject = tempObject ;
                tempObject = temp;
            }
            queryBuilder.append('?');
            queryBuilder.append(tempSubject);
            queryBuilder.append(' ');
            queryBuilder.append('<');
            queryBuilder.append(pair.getFirst().getProperty().getURI());
            queryBuilder.append('>');
            queryBuilder.append(' ');
            queryBuilder.append('?');
            queryBuilder.append(tempObject);
            queryBuilder.append(' ');
            queryBuilder.append('.');

            if(i+1 < path.getPaths().size()){
                // space before first next variable
                queryBuilder.append(' ');
            }
        }
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


}
