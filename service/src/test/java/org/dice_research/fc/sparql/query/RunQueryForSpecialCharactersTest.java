package org.dice_research.fc.sparql.query;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.dice_research.fc.run.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RunQueryForSpecialCharactersTest {
/*    @Test
    public void customhttpclientShouldHandleSpecialCharactersCorrect(){
        QueryExecutionFactory qef = new QueryExecutionFactoryCustomHttp("https://dbpedia.org/sparql");
        String query = "select distinct ?p ?o where {<http://dbpedia.org/resource/Ouțul_River> ?p ?o} LIMIT 100";
        QueryExecution q = qef.createQueryExecution(query);
        ResultSet result = q.execSelect();
        System.out.println(result);
        Assert.assertEquals(result.hasNext(),true);
    }*/
}
