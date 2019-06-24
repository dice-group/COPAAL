package org.dice.FactCheck.Corraborative;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.lang.sparql_11.ParseException;

public class Test {

	public static void main(String[] args) throws ParseException, IOException {
		
		FileOutputStream fout = new FileOutputStream(new File("/home/datascienceadmin/Output/Synthetic_US_Vice_President_output.txt"));
		PrintWriter pw = new PrintWriter(fout);
		TreeMap<Integer, Double> map = new TreeMap<Integer, Double>();
		Model model = ModelFactory.createDefaultModel();
		//model.read(new FileInputStream(new File("/home/datascienceadmin/Output/Synthetic_US_Vice_President_output_1.nt")), 
		//		"N-TRIPLES");
		model = RDFDataMgr.loadModel("/home/datascienceadmin/Output/Synthetic_US_Vice_President_output_1.nt");
		
		StmtIterator it = model.listStatements();
		while(it.hasNext())
		{
			Statement s = it.nextStatement();
			int id = Integer.parseInt(s.getSubject().getLocalName().split("-")[1]);
			double score = s.getDouble();
			map.put(id, score);
		}
		
		for(Map.Entry<Integer, Double> element : map.entrySet())
		{
			pw.write(element.getKey()+"\t");
			pw.write(element.getValue().toString());
			pw.println();
			
		}
		pw.flush();
		pw.close();
		
		/*Model m = ModelFactory.createDefaultModel();
		
		try (BufferedReader br = new BufferedReader(new FileReader("/home/datascienceadmin/Output/Synthetic_US_Vice_President_output.txt"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	Resource stmt = ResourceFactory.createResource("http://swc2017.aksw.org/task2/dataset/usvp-"+line.split("\t")[0]);
		    	m.addLiteral(stmt, ResourceFactory.createProperty("http://swc2017.aksw.org/hasTruthValue"),
						Double.parseDouble(line.split("\t")[1]));
		    }
		}
		
		m.write(new FileOutputStream(new File("/home/datascienceadmin/Output/Synthetic_US_Vice_President_output_2.nt")),
				"N-TRIPLES");*/

	}

}
