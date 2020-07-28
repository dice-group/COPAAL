package org.dice.FactCheck.Corraborative;

import java.io.FileNotFoundException;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice.FactCheck.Corraborative.Query.SparqlQueryGenerator;
import org.dice.FactCheck.Corraborative.UIResult.CorroborativeGraph;
import org.dice.FactCheck.Corraborative.UIResult.CorroborativeTriple;
import org.dice.FactCheck.Corraborative.UIResult.Path;
import org.junit.Test;

public class FactCheckingTest {
	
	@Test
	public void QryTest() throws ParseException {
		QueryExecutioner queryExecutioner=new QueryExecutioner();
		String serviceRequestURL="http://synthg-fact.cs.upb.de:8890/sparql";
		queryExecutioner.setServiceRequestURL(serviceRequestURL);
		//String cntQuerytxt="SELECT  (count(*) AS ?c) WHERE  { ?s  <http://dbpedia.org/ontology/nationality>  ?o}";
		SelectBuilder qryBuilder = new SelectBuilder();
		qryBuilder.addVar("count(*)", "?c");
		qryBuilder.addWhere(NodeFactory.createVariable("s"), "<http://dbpedia.org/ontology/nationality>", NodeFactory.createVariable("o"));
		
		Query cntQuery =qryBuilder.build();
		System.out.println(cntQuery.toString());
		 QueryExecution queryExecution = queryExecutioner.getQueryExecution(cntQuery);
		 // ResultSet resultSet = queryExecution.execSelect();
		 double count_Occurrence;
		 //if (resultSet.hasNext())
		count_Occurrence = queryExecution.execSelect().next().get("?c").asLiteral().getDouble();
		        queryExecution.close();
		   System.out.println("Count: "+ count_Occurrence);
	}
	
	
	protected void FCTest(Statement statement,int pathLen,boolean vTy) throws FileNotFoundException, InterruptedException, ParseException {
		final Model model = ModelFactory.createDefaultModel();
//		Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Bill_Gates");
////	        Resource object = ResourceFactory.createResource("http://dbpedia.org/resource/United_Kingdom");
//		Resource object = ResourceFactory.createResource("http://dbpedia.org/resource/United_States");
//		Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/nationality");
//		//Statement statement = ResourceFactory.createStatement(subject, property, object);

		model.add(statement);
		
		QueryExecutioner queryExecutioner=new QueryExecutioner();
		String serviceRequestURL1="http://synthg-fact.cs.upb.de:8890/sparql";
		queryExecutioner.setServiceRequestURL(serviceRequestURL1);
		
		FactChecking factChecking = new FactChecking(new SparqlQueryGenerator(),queryExecutioner , new CorroborativeGraph());
//		boolean vTy=true;
		CorroborativeGraph cg=factChecking.checkFacts(model, pathLen,vTy);//vTy:virtual types true
		System.out.println("Subject: " + statement.getSubject());
		System.out.println("Property: " + statement.getPredicate());
		System.out.println("Object: " + statement.getObject());
		System.out.println("Count paths: " + cg.getPathList().toArray().length);
		for(Path p: cg.getPathList()) {
			System.out.println("============= pth len:"+p.getPath().toArray().length+' '+p.getPathScore());
			for(CorroborativeTriple trp:p.getPath()) 
				System.out.println(trp.getSubject()+' '+trp.getProperty()+' '+trp.getObject());
		}
		System.out.println("virtual Types: "+vTy+" Score:"+cg.getGraphScore());     
		
	}

	@Test
	public void FC_EducationTest() throws FileNotFoundException, InterruptedException, ParseException {
	    final Model model = ModelFactory.createDefaultModel();
//		    Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Nia_Gill");
		//    Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Theodore_McKee");
//	    Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Nahum_Tate");
	    
	   String sstr="http://dbpedia.org/resource/John_Kemeny_(film_producer)",
			   ostr="http://dbpedia.org/resource/Canada";
	   Resource subject = ResourceFactory.createResource(sstr);
	   Resource object = ResourceFactory.createResource(ostr);
//	    Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/deathPlace");
//	    Resource object = ResourceFactory.createResource("http://dbpedia.org/resource/Southwark");
//	        Resource object = ResourceFactory.createResource("http://dbpedia.org/resource/Bachelor_of_Arts");
//	        Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/education");
//			Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Bill_Gates");
//	        Resource object = ResourceFactory.createResource("http://dbpedia.org/resource/United_States");
	        Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/nationality");
//	//Statement statement = ResourceFactory.createStatement(subject, property, object);

	        Statement statement = ResourceFactory.createStatement(subject, property, object);
	        FCTest(statement,3,true);     
	       FCTest(statement,3,false);     

	}
}
