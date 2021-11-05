package org.dice.FactCheck.preprocess;

import net.sf.extjwnl.data.Exc;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

@SpringBootApplication
@ComponentScan("org.dice.FactCheck.preprocess.config")
public class PreprocessApplication implements CommandLineRunner {
    //http://127.0.0.1:9080/stream?query=SELECT%20%3Fp%20WHERE%20%7B%20%3Fs%20%3Fp%20%3Fo%20.%20%7D
	//https://dbpedia.org/sparql
	String dateStr;
	private String progressFileName;
	private boolean isIndividual = true;
	HashMap<Integer,String> progress = new HashMap<>();
	private final CloseableHttpClient httpClient = HttpClients.createDefault();

	public static void main(String[] args) {
		SpringApplication.run(PreprocessApplication.class, args);
	}

	@Override
	public void run(String... args) {
		if (args.length == 0){
			System.out.println("h : use this to get Help  'java -jar [jarfile] h '");
		}
		if (args.length == 1) {
			if(args[0].equals("h")){
				System.out.println("help");
				System.out.println("f [FileName] [directory for save results] [endpoint with ?stream= or sparql?query= part] [C for cumulative result or I for individual]: this will read file and run queries in that file");
			}
		}

		if(args.length == 5){
			if(args[0].equals("f")){
				System.out.println("looking at "+ args[1]+" for a file");
				/*try {
					String tempPath = new File(PreprocessApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getPath();
					System.out.println("working directory is "+ tempPath);
				}catch (Exception ex){
					System.out.println(ex);
				}*/

				File f = new File(args[1]);
				if (!f.exists()) {
					System.out.println("no file exist");
					return;
				}

				if (!f.isFile()) {
					System.out.println(args[1] + " is not a file");
					return;
				}

				f = new File(args[2]);
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



				//Progress File

				progressFileName = args[1]+".prg";
				File prf = new File(progressFileName);
				System.out.println("looking for progress file at "+ progressFileName);
				if(prf.exists()){
					System.out.println("progress file exists");
					try {
						FileInputStream fileIn = new FileInputStream(progressFileName);
						ObjectInputStream in = new ObjectInputStream(fileIn);
						progress = (HashMap) in.readObject();
						in.close();
						fileIn.close();
						System.out.println("progress file loaded it has "+progress.size()+" items");
					} catch (IOException i) {
						System.out.println("error reading progress file");
						i.printStackTrace();
					} catch (ClassNotFoundException c) {
						System.out.println("error reading progress file class");
						c.printStackTrace();
					}
				}else{
					System.out.println("progress file does not exist");
				}

				// how save the result
				if(args[4].equals("C")){
					isIndividual = false;
				}else{
					if(args[4].equals("I")){
						isIndividual = true;
					}else{
						System.out.println("The 4Th argument should be C or I");
					}
				}

				// set date str
				Date date = Calendar.getInstance().getTime();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss");
				dateStr = dateFormat.format(date);

				// read the query file
				try (BufferedReader br = new BufferedReader(new FileReader(args[1]))) {
					String line;
					Integer lineCounter = 1;
					while ((line = br.readLine()) != null) {
						// process the line.
						line = line.replace("(count(DISTINCT *) AS ?sum)"," DISTINCT ?s ?o ");
						if(!progress.containsKey(lineCounter))
						{
							// check fact
							String result = doQuery(line, args[3]);
							if(!result.equals("")) {
								//long resultNumber = countTheLines(result);
								save(line,result,args[2],isIndividual);
								System.out.println("running query was successful");
								progress.put(lineCounter, "successful");
							}else {
								System.out.println("running query was unsuccessful");
								progress.put(lineCounter, "unsuccessful");
							}
							updateProgress();
						}
						lineCounter = lineCounter + 1;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}
	public static void writeToFile(String str, String path, String query) throws Exception {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
				pw.print(str.replace("\n",""));
				pw.print("\t");
				pw.print(query);
				pw.println("");
			pw.flush();
		} finally {
			pw.close();
		}
	}

	// save each file or save all result in one file

	private void save(String query, String result, String path, Boolean individual)  {
		if(individual){
			String oldQuery = new String(query);
			query = query.replace(" ","").replace("\n","");
			String filePath = path+DigestUtils.md5Hex(query).toUpperCase()+".tsv";
			System.out.println("save result at "+filePath);
			try {
				writeToFile(result, filePath, oldQuery);
			}catch(Exception ex){
				System.out.println(ex);
			}
		}else{
			// write result in one File
			try
			{
				String filename= path+"cumulativeResult"+dateStr+".tsv";
				FileWriter fw = new FileWriter(filename,true); //the true will append the new data
				fw.write(result.replace("\n",""));
				fw.write("\t");
				fw.write(query);
				fw.write("\n");
				fw.close();
			}
			catch(IOException ioe)
			{
				System.err.println("IOException: " + ioe.getMessage());
			}

		}

	}

	private void updateProgress() {
		FileOutputStream fileOut = null;
		try {
			fileOut =
					new FileOutputStream(progressFileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(progress);
			out.close();
			fileOut.close();
			System.out.println("Serialized data is saved in"+ progressFileName+ " progress size is :"+progress.size());
		} catch (IOException i) {
			i.printStackTrace();
		}finally {
			try {
				if (fileOut != null) {
					fileOut.close();
				}
			}catch (Exception ex){
				ex.printStackTrace();
			}
		}

	}

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
				System.out.println(response.getStatusLine().toString());
				System.out.println(response.getStatusLine().getStatusCode());
				if(response.getStatusLine().getStatusCode()!=200 ){
					return "";
				}
				HttpEntity entity = response.getEntity();
				Header headers = entity.getContentType();
				System.out.println(headers);

				if (entity != null) {
					// return it as a String
					String result = EntityUtils.toString(entity);
					System.out.println(result);
					return result;
				}
			}

		}catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return "";
	}
}
