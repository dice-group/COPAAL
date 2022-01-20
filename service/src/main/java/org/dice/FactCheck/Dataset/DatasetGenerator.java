package org.dice.FactCheck.Dataset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetGenerator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DatasetGenerator.class);
	
	public static void generateDataset(File inputFile, File outputFile) throws IOException
	{
		Model model = ModelFactory.createDefaultModel();
		Model model2 = ModelFactory.createDefaultModel();
		
		String line= "";
		int i=1001;
		BufferedReader br = new BufferedReader( new FileReader(inputFile));
		br.readLine();
		while((line=br.readLine())!=null)
		{
			LOGGER.info(line);
			String[] fact = line.split("\t");
			Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/"+fact[1].trim());
			Resource object = ResourceFactory.createResource("http://dbpedia.org/resource/"+fact[5].trim());
			Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/", fact[3].trim());
			//Property property = ResourceFactory.createProperty("http://dbpedia.org/ontology/", "commander");
			Resource id = ResourceFactory.createResource("http://swc2017.aksw.org/task2/dataset/fb_range-"+i++);
			model.add(id, RDF.type, RDF.Statement);
			//Statement statement = ResourceFactory.createStatement(subject, property, object);
			model.add(id, RDF.subject, subject);
			model.add(id, RDF.predicate, property);
			model.add(id, RDF.object, object);
			if(Integer.parseInt(fact[6])==1)
				model.addLiteral(id, ResourceFactory.createProperty("http://swc2017.aksw.org/", "hasTruthValue"), 1.0);
			else
				model.addLiteral(id, ResourceFactory.createProperty("http://swc2017.aksw.org/", "hasTruthValue"), 0.0);
			
			model2.add(subject, property, object);
		}
		
		model.write(new BufferedWriter(new FileWriter(outputFile)),"N-TRIPLES");
	}
	
	
	public static void generateFalseSet() throws FileNotFoundException {
		Model model = ModelFactory.createDefaultModel();
		Model output = ModelFactory.createDefaultModel();
		model.read(new FileInputStream(new File("/home/datascienceadmin/knowledgestream/datasets/real/institution.nt")), "N-TRIPLES");
		StmtIterator st = model.listStatements(null, RDF.subject, (RDFNode)null);
		Model model2 = ModelFactory.createDefaultModel();
		model2.read(new FileInputStream(new File("/home/datascienceadmin/knowledgestream/datasets/real/institution_false.nt")), "N-TRIPLES");
		int k=2546;
		while(st.hasNext())
		{
			RDFNode subject = st.next().getObject();
			StmtIterator st1 = model2.listStatements(subject.asResource(), null, (RDFNode)null);
			
			while(st1.hasNext())
			{
				Statement stmt = st1.next();
				Resource id = ResourceFactory.createResource("http://swc2017.aksw.org/task2/dataset/rw_institution-"+k++);
				output.add(id, RDF.type, RDF.Statement);
				//Statement statement = ResourceFactory.createStatement(subject, property, object);
				output.add(id, RDF.subject, stmt.getSubject());
				output.add(id, RDF.predicate, stmt.getPredicate());
				output.add(id, RDF.object, stmt.getObject());
				output.addLiteral(id, ResourceFactory.createProperty("http://swc2017.aksw.org/", "hasTruthValue"), 0.0);
			}
			
		}
		
		output.write(new FileOutputStream(new File("/home/datascienceadmin/knowledgestream/datasets/real/institution_false_output.nt")), "N-TRIPLES");
	}

	public static void main(String[] args) throws IOException {
		
		generateDataset(new File("/home/datascienceadmin/knowledgestream/FB_Datasets/fb_range.tsv"), 
				new File("/home/datascienceadmin/knowledgestream/FB_Datasets/fb_range.nt"));
		//generateFalseSet();

	}

}
