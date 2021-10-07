package org.dice.FactCheck.updater.service;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dice.FactCheck.updater.model.QueryResults;
import org.dice.FactCheck.updater.repository.IQueryResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Component
public class Runner implements IRunner {

    IQueryResultsRepository repository;
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public Runner(IQueryResultsRepository repository) {
        this.repository = repository;
    }

    public void run(){
        List<QueryResults> list = repository.findAll();
        for(QueryResults q : list){
            if(!q.isDone()){
                // run query
                String result = doQuery(q.getQuery());
                if(!result.equals("")){
                    q.setResponse(result);
                    q.setIsdone(true);
                    repository.save(q);
                    System.out.println("save Done");
                }
            }
        }
    }

    private String doQuery(String query)  {
        //query = query.replace("  ","");
        //query = query.replace("\n"," ");
      //String endpoint = "https://synthg-fact.dice-research.org/sparql";
        String endpoint = "https://dbpedia.org/sparql";

      try{
          String url = endpoint+"?query="+ URLEncoder.encode(query, "UTF-8");
          System.out.println(url);
          HttpGet request = new HttpGet(url);
          request.addHeader(HttpHeaders.ACCEPT, "application/sparql-results+xml");
          try (CloseableHttpResponse response = httpClient.execute(request)) {
              // Get HttpResponse Status
              System.out.println(response.getStatusLine().toString());
              System.out.println(response.getStatusLine().getStatusCode());
              if(response.getStatusLine().getStatusCode()!=200 ){
                  return "";
              }
              HttpEntity entity = response.getEntity();
              Header headers = entity.getContentType();
              System.out.println(headers);

              if (entity != null) {
                  // return it as a String
                  String result = EntityUtils.toString(entity);
                  System.out.println(result);
                  return result;
              }
          }

      }catch (Exception ex){
        System.out.println(ex.getMessage());
      }
        return "";
    }
}
