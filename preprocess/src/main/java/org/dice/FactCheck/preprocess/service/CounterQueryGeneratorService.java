package org.dice.FactCheck.preprocess.service;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.dice.FactCheck.preprocess.model.Path;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;


import java.util.*;

/*
* this class provide methods for generate queries which their purpose is counting
* */
public class CounterQueryGeneratorService implements ICounterQueryGenerator {


    protected static final String COUNT_VARIABLE_NAME = "sum";

    /*
    * this method generate all queries
    * */
    @Override
    public Collection<String> cooccurenceCount(Predicate predicate, Collection<Path> paths) {
        Set<String> queries = new HashSet<String>();
        for(Path path: paths){
            if(isFitWithPath(predicate, path)) {
                queries.add(generateQuery(predicate, path));
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
            if(firstElement.getFirst().getRange().equals(predicate.getDomain())){
                isDomainFit = true;
            }
        }else{
            // is not inverted domain should be equal with predicate domain
            if(firstElement.getFirst().getDomain().equals(predicate.getDomain())){
                isDomainFit = true;
            }
        }

        if(lastElement.getSecond()){
            // is inverted domain should be equal with predicate range
            if(lastElement.getFirst().getDomain().equals(predicate.getRange())){
                isRangeFit = true;
            }
        }else{
            // is not inverted range should be equal with predicate range
            if(lastElement.getFirst().getRange().equals(predicate.getRange())){
                isRangeFit = true;
            }
        }

        return isDomainFit & isRangeFit;
    }

    private String generateQuery(Predicate predicate, Path path) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT (count(DISTINCT *) AS ?");
        queryBuilder.append(COUNT_VARIABLE_NAME);
        queryBuilder.append(") WHERE { ?s <");
        queryBuilder.append(predicate.getProperty().getURI());
        queryBuilder.append("> ?o . ");
        predicate.getDomain().addRestrictionToQuery("s", queryBuilder);
        predicate.getRange().addRestrictionToQuery("o", queryBuilder);
        addPath(path, queryBuilder, "s", "o","in");
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

            if(!pair.getSecond()){
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

}
