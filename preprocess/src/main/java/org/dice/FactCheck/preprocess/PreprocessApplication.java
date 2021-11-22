package org.dice.FactCheck.preprocess;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.dice.FactCheck.preprocess.model.CountQueries;
import org.dice.FactCheck.preprocess.model.Path;
import org.dice.FactCheck.preprocess.service.CounterQueryGeneratorService;
import org.dice.FactCheck.preprocess.service.JsonCounterService;
import org.dice.FactCheck.preprocess.service.PathService;
import org.dice.FactCheck.preprocess.service.PredicateService;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice_research.fc.sparql.restrict.TypeBasedRestriction;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@SpringBootApplication
@ComponentScan("org.dice.FactCheck.preprocess.config")
public class PreprocessApplication implements CommandLineRunner {
    //http://127.0.0.1:9080/stream?query=SELECT%20%3Fp%20WHERE%20%7B%20%3Fs%20%3Fp%20%3Fo%20.%20%7D
	//https://dbpedia.org/sparql
	String dateStr;
	private boolean isIndividual = true;
	private boolean isLiteVersion = true;
	private boolean isCompleteVersion = true;
	// show the input was a single file or a folder , for folder all the files in the folder will process
	private boolean isFolder = false;
	private final CloseableHttpClient httpClient = HttpClients.createDefault();

	JsonCounterService counterservice;

