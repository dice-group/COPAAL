package org.dice.FactCheck.Corraborative;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.*;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice.FactCheck.Corraborative.Query.SparqlQueryGenerator;
import org.dice.FactCheck.Corraborative.UIResult.CorroborativeGraph;

public class AFIRMLabDemo {

	public static void main(String[] args) {

		try {
		    QueryExecutioner queryExecutioner = new QueryExecutioner();
            queryExecutioner.setServiceRequestURL("http://131.234.29.111:8890/sparql");
            FactChecking factChecking = new FactChecking(new SparqlQueryGenerator(), queryExecutioner, new CorroborativeGraph());
            System.out.println("Generating result file for US-Vice-President dataset....");
            Model outputModel = factChecking.checkFacts(getModelfromFile(AFIRMLabDemo.class.getResource("/US_Vice_President.nt").getFile()), false, 2);
            outputModel.write(new FileOutputStream("/home/zafar/Documents/USVP.nt"), "N-TRIPLES");
            System.out.println("Finished generating result file.\n" +
                    "The result file will be generated at the location you passed.\n"+
                    "For generating ROC-AUC score on our benchmarking platform (GERBIL) do as follows:\n" +
                    "1. Go to http://swc2017.aksw.org/gerbil/config.\n" +
                    "2. Select Task: Fact Checking.\n" +
                    "3. Type the name of participating system and enter email address.\n" +
                    "4. Submit the generated output file by clicking on Select file... button.\n" +
                    "5. Select the Reference dataset: Synthetic US Vice President.\n" +
                    "6. Check the Disclaimer. Optionally check publish to publish the results on our leaderboard.\n" +
                    "7. Click on Run Experiment. After the experiment is finished click on the generated link to view experiment results.");

        }catch (Exception e){
		    System.out.println(e);
        }
	}
	
	public static Model getTestModel() {
        final Model model = ModelFactory.createDefaultModel();
        Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Barack_Obama");
        Resource object = ResourceFactory.createResource("http://dbpedia.org/resource/United_States");
        Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/nationality");
        Statement statement = ResourceFactory.createStatement(subject, property, object);
        model.add(statement);
        return model;
    }

    public static Model getModelfromFile(String inputFile){
        final Model model = ModelFactory.createDefaultModel();
        try {
            model.read(new FileInputStream(new File(inputFile)), null, "N-TRIPLES");
        } catch (FileNotFoundException e) {

            System.out.println("Exception while reading input file. Please check the input file path.");
        }
        return model;
    }

}