package org.dice.FactCheck.Corraborative;

import static org.junit.Assert.assertTrue;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.FactCheck.Corraborative.Config.Config;
import org.dice.FactCheck.Corraborative.PathGenerator.IPathGeneratorFactory.PathGeneratorType;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice.FactCheck.Corraborative.Query.RemoteQueryExecutioner;
import org.dice.FactCheck.Corraborative.filter.npmi.NPMIFilterException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NPMICalculator_vTyTest {

  @Autowired private RemoteQueryExecutioner queryExecutioner;

  @Autowired private Config config;

  @Test
  public void testQueries() throws NPMIFilterException, ParseException {
    queryExecutioner.setServiceRequestURL(
        config.serviceURLResolve(PathGeneratorType.defaultPathGenerator));
    Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Bill_Gates");
    Resource object = ResourceFactory.createResource("http://dbpedia.org/resource/United_States");
    Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/nationality");
    Statement statement = ResourceFactory.createStatement(subject, property, object);

    int count_predicate_Occurrence = 130523;
    int count_subject_Triples = 126000;
    int count_object_Triples = 2523;
    String path = "http://dbpedia.org/ontology/child;http://dbpedia.org/ontology/birthPlace";
    String builder = "?x1 ?p1 ?s;?x1 ?p2 ?o";

    NPMICalculator calculator =
        new NPMICalculator(
            path,
            builder,
            statement,
            null,
            2,
            count_predicate_Occurrence,
            count_subject_Triples,
            count_object_Triples,
            null,
            null,
            queryExecutioner);
    System.out.println(calculator.calculatePMIScore_vTy());
    assertTrue(calculator.calculatePMIScore_vTy() > 0);
  }
}
