package org.dice.FactCheck.Corraborative;


import java.io.FileNotFoundException;

import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.FactCheck.Corraborative.FactChecking;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice.FactCheck.Corraborative.Query.SparqlQueryGenerator;
import org.dice.FactCheck.Corraborative.UIResult.CorroborativeGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactCheckingDemo {

    public static QueryExecutionFactoryHttp qef = new QueryExecutionFactoryHttp("http://131.234.29.111:8890/sparql");

	public static void main(String[] args) throws InterruptedException, FileNotFoundException, ParseException {
		
		org.apache.log4j.PropertyConfigurator.configure(FactChecking.class.getClassLoader().getResource("properties/log4j.properties"));
		FactChecking factChecking = new FactChecking(new SparqlQueryGenerator(), new QueryExecutioner(), new CorroborativeGraph());

		factChecking.checkFacts(getTestModel(), false, 2);


	}
	
	public static Model getTestModel() {
        final Model model = ModelFactory.createDefaultModel();
        Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Bill_Gates");
        Resource object = ResourceFactory.createResource("http://dbpedia.org/resource/United_States");
        Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/nationality");
        Statement statement = ResourceFactory.createStatement(subject, property, object);
        model.add(statement);
        return model;
    }

}