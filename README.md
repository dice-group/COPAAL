# COPAAL
# Paper
This repository contains code (service and demo) for the paper and demo titled "*Unsupervised Discovery of Corroborative Paths for FactValidation*" and "*COPAAL – An Interface for Explaining Facts usingCorroborative Paths*" respectively, accepted at **International Semantic Web Conference (ISWC-2019)**.

# Description
COPAAL is an unsupervised fact validation approach for RDF knowledge graphs which identifies paths that support a given fact *(s,p,o)*. This approach is based on the insight that the predicate *p* (e.g.,**nationality**) carries mutual information with a set of other paths (e.g., paths pertaining to **birthPlace** and **country**) in the background knowledge graph *G*. Hence,the presence of certain sets of paths in *G* that begin in *s* and end in *o* can be regarded as evidence which corroborates the veracity of *(s,p,o)*. For example, we would have good reasons to believe that **BarackObama** is a citizen of the **USA** given that **BarackObama** was born in **Hawaii** and **Hawaii** is located in the **USA** (see figure below).

![A subgraph of DBpedia version 10-2016.](https://github.com/dice-group/COPAAL/blob/master/service/src/main/resources/Running_Example_DBpedia.png)

# Usage Instructions
To run COPAAL, do the following:
- Clone the respository using the command `git clone --single-branch --branch COPAAL-AFIRM https://github.com/dice-group/COPAAL.git`
- Navigate to COPAAL folder (cd COPAAL) and run `mvn clean compile`

After the project is build successfully, you can run COPAAL from an IDE (IntelliJ or Eclipse)
- To run in an IDE, load the project in the IDE and run AFIRMLabDemo.java file. Configure the editor to pass the following command line arguments
1. -host XXX.XXX.XXX.XXX (IP address of the host running SPARQL service)
2. -port YYYY (port on the host where the SPARQL service is running)
3. -output /home/output/output.nt (path to output directory to generate output file). Note the out file extension should be `nt`

Example: -host 127.0.0.1 -port 8000 -output /home/results/result.nt

AFIRMLabDemo is configured to run the US_Vice-President dataset. When running, you should see the following message in your console

``Generating result file for US-Vice-President dataset....``

Note that this run may take 13-15 mins (the dataset has 274 facts). Once the program is finished, you will see further instructions on how to submit the results to GERBIL platform as follows

``Finished generating result file.
The result file will be generated at the location you specified.
For generating ROC-AUC score on our benchmarking platform (GERBIL) do as follows:
1. Go to http://swc2017.aksw.org/gerbil/config.
2. Select Task: Fact Checking.
3. Type the name of participating system and enter email address.
4. Submit the generated output file by clicking on Select file... button.
5. Select the Reference dataset: Synthetic US Vice President.
6. Check the Disclaimer. Optionally check publish to publish the results on our leaderboard.
7. Click on Run Experiment. After the experiment is finished click on the generated link to view experiment results.``