	public static void main(String[] args) {
		SpringApplication.run(PreprocessApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		counterservice = new JsonCounterService();
		if (args.length == 0){
			System.out.println("h : use this to get Help  'java -jar [jarfile] h '");
		}
		if (args.length == 1) {
			if(args[0].equals("h")){
				System.out.println("help");
				System.out.println("f [FileName] [directory for save results] [endpoint with ?stream= or sparql?query= part] ['C' for cumulative result(both Lite and Complete version), 'CL' just lite version, 'CC' just Complete version , 'I' for individual]: this will read file and run queries in that file");
				System.out.println("pc [collected_predicates.json] [len] [pathToSaveResult] [PathToSaveSerialization] [true or false for save the result]: this will read file and  generate all combination for predicates by lentgh [len]  ");
				System.out.println("gq [predicate] [domain] [range] [predicate combination files] [pathToSaveResults]: this will read the predicate path and generate all queries]  ");
			}
		}

		if(args[0].equals("f")){
			if(args.length == 6){
				System.out.println("looking at "+ args[1]+" for a file");
				/*try {
					String tempPath = new File(PreprocessApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getPath();
					System.out.println("working directory is "+ tempPath);
				}catch (Exception ex){
					System.out.println(ex);
				}*/

				File inputFileOrFolder = new File(args[1]);
				if (!inputFileOrFolder.exists()) {
					System.out.println("no file exist");
					return;
				}

				if (inputFileOrFolder.isDirectory()) {
					isFolder = true;
				}else{
					isFolder = false;
				}

				File f = new File(args[2]);
				if (!f.exists()) {
					System.out.println("folder for save results not exist");
					return;
				}

				if (!f.isDirectory()) {
					System.out.println(args[2] + " is not a directory");
					return;
				}

				if (!f.canRead()) {
					System.out.println(args[0] + " is not readable");
					return;
				}


				// how save the result
				if(args[4].equals("I")){
					isIndividual = true;
				}else{
					if(args[4].equals("C")){
						isIndividual = false;
						isCompleteVersion = true;
						isLiteVersion = true;
					}else{
						if(args[4].equals("CL")){
							isIndividual = false;
							isCompleteVersion = false;
							isLiteVersion = true;
						}else {
							if(args[4].equals("CC")){
								isIndividual = false;
								isCompleteVersion = true;
								isLiteVersion = false;
							}else {
								System.out.println("The 4Th argument should be C or I or CC or CL , see the help");
							}
						}
					}
				}

				// set date str
				Date date = Calendar.getInstance().getTime();
				DateFormat dateFormat = new SimpleDateFormat("MM-dd_hh-mm");
				dateStr = dateFormat.format(date);


				if(!isFolder) {
					processTheFile(args[1], args[2], inputFileOrFolder.getName(), args[3]);
				}else{
					File[] listOfFiles = inputFileOrFolder.listFiles();
					for (int i = 0; i < listOfFiles.length; i++) {
						if (listOfFiles[i].isFile()) {
							processTheFile(listOfFiles[i].getPath(), args[2], listOfFiles[i].getName(), args[3]);
							System.out.println("Done File"+listOfFiles[i].getName());
						} else if (listOfFiles[i].isDirectory()) {
							System.out.println("Directory " + listOfFiles[i].getName());
						}
					}
				}
			}
		}

		if(args[0].equals("pc")){
			System.out.println("run predicate combination generator");
			if(args.length == 6) {
			try {
				PredicateService predicateService = new PredicateService(null);

				Collection<Predicate> predicates = predicateService.allPredicates(args[1]);

				PathService pathService = new PathService();
				Collection<Path> paths = pathService.generateAllPaths(predicates, Integer.valueOf(args[2]), args[4],Boolean.parseBoolean(args[5]));

				SaveAllPathInAFileAsText(paths, args[3]);
			}catch (Exception ex){
				System.out.println(ex);
			}
			}else{
				System.out.println("the arguments are not enough");
			}
		}

		if(args[0].equals("gq")){
			System.out.println("generating queries");
			if(args.length == 6){
				System.out.println("Start generating the queries");
				CounterQueryGeneratorService service = new CounterQueryGeneratorService();

				// read all predicates
				PredicateService predicateService = new PredicateService(null);

				HashSet<Predicate> predicates = (HashSet<Predicate>) predicateService.allPredicates("collected_predicates.json");

				Map<String,Predicate> predicatesMap = predicates.stream().collect(Collectors.toMap(p->p.getProperty().getURI(),p->p));

				System.out.println("map of predicates loaded"+predicates.size());

				// The First Property
				//Property
				Model model = ModelFactory.createDefaultModel();
				Property property = model.createProperty(args[1]);

				//Domain
				Set<String> domainSet = new HashSet<String>();
				domainSet.add(args[2]);
				ITypeRestriction domain = new TypeBasedRestriction(domainSet);

				//Range
				Set<String> rangeSet = new HashSet<String>();
				rangeSet.add(args[3]);
				ITypeRestriction range = new TypeBasedRestriction(rangeSet);

				Predicate predicate = new Predicate(property,domain,range);

				Collection<Path> paths = readPredicateCombinationFromFile(args[4], predicatesMap);

				System.out.println("paths are loaded "+paths.size());

				CountQueries queries = service.generateCountQueries(predicate,paths);

				try {
					System.out.println("queries are loaded "+queries.whatIsTheSize());
				}catch (Exception ex){
					System.out.println(ex);
				}
				saveTheCountQueriesInFile(queries, args[5],predicate.getProperty().getLocalName());
			}
		}
	}

	private Collection<Path> readPredicateCombinationFromFile(String filePath, Map<String,Predicate> predicatesMap) throws Exception {
		Set<Path> returnSet = new HashSet<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			long lineCounter = 1;
			while ((line = br.readLine()) != null) {
				Path path = convertTextToPath(line, predicatesMap);
				returnSet.add(path);
			}
		}catch (Exception ex){
			System.out.println(ex);
			throw ex;
		}
		return returnSet;
	}

	private Path convertTextToPath(String line, Map<String,Predicate> predicatesMap) throws Exception {
		Path path = new Path();
		String[] parts = line.split(">");
		for(int i = 0 ; i < parts.length ; i++){
			parts[i] = parts[i].replace("<","");
			if(parts[i].charAt(0)=='^'){
				// is inverted
				parts[i] = parts[i].replace("^","");
				path.addPart(makePredicateFromTextGerDomainAndRangeFromFile(parts[i], predicatesMap),true);
			}else{
				path.addPart(makePredicateFromTextGerDomainAndRangeFromFile(parts[i], predicatesMap),false);
			}
		}
		return path;
	}

