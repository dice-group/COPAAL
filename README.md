# COPAAL
# Paper
This repository contains code (service and demo) for the paper and demo titled "*Unsupervised Discovery of Corroborative Paths for FactValidation*" and "*COPAAL – An Interface for Explaining Facts usingCorroborative Paths*" respectively, accepted at **International Semantic Web Conference (ISWC-2019)**.

# Description
COPAAL is an unsupervised fact validation approach for RDF knowledge graphs which identifies paths that support a given fact *(s,p,o)*. This approach is based on the insight that the predicate *p* (e.g.,**nationality**) carries mutual information with a set of other paths (e.g., paths pertaining to **birthPlace** and **country**) in the background knowledge graph *G*. Hence,the presence of certain sets of paths in *G* that begin in *s* and end in *o* can be regarded as evidence which corroborates the veracity of *(s,p,o)*. For example, we would have good reasons to believe that **BarackObama** is a citizen of the **USA** given that **BarackObama** was born in **Hawaii** and **Hawaii** is located in the **USA** (see figure below).

![A subgraph of DBpedia version 10-2016.](https://github.com/dice-group/COPAAL/blob/master/service/src/main/resources/Running_Example_DBpedia.png)

# Usage Instructions
COPAAL uses infomation from a given background knowledge graph *G* hosted on a sparql end-point. Currently, COPAAL supports knowledge graphs that contains ontology information. An example of a KG that defines type information is DBpedia. In our experiments, we used DBpedia and the usage instructions are for the same.

- Prepare a sparql end-point of your choice and index the following dumps of DBpedia version 10-2016: ``ontology``,``instance types``,``mapping-based objects`` and ``infobox properties``.
- Clone the repository from https://github.com/dice-group/COPAAL.
The repository contains two components namely the service and demo (ui-service).
# Service
To use the service do the following:
- Update the URL to map to your sparql end-point (from previous step) in https://github.com/dice-group/COPAAL/blob/master/service/src/main/resources/application.properties
- COPAAL is developed using Spring Boot Maven.
- Build the project by navigating to service folder and issuing the the command `mvn clean install`
COPAAL service can be deployed
- using IDE
- packaged application
- using maven plugin

# Running from an IDE
You can run COPAAL service from your IDE as a simple Java application. To do this simply import the maven project in your IDE of choice and run https://github.com/dice-group/COPAAL/blob/master/service/src/main/java/org/dice/FactCheck/Application.java.

# Running as a Packaged Application
To run COPAAL as packaged application simply issue the command ``java -jar target/Corraborative-0.0.1-SNAPSHOT.jar``

# Using the Maven Plugin
