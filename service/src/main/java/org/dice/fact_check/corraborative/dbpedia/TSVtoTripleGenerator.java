package org.dice.fact_check.corraborative.dbpedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;



public class TSVtoTripleGenerator {
	
	public static String line;

	public static void tsvToTriple(File sourcetsv, File outputTriple) throws IOException 
	{
		Model model = ModelFactory.createDefaultModel();
		PrintWriter pw = new PrintWriter(new FileWriter("/home/datascienceadmin/Downloads/dbpedia.3.8_v2.ttl"));
		String subject = "";
		String object = "";
		String predicate = "";
		StringTokenizer st;
		BufferedReader TSVFile = null;
		try {
			TSVFile = new BufferedReader(new FileReader(sourcetsv));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			line = TSVFile.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i =0;
		while(line!=null)
		{
			st = new StringTokenizer(line, "\t");
			subject = st.nextToken().split(":")[1].replaceAll(">", "");
			predicate = st.nextToken().split(":")[1].replaceAll(">", "");
			object = st.nextToken().split(":")[1].replaceAll(">", "");
			
			if(subject=="" || object=="" || predicate=="") {
				System.out.println("found empty");
			}
			Statement statement = ResourceFactory.createStatement(ResourceFactory.createResource("http://dbpedia.org/resource/"+subject), 
					ResourceFactory.createProperty("http://dbpedia.org/ontology/"
					+predicate), ResourceFactory.createResource("http://dbpedia.org/resource/"
							+object));
			pw.write("<http://dbpedia.org/resource/"+subject+">");
			pw.write(" ");
			pw.write("<http://dbpedia.org/ontology/"+predicate+">");
			pw.write(" ");
			pw.write("<http://dbpedia.org/resource/"+object+">");
			pw.write(" .");
			pw.println();
			//model.add(statement);
			try {
				line = TSVFile.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}

			//model.write(new FileOutputStream(outputTriple), "N-TRIPLES");

		System.out.println("Wrote "+i+" triples to file");
		pw.flush();
		pw.close();
		
	}
	
	public static void generateDataset() throws FileNotFoundException
	{
		Model inputModel = ModelFactory.createDefaultModel();
		inputModel.read(new FileInputStream(new File("/home/datascienceadmin/infobox_properties_en.ttl")), "TURTLE");
	}
	
	public static void main(String[] args) throws IOException {
		
		//tsvToTriple(new File("/home/datascienceadmin/Downloads/dbpedia.3.8.tsv"), new File("/home/datascienceadmin/Downloads/dbpedia.3.8_v1.ttl"));
		generateDataset();

	}

}
