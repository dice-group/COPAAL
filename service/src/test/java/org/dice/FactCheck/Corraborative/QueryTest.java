package org.dice.FactCheck.Corraborative;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.junit.Test;

public class QueryTest {

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
    }

  }



}
