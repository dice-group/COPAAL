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
import org.dice_research.fc.sparql.filter.IRIFilter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public class QRestrictedPathSampler implements IPathSampler{
    protected QueryExecutionFactory qef;
    @Autowired
    public QRestrictedPathSampler(QueryExecutionFactory qef) {
        this.qef = qef;
    }

    @Override
    public String getSample(Resource subject, Resource object, IPieceOfEvidence path) {
        String output = getOneSample(subject, object, (QRestrictedPath)path);
        path.setSample(output);
        return output;
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
