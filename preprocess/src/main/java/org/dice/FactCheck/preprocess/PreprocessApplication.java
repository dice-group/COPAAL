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
import java.util.HashMap;
import java.util.Set;

@SpringBootApplication
@ComponentScan("org.dice.FactCheck.preprocess.config")
public class PreprocessApplication implements CommandLineRunner {
    //http://127.0.0.1:9080/stream?query=SELECT%20%3Fp%20WHERE%20%7B%20%3Fs%20%3Fp%20%3Fo%20.%20%7D
	//https://dbpedia.org/sparql
	private String ProgressFileName;
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
				System.out.println("f [FileName] [directory for save results] [endpoint with stream? or query? part]: this will read file and run queries in that file");
			}
		}

		if(args.length == 4){
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

				ProgressFileName = args[1]+".prg";
				File prf = new File(ProgressFileName);

				if(prf.exists()){
					try {
						FileInputStream fileIn = new FileInputStream(ProgressFileName);
						ObjectInputStream in = new ObjectInputStream(fileIn);
						progress = (HashMap) in.readObject();
						in.close();
						fileIn.close();
					} catch (IOException i) {
						i.printStackTrace();
					} catch (ClassNotFoundException c) {
						System.out.println("Employee class not found");
						c.printStackTrace();
					}
				}

				try (BufferedReader br = new BufferedReader(new FileReader(args[1]))) {
					String line;
					Integer lineCounter = 1;
					while ((line = br.readLine()) != null) {
						// process the line.
						if(!progress.containsKey(lineCounter))
						{
							// check fact
							String result = doQuery(line, args[3]);
							if(!result.equals("")) {
								save(line,result,args[2]);
								progress.put(lineCounter, "successful");
								updateProgress();
							}else {
								progress.put(lineCounter, "unsuccessful");
								updateProgress();
							}
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
	public static void writeToFile(String str, String path) throws Exception {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
				pw.println(str);
			pw.flush();
		} finally {
			pw.close();
		}
	}
	private void save(String line, String result, String path)  {
		line = line.replace(" ","");
		String filePath = path+DigestUtils.md5Hex(line).toUpperCase()+".txt";
		System.out.println("save result at "+filePath);
		try {
			writeToFile(result, filePath);
		}catch(Exception ex){
			System.out.println(ex);
		}

	}

	private void updateProgress() {
		FileOutputStream fileOut = null;
		try {
			fileOut =
					new FileOutputStream(ProgressFileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(progress);
			out.close();
			fileOut.close();
			System.out.println("Serialized data is saved in"+ ProgressFileName+ "progress size is :"+progress.size());
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
