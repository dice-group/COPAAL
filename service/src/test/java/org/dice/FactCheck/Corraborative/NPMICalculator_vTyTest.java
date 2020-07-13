package org.dice.FactCheck.Corraborative;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.apache.jena.vocabulary.RDFS;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice.FactCheck.Corraborative.filter.npmi.NPMIFilterException;
import org.junit.Assert;
import org.junit.Test;

public class NPMICalculator_vTyTest {
	@Test
    public void testQueries() throws NPMIFilterException, ParseException {
//System.out.println("OK");
	        Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Bill_Gates");
	        Resource object = ResourceFactory.createResource("http://dbpedia.org/resource/United_States");
	        Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/nationality");
	        Statement statement = ResourceFactory.createStatement(subject, property, object);
	        
	        int count_predicate_Occurrence=130523;
			int count_subject_Triples=126000;
			int count_object_Triples=2523;
			String path="http://dbpedia.org/ontology/child;http://dbpedia.org/ontology/birthPlace";
			String builder=	"?x1 ?p1 ?s;?x1 ?p2 ?o";
			
			QueryExecutioner queryExecutioner=new QueryExecutioner();
			String serviceRequestURL="http://synthg-fact.cs.upb.de:8890/sparql";
			queryExecutioner.setServiceRequestURL(serviceRequestURL);

			NPMICalculator calculator = new NPMICalculator(path, builder, statement, null, 2, count_predicate_Occurrence,
	                count_subject_Triples, count_object_Triples, null, null, queryExecutioner);
	       System.out.println(calculator.calculatePMIScore_vTy());
//	        System.out.println(calculator.calculatePMIScore());//needs subjTypes/objTypes as Nodes
    }
}
