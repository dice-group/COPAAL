package org.dice.factcheck.preprocess;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.dice.factcheck.preprocess.model.CountQueries;
import org.dice.factcheck.preprocess.model.Path;
import org.dice.factcheck.preprocess.service.CounterQueryGeneratorService;
import org.dice.factcheck.preprocess.service.PathService;
import org.dice.factcheck.preprocess.service.PredicateService;
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

/*
 *   this class runs the program as a command line program
 * */
@SpringBootApplication
@ComponentScan("org.dice.factcheck.preprocess.config")
public class PreprocessApplication implements CommandLineRunner {
    //http://127.0.0.1:9080/stream?query=SELECT%20%3Fp%20WHERE%20%7B%20%3Fs%20%3Fp%20%3Fo%20.%20%7D
	//https://dbpedia.org/sparql

	// this variable will save the String formated date to use in file names while saving on disk
	String dateStr;

	// flags which come from cmd arguments
	private boolean isIndividual = true;
	private boolean isLiteVersion = true;
	private boolean isCompleteVersion = true;
	// show the input was a single file or a folder , for folder all the files in the folder will process
	private boolean isFolder = false;

	private final CloseableHttpClient httpClient = HttpClients.createDefault();

	public static void main(String[] args) {
		SpringApplication.run(PreprocessApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// set date string
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("MM-dd_hh-mm");
		dateStr = dateFormat.format(date);

		if (args.length == 0){
			System.out.println("h : use this to get Help  'java -jar [jarfile] h '");
			return;
		}
		if (args.length == 1 && args[0].equals("h")) {
			System.out.println("help");
			System.out.println("f [FileName] [directory for save results] [endpoint with ?stream= or sparql?query= part] ['C' for cumulative result(both Lite and Complete version), 'CL' just lite version, 'CC' just Complete version , 'I' for individual] [folder for save temp files] [number of line to start] [optional : file to check if the same path in this file has result then run query for the path in provided file , just useable for file not for folder]: this will read file and run queries in that file");
			System.out.println("pc [collected_predicates.json] [len] [pathToSaveResult] [PathToSaveSerialization] [true or false for save the result]: this will read file and  generate all combination for predicates by length [len]  ");
			System.out.println("gq [predicate] [domain] [range] [predicate combination files] [pathToSaveResults] [collected_predicates.json]: this will read the predicate path and generate all queries]  ");
		}

		terminalWrite("first arg is :"+args[0]+" and size is "+ args.length);

		if(args[0].equals("f")){

			/* this will read file and run queries in that file
			* args[1][FileName]
			* args[2][directory for save results]
			* args[3][endpoint with ?stream= or sparql?query= part]
			* args[4]['C' for cumulative result(both Lite and Complete version), 'CL' just lite version, 'CC' just Complete version , 'I' for individual]
			* args[5][folder for save temp files]
			* args[6][number of line to start]
			* args[7][optional : file to check if the same path in this file has result then run query for the path in provided file , just useable for file not for folder]
			* */


			if(args.length == 7 || args.length == 8){
				System.out.println("looking at "+ args[1]+" for a file");

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

				//TODO remove arg 5
				//String tempQueryFolderAddress = args[5];
				//terminalWrite("temp folder is :"+tempQueryFolderAddress);

				//TODO remove arg 6
				//long fromThisLineStartToProcess = Long.parseLong(args[6]);

				if(args.length == 8){
					if(isFolder){
						terminalWrite("you provide the file to check each path to run or not (8th arguments) in this situation the input could not be a folder , it must be a file");
					}else{
						// is file
						processTheFile(args[1], args[2], inputFileOrFolder.getName(), args[3], args[7]);
					}
				}else{
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
			}else {
				terminalWrite("for f the arguments are not correct");
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
			if(args.length == 7){
				System.out.println("Start generating the queries");
				CounterQueryGeneratorService service = new CounterQueryGeneratorService(new PathService());

				// read all predicates
				PredicateService predicateService = new PredicateService(null);

				HashSet<Predicate> predicates = (HashSet<Predicate>) predicateService.allPredicates(args[6]);

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

				// read a file of all combinations (it is text file ) then with use the predicateMap
				// for each combinations make a path which each part of it is a predicate with domain and range
				Collection<Path> paths = readPredicateCombinationFromFile(args[4], predicatesMap);

				System.out.println("paths are loaded "+paths.size());

				CountQueries queries = service.generateCountQueries(predicate,paths);

				try {
					System.out.println("queries are loaded "+queries.whatIsTheSize());
				}catch (Exception ex){
					System.out.println(ex);
				}
				System.out.println("start save the results ");
				saveTheCountQueriesInFile(queries, args[5],predicate.getProperty().getLocalName());
				System.out.println(" results saved");
			}
		}
	}

	private Collection<Path> readPredicateCombinationFromFile(String filePath, Map<String,Predicate> predicatesMap) throws Exception {
		Set<Path> returnSet = new HashSet<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
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
				path.addPart(makePredicateFromTextGerDomainAndRangeFromFile(parts[i], predicatesMap),false);
			}else{
				path.addPart(makePredicateFromTextGerDomainAndRangeFromFile(parts[i], predicatesMap),true);
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

	private Set<String> selectThePathsWithScoreMoreThanZeroFromFile(String pathOfSourceFileForCheckShouldRunQueryOrNot) {
		Set<String> returnSet = new HashSet<>();

		try (BufferedReader br = new BufferedReader(new FileReader(pathOfSourceFileForCheckShouldRunQueryOrNot))) {
			String line;
			while ((line = br.readLine()) != null) {
				//SELECT DISTINCT ?s ?o WHERE { ?s <http://dbpedia.org/ontology/birthPlace> ?o .  ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Animal> .  ?o <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Place> . ?s <http://dbpedia.org/ontology/birthPlace> ?in1 . ?in1 <http://dbpedia.org/ontology/landeshauptmann> ?in2 . ?in2 <http://dbpedia.org/ontology/deathPlace> ?o . },0,<http://dbpedia.org/ontology/birthPlace><http://dbpedia.org/ontology/landeshauptmann><http://dbpedia.org/ontology/deathPlace>,http://dbpedia.org/ontology/birthPlace
				String[] parts = line.split(",");
				if(parts.length!=4){
					terminalWrite("Error the parts are not 4 "+ line);
				}
				if(Long.parseLong(parts[1])>0) {
					returnSet.add(parts[2]);
				}
			}
			System.out.println("Done for this File");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		terminalWrite("we have "+returnSet.size()+" paths to check in this file");
		return returnSet;
	}

	private void processTheFile(String filePath, String pathForSaveResults, String fileName, String endpoint) {
		// read the query file
		System.out.println("Start Process the File"+  filePath);
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String query;
			String queryAndPath;
			long lineCounter = 0L;
			while ((queryAndPath = br.readLine()) != null) {
				//System.out.println(filePath+" "+lineCounter);

				lineCounter = lineCounter + 1;

				if(!diskSpaceIsEnough()){
					terminalWrite("this line note processed " + lineCounter);
					break;
				}

				String[] parts = queryAndPath.split(",");
				//terminalWrite("parts Size is : "+parts.length);
				if(parts.length!=3){
					terminalWrite("Error the parts are not 3 "+ queryAndPath);
				}
				query = parts[0];

				// process the line. because TENTRIS can not process this query yet
				query = query.replace("(count(DISTINCT *) AS ?sum)"," DISTINCT ?s ?o ");
				runTheQuery(query, endpoint, pathForSaveResults, fileName, parts[1], parts[2]);
				lineCounter = lineCounter + 1;
				//System.out.println("read next line");
			}
			System.out.println("Done for this File");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processTheFile(String filePath, String pathForSaveResults, String fileName, String endpoint,  String pathOfSourceFileForCheckShouldRunQueryOrNot) {
		// read the query file
		terminalWrite("Start Process the File "+  filePath+" and reference file :"+pathOfSourceFileForCheckShouldRunQueryOrNot);
		Set<String> validPaths = new HashSet<>();
		validPaths = selectThePathsWithScoreMoreThanZeroFromFile(pathOfSourceFileForCheckShouldRunQueryOrNot);

		terminalWrite("we have  "+  validPaths.size()+" valid paths");
		long numberOfNotFinds = 0;
		long numberOfFinds = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String query;
			String queryAndPath;
			Long lineCounter = 0L;
			while ((queryAndPath = br.readLine()) != null) {
				//System.out.println(filePath+" "+lineCounter);

				lineCounter = lineCounter + 1;

				if(!diskSpaceIsEnough()){
					terminalWrite("this line note processed " + lineCounter);
					break;
				}

				String[] parts = queryAndPath.split(",");
				//terminalWrite("parts Size is : "+parts.length);
				if(parts.length!=3){
					terminalWrite("Error the parts are not 3 "+ queryAndPath);
				}
				query = parts[0];

				if(validPaths.contains(parts[1])) {
					// process the line.
					query = query.replace("(count(DISTINCT *) AS ?sum)"," DISTINCT ?s ?o ");
					runTheQuery(query, endpoint, pathForSaveResults, fileName, parts[1], parts[2]);
					numberOfFinds = numberOfFinds + 1;
				}else{
					//terminalWrite("can not find this path in source file :"+ parts[1]);
					numberOfNotFinds = numberOfNotFinds+1;
				}
				lineCounter = lineCounter + 1;
				if(lineCounter % 500 == 0){
					terminalWrite(lineCounter.toString());
					terminalWrite("free space is :"+new File("/").getFreeSpace());
				}
				//System.out.println("read next line");
			}
			System.out.println("Done for this File" + " LINE COUNTER :"+ lineCounter+ " numberOfFinds "+numberOfFinds +"numberOf not Finds:"+numberOfNotFinds+" map of valid paths :"+validPaths.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean diskSpaceIsEnough() {
		try {
			Long freeSpace = new File("/").getFreeSpace();
			if(freeSpace > 5368709120L){
				return true;
			}
			return false;
		}catch (Exception ex){
			terminalWrite(ex.getMessage());
			return false;
		}
	}


	private void runTheQuery(String query, String endpoint, String pathForSaveResults, String fileName, String pathOfQuery , String predicate) {
		// check fact
		//System.out.println("start do the query");
		long resultNumber = doQuery(query, endpoint);
		System.out.println(resultNumber);
		save(query, resultNumber, pathForSaveResults, isIndividual, isLiteVersion, isCompleteVersion, fileName , pathOfQuery,predicate);
	}

	public static void writeToFile(String path, String query, long CountResult, String pathOfQuery , String predicate) throws Exception {

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
				pw.print(query);
				pw.write(",");
				pw.write(String.valueOf(CountResult));
				pw.write(",");
				pw.write(pathOfQuery);
				pw.write(",");
				pw.write(predicate);
				pw.println("");
			pw.flush();
		} finally {
			pw.close();
		}
	}

	// save each file or save all result in one file

	private void save(String query,long CountResult ,String pathForSaveFile, Boolean individual, Boolean isLiteVersion , Boolean isCompleteVersion, String queriesFileName, String pathOfQuery , String predicate)  {
		if(individual){
			//System.out.println("it is individual");
			String oldQuery = new String(query);
			query = query.replace(" ","").replace("\n","");
			String filePath = pathForSaveFile+DigestUtils.md5Hex(query).toUpperCase()+".csv";
			//System.out.println("save result at "+filePath);
			try {
				writeToFile(filePath, oldQuery, CountResult,pathOfQuery,predicate);
			}catch(Exception ex){
				System.out.println(ex);
			}
		}else{
			// write result in one File
			if(isCompleteVersion){
				try
				{
					String filename= pathForSaveFile+queriesFileName+"CumulativeResult-"+dateStr+".csv";
					FileWriter fw = new FileWriter(filename,true); //the true will append the new data
					fw.write(query);
					fw.write(",");
					fw.write(String.valueOf(CountResult));
					fw.write(",");
					fw.write(pathOfQuery);
					fw.write(",");
					fw.write(predicate);
					fw.write("\n");
					fw.close();
				}
				catch(IOException ioe)
				{
					System.err.println("IOException: " + ioe.getMessage());
				}
			}

			if(isLiteVersion){
//				terminalWrite("lite version");
				try
				{
					String filename= pathForSaveFile+queriesFileName+"LiteCumulativeResult-"+dateStr+".csv";
					//terminalWrite(("file pass is " + filename);
					FileWriter fw = new FileWriter(filename,true); //the true will append the new data
					fw.write(query);
					fw.write(",");
					fw.write(String.valueOf(CountResult));
					fw.write(",");
					fw.write(pathOfQuery);
					fw.write(",");
					fw.write(predicate);
					fw.write("\n");
					fw.close();
				}
				catch(IOException ioe)
				{
					terminalWrite("IOException: " + ioe.getMessage());
				}
			}
		}

	}

	public void terminalWrite(String str){
		System.out.println(str);
	}

	private void saveTheCountQueriesInFile(CountQueries queries, String pathTosave,String predicate) {

		//terminalWrite("start save the results in files");

		String filename= pathTosave+"CoOccurrenceCountQueries-"+predicate+"-"+dateStr+".csv";
		terminalWrite("first path is :"+filename);
		try {
			FileWriter fw = new FileWriter(filename, true); //the true will append the new data
			terminalWrite("will write  :"+queries.getCoOccurrenceCountQueries().size()+" lines");
			for(String s : queries.getCoOccurrenceCountQueries()){
				fw.write(s);
				fw.write("\n");
			}
			fw.close();
		}catch (Exception ex){
			System.out.println(ex);
		}
		terminalWrite("Write "+filename+" is done ");

		filename= pathTosave+"PathInstancesCountQueries-"+predicate+"-"+dateStr+".csv";
		terminalWrite("first path is :"+filename);
		try {
			FileWriter fw = new FileWriter(filename, true); //the true will append the new data

			terminalWrite("will write  :"+queries.getPathInstancesCountQueries().size()+" lines");
			for(String s : queries.getPathInstancesCountQueries()){
				fw.write(s);
				fw.write("\n");
			}
			fw.close();
		}catch (Exception ex){
			System.out.println(ex);
		}
		terminalWrite("Write "+filename+" is done ");

		filename= pathTosave+"MaxCountQueries-"+predicate+"-"+dateStr+".csv";
		terminalWrite("first path is :"+filename);
		try {
			FileWriter fw = new FileWriter(filename, true); //the true will append the new data

			terminalWrite("will write  :"+queries.getMaxCountQueries().size()+" lines");
			for(String s : queries.getMaxCountQueries()){
				fw.write(s);
				fw.write("\n");
			}
			fw.close();
		}catch (Exception ex){
			System.out.println(ex);
		}
		terminalWrite("Write "+filename+" is done ");

		filename= pathTosave+"PredicateInstancesCountQueries-"+predicate+"-"+dateStr+".csv";
		terminalWrite("first path is :"+filename);
		try {
			FileWriter fw = new FileWriter(filename, true); //the true will append the new data

			terminalWrite("will write  :"+queries.getPredicateInstancesCountQueries().size()+" lines");
			for(String s : queries.getPredicateInstancesCountQueries()){
				fw.write(s);
				fw.write("\n");
			}
			fw.close();
		}catch (Exception ex){
			System.out.println(ex);
		}
		terminalWrite("Write "+filename+" is done ");

		filename= pathTosave+"TypeInstancesCountQueries-"+predicate+"-"+dateStr+".csv";
		terminalWrite("first path is :"+filename);
		try {
			FileWriter fw = new FileWriter(filename, true); //the true will append the new data

			terminalWrite("will write  :"+queries.getTypeInstancesCountQueries().size()+" lines");
			for(String s : queries.getTypeInstancesCountQueries()){
				fw.write(s);
				fw.write("\n");
			}
			fw.close();
		}catch (Exception ex){
			System.out.println(ex);
		}
		terminalWrite("Write "+filename+" is done ");
	}

	// run a query save in a file return the file name as a result
	private long doQuery(String query,String endpoint)  {
		//query = query.replace("  ","");
		//query = query.replace("\n"," ");
		//String endpoint = "https://synthg-fact.dice-research.org/sparql";

		try{
			String url = endpoint+ URLEncoder.encode(query, "UTF-8");
			System.out.print(url);
			System.out.print(":");
			HttpGet request = new HttpGet(url);
			//request.addHeader(HttpHeaders.ACCEPT, "application/sparql-results+xml");
			try (CloseableHttpResponse response = httpClient.execute(request)) {
				// Get HttpResponse Status
				//System.out.println(response.getStatusLine().getStatusCode());
				if(response.getStatusLine().getStatusCode()!=200 ){
					terminalWrite("response is not 200" + query);
					return 0;
				}
				HttpEntity entity = response.getEntity();
				//System.out.println("the entity is ready");
				System.out.print(entity);
				if (entity != null) {
					return Long.parseLong(EntityUtils.toString(entity));
				}else{
					System.out.println("entity is null");
				}
			}
			System.out.println(" ");
		}catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return 0;
	}
}
