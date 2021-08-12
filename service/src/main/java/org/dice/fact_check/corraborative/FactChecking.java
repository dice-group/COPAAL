package org.dice.fact_check.corraborative;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice.fact_check.corraborative.filter.npmi.LowCountBasedNPMIFilter;
import org.dice.fact_check.corraborative.filter.npmi.NPMIFilter;
import org.dice.fact_check.corraborative.path_generator.IPathGenerator;
import org.dice.fact_check.corraborative.path_generator.IPathGeneratorFactory;
import org.dice.fact_check.corraborative.path_generator.IPathGeneratorFactory.PathGeneratorType;
import org.dice.fact_check.corraborative.query.QueryExecutioner;
import org.dice.fact_check.corraborative.query.SparqlQueryGenerator;
import org.dice.fact_check.corraborative.sum.FixedSummarist;
import org.dice.fact_check.corraborative.sum.ScoreSummarist;
import org.dice.fact_check.corraborative.ui_result.CorroborativeGraph;
import org.dice.fact_check.corraborative.ui_result.CorroborativeTriple;
import org.dice.fact_check.corraborative.ui_result.Path;
import org.dice.fact_check.corraborative.ui_result.create.IPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FactChecking {

  private static final Logger LOGGER = LoggerFactory.getLogger(FactChecking.class);

  private SparqlQueryGenerator sparqlQueryGenerator;
  private QueryExecutioner queryExecutioner;
  private CorroborativeGraph corroborativeGraph;

  private IPathFactory defaultPathFactory;
  private IPathGeneratorFactory pathGeneratorFactory; // = new DefaultPathGeneratorFactory();
  private int maxThreads = 100;
  private NPMIFilter filter = null;

  protected ScoreSummarist summarist = new FixedSummarist();

  /**
   * The namespace we want the paths to follow
   */
  private String ontologyURI;
  
  @Autowired
  public FactChecking(SparqlQueryGenerator sparqlQueryGenerator, QueryExecutioner queryExecutioner,
      CorroborativeGraph corroborativeGraph, IPathFactory defaultPathFactory,
      IPathGeneratorFactory pathGeneratorFactory, String ontologyURI) {
    this.sparqlQueryGenerator = sparqlQueryGenerator;
    this.queryExecutioner = queryExecutioner;
    this.corroborativeGraph = corroborativeGraph;
    this.defaultPathFactory = defaultPathFactory;
    this.pathGeneratorFactory = pathGeneratorFactory;
    this.filter = new LowCountBasedNPMIFilter(new int[] {1, 1, 3});
    this.ontologyURI = ontologyURI;
  }

  public void setPathGeneratorFactory(IPathGeneratorFactory pathGeneratorFactory) {
    this.pathGeneratorFactory = pathGeneratorFactory;
  }

  public CorroborativeGraph checkFacts(Model model, int pathLength, boolean vTy,
      PathGeneratorType pathGeneratorType, boolean verbalize)
      throws InterruptedException, FileNotFoundException, ParseException {
    StmtIterator iterator = model.listStatements();
    Statement inputTriple = iterator.next();

    return checkFacts(inputTriple, pathLength, vTy, pathGeneratorType, verbalize);
  }

  public CorroborativeGraph checkFacts(Statement inputTriple, int pathLength, boolean vTy,
      PathGeneratorType pathGeneratorType, boolean verbalize)
      throws InterruptedException, FileNotFoundException, ParseException {
    // Initialization
    long startTime = System.nanoTime();
    long stepTime = System.nanoTime();

    Resource subject = inputTriple.getSubject();
    Resource object = inputTriple.getObject().asResource();
    Property property = inputTriple.getPredicate();
    corroborativeGraph.setInputTriple(
        new CorroborativeTriple(subject.toString(), property.toString(), object.toString()));

    int count_predicate_Triples = countPredicateOccurrances(NodeFactory.createVariable("s"),
        property, NodeFactory.createVariable("o"));

    // get Domain and Range info
    Set<Node> subjectTypes = null, objectTypes = null;
    if (!vTy) {
      subjectTypes = getTypeInformation(property, RDFS.domain);
      objectTypes = getTypeInformation(property, RDFS.range);

      // Check if the domain information is missing. If yes, then fallback to types of
      // subject
      if (subjectTypes.isEmpty()) {
        subjectTypes = getTypeInformation(subject.asResource(), RDF.type);
      }

      // Check if the range information is missing. If yes, then fallback to types of
      // object
      if (objectTypes.isEmpty()) {
        objectTypes = getTypeInformation(object.asResource(), RDF.type);
      }

      // if no type information is available for subject or object, simply return
      // score 0. We cannot verify fact.
      if (subjectTypes.isEmpty() || objectTypes.isEmpty()) {
        corroborativeGraph.setPathList(new ArrayList<Path>());
        corroborativeGraph.setGraphScore(0.0);
        LOGGER.info("subjectTypes is Empty or object Types is Empty");
        return corroborativeGraph;
      }
    }

    int count_subject_Triples, count_object_Triples;
    if (!vTy) {
      count_subject_Triples =
          countOccurrances(NodeFactory.createVariable("s"), RDF.type, subjectTypes);
      count_object_Triples =
          countOccurrances(NodeFactory.createVariable("s"), RDF.type, objectTypes);
    } else {
      count_subject_Triples = countSOOccurrances("count(distinct ?s)", property);
      count_object_Triples = countSOOccurrances("count(distinct ?o)", property);
    }

    stepTime = logElapsedTimeThisStep("initiate", stepTime);

    // Path Discovery
    LOGGER.info("Checking Fact");
    LOGGER.info("count_subject_Triples: " + count_subject_Triples + " count_object_Triples: "
        + count_object_Triples);

    for (int j = 1; j <= pathLength; j++) {
      try {
        sparqlQueryGenerator.generatorSparqlQueries(inputTriple, j);
      } catch (ParseException e) {
        LOGGER.error("Exception while generating Sparql queries." + e.getMessage());
      }
    }

    Set<IPathGenerator> pathGenerators = new HashSet<IPathGenerator>();
    Set<PathQuery> pathQueries = new HashSet<PathQuery>();

    for (Entry<String, Integer> entry : sparqlQueryGenerator.sparqlQueries.entrySet()) {

      IPathGenerator pg = pathGeneratorFactory.build(entry.getKey(), inputTriple, entry.getValue(),
          queryExecutioner, pathGeneratorType, ontologyURI);
      pathGenerators.add(pg);
    }

    try {
      ExecutorService executor = Executors.newFixedThreadPool(maxThreads);

      for (Future<PathQuery> result : executor.invokeAll(pathGenerators)) {
        if (result.get() != null) {
          pathQueries.add(result.get());
        }
      }

      executor.shutdown();
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }

    stepTime = logElapsedTimeThisStep("path discovery", stepTime);
    LOGGER.info("there are " + pathQueries.size() + " path queries generate");
    // Path scoring
    Set<NPMICalculator> pmiCallables = new HashSet<NPMICalculator>();
    Set<Result> results = new HashSet<Result>();

    for (PathQuery pathQuery : pathQueries) {
      for (Entry<String, java.util.HashMap<String, Integer>> entry : pathQuery.getPathBuilder()
          .entrySet()) {
        for (Entry<String, Integer> path : entry.getValue().entrySet()) {
          String querySequence = entry.getKey();
          String pathString = path.getKey();
          String intermediateNodes = pathQuery.getIntermediateNodes().get(pathString);
          NPMICalculator pc = new NPMICalculator(pathString, querySequence, inputTriple,
              intermediateNodes, path.getValue(), count_predicate_Triples, count_subject_Triples,
              count_object_Triples, subjectTypes, objectTypes, queryExecutioner, filter, vTy);
          pmiCallables.add(pc);
        }
      }
    }

    stepTime = logElapsedTimeThisStep("path scorring", stepTime);
    LOGGER.info("there are " + pmiCallables.size() + " pmiCallables ");
    // for experiments, use run in parallel
    /*
     * try { ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
     * 
     * for (Future<Result> result : executor.invokeAll(pmiCallables)) { if (result != null) {
     * LOGGER.info("elapsed time for " + result.get().path + " is " + result.get().elapsedTime /
     * 1_000_000_000); } if (result.get().hasLegalScore) { results.add(result.get()); } }
     * 
     * executor.shutdown(); } catch (Exception e) { e.printStackTrace(); }
     */

    for (NPMICalculator n : pmiCallables) {
      Result r;
      try {
        r = n.call();
        if (r != null)
          results.add(r);
        LOGGER.info(r.toString());
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }



    stepTime = logElapsedTimeThisStep("path PMICalculation", stepTime);

    LOGGER.info("there are " + results.size() + " Path ");

    List<Path> pathList = results.parallelStream()
        .map(r -> defaultPathFactory.ReturnPath(verbalize).createPath(subject, object, r))
        .collect(Collectors.toList());

    double[] scores = results.parallelStream().mapToDouble(r -> r.score).toArray();

    stepTime = logElapsedTimeThisStep("path lists", stepTime);

    corroborativeGraph.setPathList(pathList);

    Arrays.sort(scores);
    double score = summarist.summarize(scores);
    corroborativeGraph.setGraphScore(score);
    LOGGER.info("score is " + score + "");
    logElapsedTimeThisStep("all steps", startTime);
    return corroborativeGraph;
  }

  private long logElapsedTimeThisStep(String stepName, long time) {
    LOGGER.info("time elapsed for " + stepName + " :"
        + (double) (System.nanoTime() - time) / 1_000_000_000 + " seconds");
    time = System.nanoTime();
    return time;
  }

  public RDFNode getResource(Model model, Property property, Resource statement) {
    StmtIterator subjectIterator = model.listStatements(statement, property, (RDFNode) null);
    RDFNode resource = null;
    if (subjectIterator.hasNext())
      resource = subjectIterator.next().getObject();
    return resource;
  }

  public List<Statement> generateVerbalizingTriples(String builder, String path,
      String intermediateNodes, int pathLength, RDFNode subject, RDFNode object) {
    List<Statement> statementList = new ArrayList<Statement>();
    String[] paths = path.split(";");
    int prop = 1;
    int res = 1;
    for (int i = 0; i < paths.length; i++) {

      String property = "?p" + (prop);
      builder = builder.replace(property, paths[i]);
      prop++;
    }
    if (pathLength > 1) {
      String[] intermediateResources = intermediateNodes.split(";");
      for (int i = 0; i < intermediateResources.length; i++) {

        String resource = "?x" + (res);
        builder = builder.replace(resource, intermediateResources[i]);
        res++;
      }
    }

    builder = builder.replace("?s", subject.toString());
    builder = builder.replace("?o", object.toString());

    String[] triples = builder.split(";");
    for (int i = 0; i < triples.length; i++) {
      Resource resourceSubject = ResourceFactory.createResource(triples[i].split(" ")[0].trim());
      Property property = ResourceFactory.createProperty(triples[i].split(" ")[1].trim());
      Resource resourceObject = ResourceFactory.createResource(triples[i].split(" ")[2].trim());
      statementList.add(new StatementImpl(resourceSubject, property, resourceObject));
    }
    return statementList;
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

  public int countPredicateOccurrances(Node subject, Property property, Node objectType) {
    SelectBuilder occurrenceBuilder = new SelectBuilder();
    try {
      occurrenceBuilder.addVar("count(*)", "?c");
      occurrenceBuilder.addWhere(subject, property, objectType);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return returnCount(occurrenceBuilder);
  }

  public int countSOOccurrances(String var, Property property) {
    SelectBuilder occurrenceBuilder = new SelectBuilder();
    try {
      occurrenceBuilder.addVar(var, "?c");
      occurrenceBuilder.addWhere(NodeFactory.createVariable("s"), property,
          NodeFactory.createVariable("o"));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return returnCount(occurrenceBuilder);
  }

  /**
   * 
   * @param ontology is a string like http://dbpedia.org/ontology
   */
  public Set<Node> getTypeInformation(Resource subject, Property property) {
    Set<Node> types = new HashSet<Node>();
    SelectBuilder typeBuilder =
        new SelectBuilder().addPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    typeBuilder.addPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
    typeBuilder.addWhere(subject, property, NodeFactory.createVariable("x"));
    try {
      typeBuilder.addFilter("STRSTARTS(str(?x), \"" + ontologyURI + "\")");
    } catch (ParseException e) {
      e.printStackTrace();
    }

    Query typeQuery = typeBuilder.build();
    try (QueryExecution queryExecution = queryExecutioner.getQueryExecution(typeQuery);) {
      ResultSet resultSet = queryExecution.execSelect();
      while (resultSet.hasNext())
        types.add(resultSet.next().get("x").asNode());
    }
    return types;
  }

  public int returnCount(SelectBuilder builder) {
    Query queryOccurrence = builder.build();
    int count_Occurrence = 0;
    try (QueryExecution queryExecution = queryExecutioner.getQueryExecution(queryOccurrence);) {
      ResultSet resultSet = queryExecution.execSelect();
      if (resultSet.hasNext())
        count_Occurrence = resultSet.next().get("?c").asLiteral().getInt();
    }
    return count_Occurrence;
  }

  public void setMaxThreads(int maxThreads) {
    this.maxThreads = maxThreads;
  }
}
