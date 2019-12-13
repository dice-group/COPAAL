package org.dice.FactCheck.Corraborative;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice.FactCheck.Corraborative.Query.SparqlQueryGenerator;
import org.dice.FactCheck.Corraborative.UIResult.CorroborativeGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Component
public class FactChecking {

    public org.dice.FactCheck.Corraborative.Config.Config Config;

    private SparqlQueryGenerator sparqlQueryGenerator;
    private QueryExecutioner queryExecutioner;
    private CorroborativeGraph corroborativeGraph;
    @Value("${info.service.url}")
    private String serviceURL;



    @Autowired
    public FactChecking(SparqlQueryGenerator sparqlQueryGenerator, QueryExecutioner queryExecutioner, CorroborativeGraph corroborativeGraph){
        this.sparqlQueryGenerator = sparqlQueryGenerator;
        this.queryExecutioner = queryExecutioner;
        this.corroborativeGraph = corroborativeGraph;
    }

    public Model checkFacts(Model model, Boolean verbalize, int pathLength) throws ParseException {

        Model outputModel = ModelFactory.createDefaultModel();
        final Logger LOGGER = LoggerFactory.getLogger(FactChecking.class);

        StmtIterator iterator = model.listStatements(null, RDF.type, RDF.Statement);

        // Needed to create gerbil results
        Property truthProp = ResourceFactory.createProperty("http://swc2017.aksw.org/hasTruthValue");

        while(iterator.hasNext()){
            Statement statement = iterator.nextStatement();
            Resource statementID = statement.getSubject();
            Resource subject = model.listStatements(statementID, RDF.subject, (RDFNode)null).nextStatement().getObject().asResource();
            Resource object = model.listStatements(statementID, RDF.object, (RDFNode)null).nextStatement().getObject().asResource();
            Property property = ResourceFactory.createProperty(model.listStatements(statementID, RDF.predicate,
                    (RDFNode)null).nextStatement().getObject().toString());
            Statement inputTriple = ResourceFactory.createStatement(subject, property, object);

            int count_predicate_Triples = countPredicateOccurances(NodeFactory.createVariable("s"), property,
                    NodeFactory.createVariable("o"));

            //get Domain and Range info

            Set<Node> subjectTypes = getTypeInformation(property, RDFS.domain);
            Set<Node> objectTypes = getTypeInformation(property, RDFS.range);

            //Check if the domain information is missing. If yes, then fallback to types of subject
            if (subjectTypes.isEmpty()) {
                subjectTypes = getTypeInformation(subject.asResource(), RDF.type);
            }

            //Check if the range information is missing. If yes, then fallback to types of object
            if (objectTypes.isEmpty()) {
                objectTypes = getTypeInformation(object.asResource(), RDF.type);
            }

            // if no type information is available for subject or object, simply return score 0. We cannot verify fact.
            if (subjectTypes.isEmpty() || objectTypes.isEmpty()) {
                outputModel.addLiteral(statementID.asResource(), truthProp, 0.0);
                continue;
            }

            // Some common calculations for all paths
            int count_subject_Triples = countOccurrances(NodeFactory.createVariable("s"), RDF.type, subjectTypes);
            int count_object_Triples = countOccurrances(NodeFactory.createVariable("s"), RDF.type, objectTypes);

            LOGGER.info("Checking Fact");

            //because we are combining score in a probablistic equation we have to initialize to 1
            double score = 1.0;

            for (int j = 1; j <= pathLength; j++) {
                try {
                    sparqlQueryGenerator.generatorSparqlQueries(inputTriple, j);
                } catch (ParseException e) {
                    LOGGER.info("Exception while generating Sparql queries.");
                }
            }
            Set<PathGenerator> pathGenerators = new HashSet<PathGenerator>();
            Set<PathQuery> pathQueries = new HashSet<PathQuery>();

            for (Entry<String, Integer> entry : sparqlQueryGenerator.sparqlQueries.entrySet()) {
                PathGenerator pg = new PathGenerator(entry.getKey(), inputTriple, entry.getValue(), queryExecutioner);
                pathGenerators.add(pg);
            }

            for (PathGenerator pathGenerator : pathGenerators) {
                pathQueries.add(pathGenerator.returnQuery());
            }

            Set<PMICalculator> pmiCallables = new HashSet<PMICalculator>();
            Set<Result> results = new HashSet<Result>();
            for (PathQuery pathQuery : pathQueries) {
                for (Entry<String, java.util.HashMap<String, Integer>> entry : pathQuery.getPathBuilder().entrySet()) {
                    for (Entry<String, Integer> path : entry.getValue().entrySet()) {
                        String querySequence = entry.getKey();
                        String pathString = path.getKey();
                        String intermediateNodes = pathQuery.getIntermediateNodes().get(pathString);
                        PMICalculator pc = new PMICalculator(pathString, querySequence, inputTriple, intermediateNodes, path.getValue(),
                                count_predicate_Triples, count_subject_Triples, count_object_Triples, subjectTypes, objectTypes, queryExecutioner);
                        pmiCallables.add(pc);
                    }
                }
            }

            // for experiments, use run in parallel
            try {
                ExecutorService executor = Executors.newFixedThreadPool(100);

                for (Future<Result> result : executor.invokeAll(pmiCallables)) {
                    results.add(result.get());
                }

                executor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }

            for(Result result : results){
                score = score * (1 - result.getScore());
            }
            outputModel.addLiteral(statementID.asResource(), truthProp, (1-score));
        }
        return outputModel;
    }


    public int countOccurrances(Node subject, Property property, Set<Node> objectTypes) {
        SelectBuilder occurrenceBuilder = new SelectBuilder();
        Iterator<Node> typeIterator = objectTypes.iterator();
        try {
            occurrenceBuilder.addVar("count(*)", "?c");
            while (typeIterator.hasNext())
                occurrenceBuilder.addWhere(subject, property, typeIterator.next());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return returnCount(occurrenceBuilder);
    }

    public int countPredicateOccurances(Node subject, Property property, Node objectType) {
        SelectBuilder occurrenceBuilder = new SelectBuilder();
        try {
            occurrenceBuilder.addVar("count(*)", "?c");
            occurrenceBuilder.addWhere(subject, property, objectType);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return returnCount(occurrenceBuilder);
    }

    public Set<Node> getTypeInformation(Resource subject, Property property) {
        Set<Node> types = new HashSet<Node>();
        SelectBuilder typeBuilder = new SelectBuilder().addPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        typeBuilder.addPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        typeBuilder.addWhere(subject, property, NodeFactory.createVariable("x"));

        Query typeQuery = typeBuilder.build();
        QueryExecution queryExecution = queryExecutioner.getQueryExecution(typeQuery);

        ResultSet resultSet = queryExecution.execSelect();
        while (resultSet.hasNext())
            types.add(resultSet.next().get("x").asNode());
        queryExecution.close();
        return types;
    }


    public int returnCount(SelectBuilder builder){
        Query queryOccurrence = builder.build();
        QueryExecution queryExecution = queryExecutioner.getQueryExecution(queryOccurrence);
        int count_Occurrence = 0;
        ResultSet resultSet = queryExecution.execSelect();
        if (resultSet.hasNext())
            count_Occurrence = resultSet.next().get("?c").asLiteral().getInt();
        queryExecution.close();
        return count_Occurrence;
    }
}
