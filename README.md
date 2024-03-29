# COPAAL
[![Maven Build](https://github.com/dice-group/COPAAL/actions/workflows/maven.yml/badge.svg)](https://github.com/dice-group/COPAAL/actions/workflows/maven.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/dd7417fb414a4c43944b3dd067566548)](https://www.codacy.com/gh/dice-group/COPAAL/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dice-group/COPAAL&amp;utm_campaign=Badge_Grade)

## Paper
This repository contains code (service and demo) for the paper and demo titled "*Unsupervised Discovery of Corroborative Paths for FactValidation*" and "*COPAAL – An Interface for Explaining Facts usingCorroborative Paths*" respectively, accepted at **International Semantic Web Conference (ISWC-2019)**.

## Description
COPAAL is an unsupervised fact validation approach for RDF knowledge graphs which identifies paths that support a given fact *(s,p,o)*. This approach is based on the insight that the predicate *p* (e.g.,**nationality**) carries mutual information with a set of other paths (e.g., paths pertaining to **birthPlace** and **country**) in the background knowledge graph *G*. Hence,the presence of certain sets of paths in *G* that begin in *s* and end in *o* can be regarded as evidence which corroborates the veracity of *(s,p,o)*. For example, we would have good reasons to believe that **BarackObama** is a citizen of the **USA** given that **BarackObama** was born in **Hawaii** and **Hawaii** is located in the **USA** (see figure below).

![A subgraph of DBpedia version 10-2016.](https://github.com/dice-group/COPAAL/blob/master/service/src/main/resources/Running_Example_DBpedia.png)

## Usage Instructions
COPAAL uses infomation from a given background knowledge graph *G* hosted on a sparql end-point. Currently, COPAAL supports knowledge graphs that contains ontology information. An example of a KG that defines type information is DBpedia. In our experiments, we used DBpedia and the usage instructions are for the same.

- Prepare a sparql end-point of your choice and index the following dumps of DBpedia version 10-2016: ``ontology``,``instance types``,``mapping-based objects`` and ``infobox properties``.
- Clone the repository from https://github.com/dice-group/COPAAL.
The repository contains two components namely the service and demo (ui-service).
### Service
To use the service do the following:
- Update the URL to map to your sparql end-point (from previous step) in https://github.com/dice-group/COPAAL/blob/master/service/src/main/resources/application.properties
- COPAAL is developed using Spring Boot Maven.
- Build the project by navigating to service folder and issuing the the command `mvn clean install`.

#### COPAAL service can be deployed
1. using IDE
2. packaged application
3. using maven plugin

### Running from an IDE
You can run COPAAL service from your IDE as a simple Java application. To do this simply import the maven project in your IDE of choice and run https://github.com/dice-group/COPAAL/blob/master/service/src/main/java/org/dice/FactCheck/Application.java.

### Running as a Packaged Application
To run COPAAL as packaged application, open a terminal and navigate to the ``target`` sub-directory inside service folder. Issue the command ``java -jar Corraborative-0.0.1-SNAPSHOT.jar``

### Using the Maven Plugin
The Spring Boot Maven plugin includes a ``run`` goal that can be used to quickly compile and run applications. To run COPAAL service using this option, navigate to the service folder and issue the command `mvn spring-boot:run`.

Using one or more options above, COPAAL service will be deployed. The running service can be verified by issuing the following example GET request from a browser

http://localhost:8080/validate?subject=http://dbpedia.org/resource/Barack_Obama&object=http://dbpedia.org/resource/United_States&property=http://dbpedia.org/ontology/nationality&pathlength=2

## Demo (UI-Service)

In addition to the COPAAL service, one can deploy a UI-service in order to query and validate facts using a web interface. The demo was developed using [Angular CLI](https://github.com/angular/angular-cli) version 7.0.3.


### Build

To build the demo navigate to the ui-service folder and run `ng build`. The build artifacts will be stored in the `dist/` directory.

### Deploy

Run `ng serve` to deploy the demo. To use the demo navigate to `http://localhost:4200/`. You should see the web-page as shown in the figure below.

![Demo web page.](https://github.com/dice-group/COPAAL/blob/master/service/src/main/resources/Demo.png)
