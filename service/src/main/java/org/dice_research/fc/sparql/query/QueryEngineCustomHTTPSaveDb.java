package org.dice_research.fc.sparql.query;

import org.apache.http.client.HttpClient;
import org.apache.jena.query.Query;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.dice_research.fc.paths.model.QueryResults;
import org.dice_research.fc.paths.repository.IPathRepository;
import org.dice_research.fc.paths.repository.IQueryResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

public class QueryEngineCustomHTTPSaveDb extends QueryEngineCustomHTTP{

    @Autowired
    protected IQueryResultsRepository repository;

    /**
     * constructor of the class
     *
     * @param query   is a query to run
     * @param client
     * @param service is a url of a SPARQL endpoint
     */
    public QueryEngineCustomHTTPSaveDb(Query query, HttpClient client, String service) {
        super(query, client, service);
    }

    @Override
    public ResultSet execSelect() {
        String queryResult = searchDb(query);
        if(queryResult.equals("")){
            // nothing found in db then do query
            queryResult = createRequest();
            // the result is not a valid XML then replace with an empty XML
            if(queryResult.length()<10) {
                QueryResults forSave = new QueryResults(query.toString(),"",false);
                repository.save(forSave);
                queryResult = emptyXML();
            }else{
                QueryResults forSave = new QueryResults(query.toString(),queryResult,true);
                repository.save(forSave);
            }

        }

        ResultSet resultSet = ResultSetFactory.fromXML(queryResult);

        return resultSet;

    }

    private String searchDb(Query query) {
        List<QueryResults> q = repository.findByQueryAndIsdoneTrue(query.toString());
        if(q.size()>0){
            return q.get(0).getQuery();
        }else{
            return "";
        }
    }
}
