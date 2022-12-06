package org.dice_research.fc.paths.search;

import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.IPathSearcher;
import org.dice_research.fc.paths.IPreProcessProvider;
import org.dice_research.fc.tentris.TentrisAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * this class use a Tentris adapter and preProcessProvider to find the path between
 */

public class PreProcessPathSearcher implements IPathSearcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreProcessPathSearcher.class);

    IPreProcessProvider preProcessProvider;

    TentrisAdapter adapter;


    public PreProcessPathSearcher(IPreProcessProvider preProcessProvider, TentrisAdapter adapter) {
        this.preProcessProvider = preProcessProvider;
        this.adapter = adapter;
    }

    @Override
    public Collection<QRestrictedPath> search(Resource subject, Predicate predicate, Resource object) {
        LOGGER.info("use PreProcessPathSearcher");
        Collection<QRestrictedPath> returnSet = new HashSet<>();
        Set<String> allAvailablePaths = preProcessProvider.allPathsForThePredicate(predicate);
        for(String path:allAvailablePaths){

            //find is there a path in the KG between subject and object or not
            // it is a sum query
            StringBuilder query = generateQueryForCheckIsThereAPathOrNotBetweenSubjectAndObject(path, subject, object);

            LOGGER.info(path);
            LOGGER.info(query.toString());

            long isPathExistadapter = adapter.executeCountQuery(query);

            if(isPathExistadapter>0){
                returnSet.add(QRestrictedPath.toQRestrictedPathFromStringWithTag(path));
            }
        }

        return returnSet;
    }

    private StringBuilder generateQueryForCheckIsThereAPathOrNotBetweenSubjectAndObject(String path, Resource subject, Resource object) {
        // seperate the parts
        // the path would be something like ^<...><...>^<...>
        String[] parts = path.split(">");
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * where { ");
        String first= "<"+subject.getURI()+">";
        int pCounter = 0;
        String variableName = "?in";
        String second = variableName + pCounter;

        for(int i = 0 ; i < parts.length ; i++){

            if(i+1 == parts.length){
                second = "<"+object.getURI()+">";
            }

            parts[i] = parts[i].replace("<","");

            if(parts[i].charAt(0)=='^'){
                // is inverted
                parts[i] = parts[i].replace("^","");

                queryBuilder.append(second);
                queryBuilder.append(" <");
                queryBuilder.append(parts[i]);
                queryBuilder.append("> ");
                queryBuilder.append(first);

            }else{
                queryBuilder.append(first);
                queryBuilder.append(" <");
                queryBuilder.append(parts[i]);
                queryBuilder.append("> ");
                queryBuilder.append(second);
            }
            queryBuilder.append(" . ");
            pCounter = pCounter+1;
            first = second;
            second = variableName + pCounter;
        }

        queryBuilder.append("}");

        return queryBuilder;

    }
}
