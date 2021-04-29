package org.dice.FactCheck.Corraborative;

import static org.junit.Assert.assertTrue;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.junit.Test;

public class QueryTest {
  // these tests may not passed


  @Test
  public void test() {
    QueryExecutioner queryExecutioner = new QueryExecutioner();
    queryExecutioner.setServiceRequestURL("https://dbpedia.org/sparql");

    String queryString =
        "SELECT  (SUM(( ?b1 * ?b2 )) AS ?c)\n" + "WHERE\n" + "  { SELECT  (count(*) AS ?b2) ?b1\n"
            + "    WHERE\n" + "      { ?s  <http://dbpedia.org/ontology/wikiPageWikiLink>  ?x1 ;\n"
            + "            a                     <http://dbpedia.org/ontology/Film>\n"
            + "        { SELECT  (count(*) AS ?b1) ?x1\n" + "          WHERE\n"
            + "            { ?o  <http://dbpedia.org/ontology/wikiPageWikiLink>  ?x1 ;\n"
            + "                  a                     <http://dbpedia.org/ontology/Person>\n"
            + "            }\n" + "          GROUP BY ?x1\n" + "        }\n" + "      }\n"
            + "    GROUP BY ?b1\n" + "  }\n" + "";

    Query query = QueryFactory.create(queryString);
    try (QueryExecution pathQueryExecution = queryExecutioner.getQueryExecution(query);) {
      ResultSet resSet = pathQueryExecution.execSelect();
      double count_Path_Occurrence = resSet.next().get("?c").asLiteral().getDouble();
      assertTrue(count_Path_Occurrence > 0);
    }

  }

  @Test
  public void MultiCalltest() {

    for (int i = 0; i < 1; i++) {
      double res = timeConsumingQuery();
      // double res = simpleCountQuery();
      assertTrue(res > 0);
    }
  }


  public double simpleCountQuery() {

    QueryExecutioner queryExecutioner = new QueryExecutioner();
    queryExecutioner.setServiceRequestURL("https://dbpedia.org/sparql");

    String queryString = "    SELECT  (count(*) AS ?b1) ?x1\n" + "        WHERE\n"
        + "          { ?o  <http://dbpedia.org/ontology/wikiPageWikiLink>  ?x1 ;\n"
        + "                a                     <http://dbpedia.org/ontology/Person>\n"
        + "          }\n" + "        GROUP BY ?x1";

    Query query = QueryFactory.create(queryString);
    try (QueryExecution pathQueryExecution = queryExecutioner.getQueryExecution(query);) {
      ResultSet resSet = pathQueryExecution.execSelect();
      double count_Path_Occurrence = resSet.next().get("?c").asLiteral().getDouble();
      return count_Path_Occurrence;
    }

  }



  public double timeConsumingQuery() {

    QueryExecutioner queryExecutioner = new QueryExecutioner();
    queryExecutioner.setServiceRequestURL("https://dbpedia.org/sparql");

    String queryString = "SELECT  (count(*) AS ?c)\n" + "WHERE { \n"
        + "SELECT DISTINCT ?s ?o WHERE {\n" + "  { SELECT  DISTINCT ?s ?x1 WHERE {\n"
        + "      ?s  <http://dbpedia.org/ontology/birthPlace>  ?x1 .\n"
        + "      FILTER EXISTS { ?s  <http://dbpedia.org/ontology/birthPlace>  _:b0 }\n" + "   }}\n"
        + "  { SELECT  DISTINCT ?o ?x1 WHERE {\n"
        + "        ?o  <http://dbpedia.org/ontology/country>  ?x1 .\n"
        + "        FILTER EXISTS { _:b1  <http://dbpedia.org/ontology/birthPlace>  ?o }\n"
        + "      }}\n" + "}\n" + "}";

    Query query = QueryFactory.create(queryString);
    try (QueryExecution pathQueryExecution = queryExecutioner.getQueryExecution(query);) {
      pathQueryExecution.setTimeout(5000, 5000);
      ResultSet resSet = pathQueryExecution.execSelect();
      double count_Path_Occurrence = resSet.next().get("?c").asLiteral().getDouble();
      return count_Path_Occurrence;
    } catch (Exception ex) {
      return 0;
    }


    /*
     * QueryEngineHTTP
     * objectToExec=QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql",YOUR_QUERY);
     * objectToExec.addParam("timeout","5000"); //5 sec resultset=objectToExec.execSelect();
     */

  }

}