	private Predicate makePredicateFromTextGerDomainAndRangeFromFile(String predicateURI, Map<String,Predicate> predicatesMap) throws Exception {

		if(predicatesMap.containsKey(predicateURI)){
			return predicatesMap.get(predicateURI);
		}
		throw new Exception("the predicate is not in the json file "+predicateURI + "map size is :"+predicatesMap.size());
	}

	private void SaveAllPathInAFileAsText(Collection<Path> paths,String pathToSave) {
		try{
			FileWriter myWriter = new FileWriter(pathToSave);

			for (Iterator<Path> iterator = paths.iterator(); iterator.hasNext();) {
				Path p = iterator.next();
				myWriter.write(p.toString());
			}
			myWriter.close();
		}catch (Exception ex){

		}
	}

	private void processTheFile(String filePath, String pathForSaveResults, String fileName, String endpoint) {
		// read the query file
		System.out.println("Start Process the File"+  filePath);
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			Integer lineCounter = 1;
			while ((line = br.readLine()) != null) {
				System.out.println(filePath+" "+lineCounter);
				// process the line.
				line = line.replace("(count(DISTINCT *) AS ?sum)"," DISTINCT ?s ?o ");
					// check fact
					System.out.println("start do the query");
					String tempQueryResultFile = doQuery(line, endpoint);
					System.out.println("query is done and result save at :"+tempQueryResultFile);
					if(!tempQueryResultFile.equals("")) {
						System.out.println("Start count the result");
						long resultNumber = counterservice.count(tempQueryResultFile);
						if(resultNumber == -1){
							System.out.println("Can not count the result for this line "+ lineCounter);
						}else{
							System.out.println("Done counting the result");
							System.out.println("start save the result");
							save(line, resultNumber, pathForSaveResults, isIndividual, isLiteVersion, isCompleteVersion, fileName);
							System.out.println("Done saving the result");
							System.out.println("running query was successful");
						}
					}else{
						System.out.println("result is empty");
					}
					// remove do query temp file
					File forDelete = new File(tempQueryResultFile);
					if(forDelete.exists()){
						System.out.println("deleting "+ forDelete);
						forDelete.delete();
						System.out.println("deleted ");
					}
				lineCounter = lineCounter + 1;
				System.out.println("read next line");
			}
			System.out.println("Done for this File");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeToFile(String path, String query, long CountResult) throws Exception {

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
				pw.print(query);
				pw.write("\t");
				pw.write(String.valueOf(CountResult));
				pw.println("");
			pw.flush();
		} finally {
			pw.close();
		}
	}

	// save each file or save all result in one file

	private void save(String query,long CountResult ,String path, Boolean individual, Boolean isLiteVersion , Boolean isCompleteVersion, String queriesFileName)  {
		if(individual){
			String oldQuery = new String(query);
			query = query.replace(" ","").replace("\n","");
			String filePath = path+DigestUtils.md5Hex(query).toUpperCase()+".tsv";
			System.out.println("save result at "+filePath);
			try {
				writeToFile(filePath, oldQuery, CountResult);
			}catch(Exception ex){
				System.out.println(ex);
			}
		}else{
			// write result in one File
			if(isCompleteVersion){
				try
				{
					String filename= path+queriesFileName+"CumulativeResult-"+dateStr+".tsv";
					FileWriter fw = new FileWriter(filename,true); //the true will append the new data
					fw.write(query);
					fw.write("\t");
					fw.write(String.valueOf(CountResult));
					fw.write("\n");
					fw.close();
				}
				catch(IOException ioe)
				{
					System.err.println("IOException: " + ioe.getMessage());
				}
			}

			if(isLiteVersion){
				try
				{
					String filename= path+queriesFileName+"LiteCumulativeResult-"+dateStr+".tsv";
					FileWriter fw = new FileWriter(filename,true); //the true will append the new data
					fw.write(query);
					fw.write("\t");
					fw.write(String.valueOf(CountResult));
					fw.write("\n");
					fw.close();
				}
				catch(IOException ioe)
				{
					System.err.println("IOException: " + ioe.getMessage());
				}
			}
		}

	}

