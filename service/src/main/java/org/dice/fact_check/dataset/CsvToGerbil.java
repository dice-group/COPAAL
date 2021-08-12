package org.dice.fact_check.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import com.google.common.io.Files;
import com.opencsv.CSVReader;

public class CsvToGerbil {
	
	
	public static void generateGerbilFile(File input, String dataset, File outputFile) throws IOException
	{
		int i = 1001;
		Model model = ModelFactory.createDefaultModel();
		Reader reader = Files.newReader(input, Charset.defaultCharset());
		CSVReader csvReader = new CSVReader(reader);
		
		String[] nextRecord;
		csvReader.readNext();
        while ((nextRecord = csvReader.readNext()) != null) {
        	Resource subject = ResourceFactory.createResource("http://swc2017.aksw.org/task2/dataset/"+dataset+"-"+i);
			Property property = ResourceFactory.createProperty("http://swc2017.aksw.org/hasTruthValue");
			model.addLiteral(subject, property, Double.parseDouble(nextRecord[6]));
			i++;
        }
        csvReader.close();
        model.write(new BufferedWriter(new FileWriter(outputFile)),"N-TRIPLES");
	}

	public static void main(String[] args) throws IOException {
		
		generateGerbilFile(new File("/home/datascienceadmin/Results_KS/out_relklinker_fb_domain_2019-03-11.csv"), "fb_domain",
				new File("/home/datascienceadmin/Results_KS/kl-rel_fb_domain_score.nt"));

	}

}
