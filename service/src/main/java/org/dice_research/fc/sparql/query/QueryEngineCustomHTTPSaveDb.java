package org.dice_research.fc.sparql.query;

import org.apache.http.client.HttpClient;
import org.apache.jena.query.Query;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.dice_research.fc.paths.model.QueryResults;
import org.dice_research.fc.paths.repository.IQueryResultsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

public class QueryEngineCustomHTTPSaveDb extends QueryEngineCustomHTTP{

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryEngineCustomHTTPSaveDb.class);

    protected IQueryResultsRepository repository;

    /**
     * constructor of the class
     *
     * @param query   is a query to run
     * @param client
     * @param service is a url of a SPARQL endpoint
     */
    public QueryEngineCustomHTTPSaveDb(Query query, HttpClient client, String service,IQueryResultsRepository repository) {
        super(query, client, service);
        this.repository = repository;
    }

    @Override
    public ResultSet execSelect() {
        String queryResult = searchDb(query);
        if(queryResult.equals("")){
            // nothing found in db then do query
            queryResult = createRequest();
            // the result is not a valid XML then replace with an empty XML
            QueryResults forSave;
            if(queryResult.length()<10) {
                forSave = new QueryResults(query.toString(),"",false);
                queryResult = emptyXML();
            }else{
                forSave = new QueryResults(query.toString(),queryResult,true);
                repository.save(forSave);
            }

            // if this query is not in the DB then insert it
            if(repository.findByQuery(query.toString()).size()==0){
                repository.save(forSave);
            }

        }

        ResultSet resultSet = ResultSetFactory.fromXML(queryResult);

        return resultSet;

    }

    private String searchDb(Query query) {
        List<QueryResults> q = repository.findByQueryAndIsdone(query.toString(),true);
        if(q.size()>0){
            LOGGER.debug("Found query in DB ");
            return q.get(0).getResponse();
        }else{
            return "";
        }
    }
}