	private void saveTheCountQueriesInFile(CountQueries queries, String pathTosave,String predicate) {
		String filename= pathTosave+"CoOccurrenceCountQueries-"+predicate+"-"+dateStr+".tsv";
		try {
			FileWriter fw = new FileWriter(filename, true); //the true will append the new data

			for(String s : queries.getCoOccurrenceCountQueries()){
				fw.write(s);
				fw.write("\n");
			}
			fw.close();
		}catch (Exception ex){
			System.out.println(ex);
		}

		filename= pathTosave+"PathInstancesCountQueries-"+predicate+"-"+dateStr+".tsv";
		try {
			FileWriter fw = new FileWriter(filename, true); //the true will append the new data

			for(String s : queries.getPathInstancesCountQueries()){
				fw.write(s);
				fw.write("\n");
			}
			fw.close();
		}catch (Exception ex){
			System.out.println(ex);
		}

		filename= pathTosave+"MaxCountQueries-"+predicate+"-"+dateStr+".tsv";
		try {
			FileWriter fw = new FileWriter(filename, true); //the true will append the new data

			for(String s : queries.getMaxCountQueries()){
				fw.write(s);
				fw.write("\n");
			}
			fw.close();
		}catch (Exception ex){
			System.out.println(ex);
		}

		filename= pathTosave+"PredicateInstancesCountQueries-"+predicate+"-"+dateStr+".tsv";
		try {
			FileWriter fw = new FileWriter(filename, true); //the true will append the new data

			for(String s : queries.getPredicateInstancesCountQueries()){
				fw.write(s);
				fw.write("\n");
			}
			fw.close();
		}catch (Exception ex){
			System.out.println(ex);
		}

		filename= pathTosave+"TypeInstancesCountQueries-"+predicate+"-"+dateStr+".tsv";
		try {
			FileWriter fw = new FileWriter(filename, true); //the true will append the new data

			for(String s : queries.getTypeInstancesCountQueries()){
				fw.write(s);
				fw.write("\n");
			}
			fw.close();
		}catch (Exception ex){
			System.out.println(ex);
		}
	}

	// run a query save in a file return the file name as a result
	private String doQuery(String query,String endpoint)  {
		//query = query.replace("  ","");
		//query = query.replace("\n"," ");
		//String endpoint = "https://synthg-fact.dice-research.org/sparql";

		try{
			String url = endpoint+ URLEncoder.encode(query, "UTF-8");
			System.out.println(url);
			HttpGet request = new HttpGet(url);
			//request.addHeader(HttpHeaders.ACCEPT, "application/sparql-results+xml");
			try (CloseableHttpResponse response = httpClient.execute(request)) {
				// Get HttpResponse Status
				System.out.println(response.getStatusLine().getStatusCode());
				if(response.getStatusLine().getStatusCode()!=200 ){
					return "";
				}
				HttpEntity entity = response.getEntity();
				System.out.println("the entity is ready");
				Header headers = entity.getContentType();
				if (entity != null) {
					// return it as a String
					String fileName = "tempQueryResults/"+UUID.randomUUID().toString()+".tmp";
					System.out.println("save the results at ");
					OutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
					entity.writeTo(out);
					out.close();
					return fileName;
					//return EntityUtils.toString(entity);
				}else{
					System.out.println("entity is null");
				}
			}

		}catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return "";
	}
}
