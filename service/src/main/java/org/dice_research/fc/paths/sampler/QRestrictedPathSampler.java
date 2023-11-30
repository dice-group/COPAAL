package org.dice_research.fc.paths.sampler;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.IPieceOfEvidence;
import org.dice_research.fc.data.QRestrictedPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;


public class QRestrictedPathSampler implements IPathSampler{
    private static final Logger LOGGER = LoggerFactory.getLogger(QRestrictedPathSampler.class);
    protected QueryExecutionFactory qef;
    @Autowired
    public QRestrictedPathSampler(QueryExecutionFactory qef) {
        this.qef = qef;
    }

    @Override
    public String getSample(Resource subject, Resource object, IPieceOfEvidence path) {
        String output = getOneSample(subject, object, (QRestrictedPath)path);
        String jsonOutput = convertToJSON(output);
        //String jsonOutput = output;
        path.setSample(jsonOutput);
        return jsonOutput;
    }

    public static String convertToJSON(String input) {
        Map<String, String> resultMap = new HashMap<>();



        String[] mainTokens = input.split("\\) \\(");
        if(mainTokens.length==1){
            //just x1
            mainTokens[0] = mainTokens[0].replace("( ","");
            mainTokens[0] = mainTokens[0].replace(" )","");
            mainTokens[0] = mainTokens[0].replace("<","");
            mainTokens[0] = mainTokens[0].replace(">","");
            input = mainTokens[0];
        }else{
            if(mainTokens.length==2){
                //x1 and x2
                mainTokens[0] = mainTokens[0].replace("( ","");
                mainTokens[0] = mainTokens[0].replace(" )","");
                mainTokens[0] = mainTokens[0].replace("<","");
                mainTokens[0] = mainTokens[0].replace(">","");

                mainTokens[1] = mainTokens[1].replace("( ","");
                mainTokens[1] = mainTokens[1].replace(" )","");
                mainTokens[1] = mainTokens[1].replace("<","");
                mainTokens[1] = mainTokens[1].replace(">","");

                // because we will split base d on =
                input = mainTokens[0]+"="+mainTokens[1].replace(" ?x","?x");
            }else{
                LOGGER.error(mainTokens.length+"cn not parse this result "+ input);
            }
        }

        // Split the input string by spaces and parentheses

        String[] tokens = input.split("=");
        if(tokens.length==3){
            String eeror = "eo";
        }
        // Process the tokens to extract key-value pairs
        for (int i = 0; i < tokens.length; i=i+1) {
            if (tokens[i].startsWith("?x")) {
                String key = tokens[i];
                String value = tokens[i + 1];
                resultMap.put(key, value);
            }
        }

        // Convert the map to a JSON string
        StringBuilder jsonBuilder = new StringBuilder("{ ");
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            jsonBuilder.append("\"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\", ");
        }

        // Remove the trailing comma and space
        if (jsonBuilder.length() > 2) {
            jsonBuilder.setLength(jsonBuilder.length() - 2);
        }

        jsonBuilder.append(" }");

        return jsonBuilder.toString();
    }

    private String getOneSample(Resource subject, Resource object, QRestrictedPath path) {
    if(path.getPathElements().size()<2){
        return "";
    }

        Query query = QueryFactory.create(generateQuery(subject,object,path));
        StringBuilder result = new StringBuilder();
        try (QueryExecution queryExecution = qef.createQueryExecution(query)) {
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext()) {
                String s = resultSet.next().toString();
                    result.append(s);
            }
        }
        return result.toString();
    }

    private String generateQuery(Resource subject, Resource object, QRestrictedPath path) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * where { ");
        for(int i = 0 ; i < path.getPathElements().size() ; i++){
            Pair<Property, Boolean> current = path.getPathElements().get(i);

            String first = "?x" + i;
            if(i == 0){
                first = "<"+subject.getURI().toString()+">";
            }

            String second = "?x" + (i+1);
            if(i+1 == path.getPathElements().size()){
                second = "<"+object.getURI().toString()+">";
            }

            if(!current.getSecond()){
                // is false swap first and second
                String temp = first;
                first = second;
                second = temp;
            }
            sb.append(first+" <"+ current.getFirst().getURI()+"> " + second+" . ");
        }
        sb.append(" } LIMIT 1");
        return sb.toString();
    }
}
