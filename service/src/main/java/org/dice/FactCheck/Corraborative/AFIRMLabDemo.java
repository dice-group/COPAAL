package org.dice.FactCheck.Corraborative;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.cli.*;
import org.apache.jena.rdf.model.*;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice.FactCheck.Corraborative.Query.SparqlQueryGenerator;

public class AFIRMLabDemo {

	public static void main(String[] args) {

        Options options = new Options();

        Option host = new Option("h", "host", true, "Host address for sparql end point");
        host.setRequired(true);
        options.addOption(host);

        Option port = new Option("p", "port", true, "port number for sparql service");
        port.setRequired(true);
        options.addOption(port);

        Option output = new Option("o", "output", true, "output file");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        String outputFile = cmd.getOptionValue("output");
        String hostAddress = cmd.getOptionValue("host");
        String portNumber = cmd.getOptionValue("port");

		try {
		    QueryExecutioner queryExecutioner = new QueryExecutioner();
            queryExecutioner.setServiceRequestURL("http://"+hostAddress+":"+portNumber+"/sparql");
            FactChecking factChecking = new FactChecking(new SparqlQueryGenerator(), queryExecutioner);
            System.out.println("Generating result file for US-Vice-President dataset....");
            Model outputModel = factChecking.checkFacts(getModelfromFile(AFIRMLabDemo.class.getResource("/Real_World_Nationality.nt").getFile()), false, 2);
            outputModel.write(new FileOutputStream(outputFile), "N-TRIPLES");
            System.out.println("Finished generating result file.\n" +
                    "The result file will be generated at the location you specified.\n"+
                    "For generating ROC-AUC score on our benchmarking platform (GERBIL) do as follows:\n" +
                    "1. Go to http://swc2017.aksw.org/gerbil/config.\n" +
                    "2. Select Task: Fact Checking.\n" +
                    "3. Type the name of participating system and enter email address.\n" +
                    "4. Submit the generated output file by clicking on Select file... button.\n" +
                    "5. Select the Reference dataset: Synthetic US Vice President.\n" +
                    "6. Check the Disclaimer. Optionally check publish to publish the results on our leaderboard.\n" +
                    "7. Click on Run Experiment. After the experiment is finished click on the generated link to view experiment results.");

        }catch (Exception e){
		    System.out.println("Invalid output file "+e);
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