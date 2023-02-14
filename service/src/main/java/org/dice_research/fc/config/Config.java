package org.dice_research.fc.config;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.apache.commons.math3.util.Pair;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.dice_research.fc.IBidirectionalMapper;
import org.dice_research.fc.IFactChecker;
import org.dice_research.fc.IMapper;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.*;

import org.dice_research.fc.paths.export.DefaultExporter;
import org.dice_research.fc.paths.export.IPathExporter;
import org.dice_research.fc.paths.imprt.*;

import org.dice_research.fc.paths.map.PathMapper;
import org.dice_research.fc.paths.map.PropertyElementMapper;
import org.dice_research.fc.paths.map.PropertyMapper;
import org.dice_research.fc.paths.model.Path;
import org.dice_research.fc.paths.model.PathElement;

import org.dice_research.fc.paths.sampler.IPathSampler;
import org.dice_research.fc.paths.sampler.QRestrictedPathSampler;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.paths.scorer.NPMIBasedScorer;
import org.dice_research.fc.paths.scorer.PNPMIBasedScorer;
import org.dice_research.fc.paths.scorer.PreCalculationScorer;
import org.dice_research.fc.paths.scorer.count.ApproximatingCountRetriever;
import org.dice_research.fc.paths.scorer.count.PairCountRetriever;
import org.dice_research.fc.paths.scorer.count.SPARQLBasedResultStreamingCountRetriever;
import org.dice_research.fc.paths.scorer.count.TentrisBasedCountRetriever;
import org.dice_research.fc.paths.scorer.count.decorate.CachingCountRetrieverDecorator;
import org.dice_research.fc.paths.scorer.count.decorate.SaveInDBCountDecorator;
import org.dice_research.fc.paths.scorer.count.max.*;
import org.dice_research.fc.paths.search.PreProcessPathSearcher;
import org.dice_research.fc.paths.search.SPARQLBasedSOPathSearcher;
import org.dice_research.fc.paths.search.CachingPathSearcherDecorator;
import org.dice_research.fc.paths.verbalizer.DefaultPathVerbalizer;
import org.dice_research.fc.paths.verbalizer.IPathVerbalizer;
import org.dice_research.fc.paths.verbalizer.MultiplePathVerbalizer;
import org.dice_research.fc.paths.verbalizer.NoopVerbalizer;
import org.dice_research.fc.sparql.filter.EqualsFilter;
import org.dice_research.fc.sparql.filter.IRIFilter;
import org.dice_research.fc.sparql.filter.NamespaceFilter;
import org.dice_research.fc.sparql.path.BGPBasedPathClauseGenerator;
import org.dice_research.fc.sparql.path.IPathClauseGenerator;
import org.dice_research.fc.sparql.path.PropPathBasedPathClauseGenerator;
import org.dice_research.fc.sparql.query.IQueryValidator;
import org.dice_research.fc.sparql.query.ListBaseQueryValidator;
import org.dice_research.fc.sparql.query.QueryExecutionFactoryCustomHttp;
import org.dice_research.fc.sparql.query.QueryExecutionFactoryCustomHttpTimeout;
import org.dice_research.fc.sparql.restrict.TypeBasedRestriction;
import org.dice_research.fc.sum.AdaptedRootMeanSquareSummarist;
import org.dice_research.fc.sum.CubicMeanSummarist;
import org.dice_research.fc.sum.FixedSummarist;
import org.dice_research.fc.sum.HigherOrderMeanSummarist;
import org.dice_research.fc.sum.NegScoresHandlingSummarist;
import org.dice_research.fc.sum.OriginalSummarist;
import org.dice_research.fc.sum.ScoreSummarist;
import org.dice_research.fc.sum.SquaredAverageSummarist;
import org.dice_research.fc.tentris.TentrisAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryBackQuery;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;

/**
 * Configuration class containing the variables present in the applications.properties file and the
 * bean configurations.
 *
 */
@Configuration
@PropertySource(value = "classpath:application.properties")
public class Config {
  private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
  /**
   * The SPARQL endpoint URL
   */
  @Value("${info.service.url.default:}")
  private String serviceURL;

  /**
   * show if sparql endpoint needs an authentication (true) or not (false)
   */
  @Value("${info.service.url.needAuthentication:}")
  private String sparqlEndpointNeedAuthentication;

  /**
   *
   */
  @Value("${info.service.url.username:}")
  private String sparqlEndpointUsername;

  /**
   * The SPARQL endpoint URL
   */
  @Value("${info.service.url.password:}")
  private String sparqlEndpointPassword;

  /**
   * The SPARQL endpoint URL
   */
  @Value("${copaal.tentris.endpoint:}")
  private String tentrisURL;

  /**
   * The desired score summarist. It should have the same name as the class names.
   */
  @Value("${dataset.scorer.summarist.type:}")
  private String summaristType;

  /**
   * The properties we want to filter from the discovered paths
   */
  @Value("${dataset.filter.properties:}")
  private String[] filteredProperties;

  /**
   * The namespace we are interested in
   */
  @Value("${dataset.filter.namespace:}")
  private String[] namespaceFilters;

  /**
   * The graph's file path if we want to run the application locally
   */
  @Value("${dataset.file.path:}")
  private String filePath;

  /**
   * Virtual types flag
   */
  @Value("${copaal.factpreprocessor.type}")
  private String factPreprocessorType;
  /**
   * This flag indicate that use BGPVirtualTypeRestriction if it is true
   */
  @Value("${copaal.factpreprocessor.ShouldUseBGPVirtualTypeRestriction}")
  private boolean ShouldUseBGPVirtualTypeRestriction;

  /**
   * This flag indicate that use BGPVirtualTypeRestriction if it is true
   */
  @Value("${dataset.sparql.foorbidLoop}")
  private boolean foorbidLoop;

  /**
   * Path's maximum length
   */
  @Value("${dataset.max.length:3}")
  private int maxLength;

  /**
   * The score calculator
   */
  @Value("${dataset.scorer.type:}")
  private String scorer;

  /**
   * The count retriever type
   */
  @Value("${dataset.sparql.counter:}")
  private String counter;

  /**
   * Score cache flag
   */
  @Value("${cache:true}")
  private boolean isCache;

  /**
   * The meta-paths pre-processor
   */
  @Value("${dataset.file.metapaths.processor:}")
  private String metaPaths;

  /**
   * The preprocessed folder path
   */
  @Value("${dataset.file.preprocess.path:}")
  private String preprocessedPaths;

  /**
   * Do we want to load the paths from file
   */
  @Value("${dataset.file.metapaths:false}")
  private boolean isPathsLoad;
  /**
   * The PathSearcher
   */
  @Value("${dataset.pathsearcher.type:}")
  private String pathSearcher;

  /**
  * The typeOfQueryResult could be Json or XML
  *
  */

  @Value("${copaal.query.typeOfQueryResult:}")
  private String typeOfQueryResult;

  /**
    * Time out for run sparql queries
    *
    */
  @Value("${copaal.query.timeout:}")
  private long timeOut = 0;


   /* type of http verb at http client for do queries
   */
  @Value("${copaal.http.query.type:}")
  private String isPostRequest;

  @Value("${copaal.preprocess.NPMIthreshold:}")
  private double preProcessPathNPMIThreshold;

  /**
   * The Path Clause Generator Type
   */
  @Value("${copaal.pathclausegenerator.type:}")
  private String pathClauseGeneratorType;

  @Value("${copaal.preprocess.addressOfPathInstancesCountFile:}")
  private String addressOfPathInstancesCountFile;

  @Value("${copaal.preprocess.addressOfPredicateInstancesCountFile:}")
  private String addressOfPredicateInstancesCountFile;

  @Value("${copaal.preprocess.addressOfCoOccurrenceCountFile:}")
  private String addressOfCoOccurrenceCountFile;

  @Value("${copaal.preprocess.addressOfMaxCountFile:}")
  private String addressOfMaxCountFile;

  @Value("${copaal.graphName:}")
  private String graphName;

  @Value("${copaal.printTheExampleOfEachFoundedPath:}")
  private String printTheExampleOfEachFoundedPath;

  @Value("${copaal.invalidQueries:}")
  private String invalidQueries;

  @Value("${copaal.pathFilterThreshold}")
  private double pathFilterThreshold;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  /**
   * 
   * @param qef
   * @return The desired {@link MetaPathsProcessor} implementation.
   */
  @Bean
  public MetaPathsProcessor getMetaPathsProcessor(QueryExecutionFactory qef) {
    switch (metaPaths.toLowerCase().trim()) {
      case "estherpathprocessor":
        return new EstherPathProcessor(preprocessedPaths, qef);
      default:
        LOGGER.info("Warning : it use default MetaPathProcessor");
        return new NoopPathProcessor(preprocessedPaths, qef);
    }
  }

  /**
   * 
   * @param factPreprocessor
   * @param pathSearcher
   * @param pathScorer
   * @param summarist
   * @param metaProcessor
   * @return The desired {@link IFactChecker} implementation.
   */
  @Bean
  public IFactChecker getFactChecker(FactPreprocessor factPreprocessor, IPathSearcher pathSearcher,
      IPathScorer pathScorer, ScoreSummarist summarist, MetaPathsProcessor metaProcessor, QueryExecutionFactory qef) {
    boolean LogThePaths = false;
    if(this.printTheExampleOfEachFoundedPath.toLowerCase().equals("true")){
      LogThePaths = true;
    }
    if (isPathsLoad) {
      return new ImportedFactChecker(factPreprocessor, pathSearcher, pathScorer, summarist,
          metaProcessor, qef, LogThePaths, pathFilterThreshold, filteredProperties);
    } else {
      return new PathBasedFactChecker(factPreprocessor, pathSearcher, pathScorer, summarist, pathFilterThreshold, filteredProperties);
    }
  }

  /**
   * 
   * @param qef
   * @param filter
   * @return The desired {@link IPathSearcher} implementation.
   */
  @Bean
  public IPathSearcher getPathSearcher(QueryExecutionFactory qef, Collection<IRIFilter> filter,IMapper<Path, QRestrictedPath> mapper,
                                       IMapper<Pair<Property, Boolean>, PathElement> propertyElementMapper,ICountRetriever counterRetrieverClass,
                                       FactPreprocessor factPreprocessorClass, IPathScorer pathScorerClass, IPreProcessProvider preProcessProvider, TentrisAdapter adapter) {
    switch (pathSearcher.toLowerCase().trim()){
      case "loadsavedecorator" :
        String counterRetriever = counterRetrieverClass.getClass().getName();
        String factPreprocessor = factPreprocessorClass.getClass().getName();
        String pathScorer = pathScorerClass.getClass().getName();
        return new CachingPathSearcherDecorator(new SPARQLBasedSOPathSearcher(qef, maxLength, filter),mapper,propertyElementMapper,counterRetriever,factPreprocessor,pathScorer);
      case "preprocess":
        return new PreProcessPathSearcher(preProcessProvider, adapter);
      default:
        LOGGER.info("Warning : it use default Pathsearcher");
        return new SPARQLBasedSOPathSearcher(qef, maxLength, filter);
    }
  }

  @Bean
  public TentrisAdapter getTentrisAdapter()
  {
    TentrisAdapter adapter = new TentrisAdapter(tentrisURL);
    return adapter;
  }

  /**
   * @param qef
   * @param maxCounter
   * @return The desired {@link ICountRetriever} implementation.
   */
  @Bean
  public ICountRetriever getCountRetriever(QueryExecutionFactory qef, MaxCounter maxCounter, IPathClauseGenerator pathClauseGenerator,IPreProcessProvider preProcessProvider, IQueryValidator queryValidator) {
    ICountRetriever countRetriever;
    switch (counter.toLowerCase().trim()) {
      case "approximatingcountretriever":
        countRetriever = new ApproximatingCountRetriever(qef, maxCounter, queryValidator);
        break;
      case "paircountretriever":
        countRetriever = new PairCountRetriever(qef, maxCounter, pathClauseGenerator, queryValidator);
        break;
      case "paircountretrieverwithdb":
        LOGGER.info("use caching for paircountretriever with this graphname"+graphName);
        countRetriever = new PairCountRetriever(qef, maxCounter, pathClauseGenerator, queryValidator);
        countRetriever = new SaveInDBCountDecorator(countRetriever, graphName);
        break;
      case "preprocess":
        countRetriever = new PreCalculationScorer(preProcessProvider);
        break;
      case "tentris":
        TentrisAdapter tentris = new TentrisAdapter(HttpClients.createDefault(), tentrisURL);
        countRetriever = new TentrisBasedCountRetriever(tentris, new TentrisBasedMaxCounter(tentris), new BGPBasedPathClauseGenerator());

/*        QueryExecutionFactory qef2 = new QueryExecutionFactoryCustomHttp(tentrisURL, false, typeOfQueryResult, false, sparqlEndpointUsername, sparqlEndpointPassword);
        MaxCounter maxCounter2 = new HybridMaxCounter(qef2);
        countRetriever = new PairCountRetriever(qef2, maxCounter2, pathClauseGenerator, queryValidator);*/
        break;
      case "tentriswithdb":
        LOGGER.info("for tentris chaching");
        TentrisAdapter tentris2 = new TentrisAdapter(HttpClients.createDefault(), tentrisURL);
        countRetriever = new TentrisBasedCountRetriever(tentris2, new TentrisBasedMaxCounter(tentris2),
                new BGPBasedPathClauseGenerator());
        LOGGER.info("counter retriver is :"+countRetriever.getClass());
        countRetriever  = new SaveInDBCountDecorator(countRetriever,graphName);
        LOGGER.info("and then is counter retriver is :"+countRetriever.getClass());
        break;
      case "streamingcountretriever":
        qef = new QueryExecutionFactoryPaginated(qef);
        countRetriever = new SPARQLBasedResultStreamingCountRetriever(qef, maxCounter, queryValidator);
          break;
      default:
        LOGGER.info("Warning : it use default count retriever");
        countRetriever = new PairCountRetriever(qef, maxCounter, pathClauseGenerator, queryValidator);
        break;
    }
    if (isCache) {
      countRetriever = new CachingCountRetrieverDecorator(countRetriever);
    }
    return countRetriever;
  }

  /**
   * 
   * @param qef
   * @return The desired {@link MaxCounter} implementation.
   */
  @Bean
  public MaxCounter getMaxCounter(QueryExecutionFactory qef) {
    MaxCounter maxCounter;

    switch (factPreprocessorType.toLowerCase().trim()) {
      case ("predicatefactory"):
        return new DefaultMaxCounter(qef);
      case ("virtualtypepredicatefactory"):
        return new VirtualTypesMaxCounter(qef);
      case ("hybridpredicatefactory"):
        return new HybridMaxCounter(qef);
      default:
        LOGGER.info("Warning : it use default Maxcounter");
        return new DefaultMaxCounter(qef);
    }
  }

  /**
   * @param countRetriever
   * @return The desired {@link IPathScorer} implementation.
   */
  @Bean
  public IPathScorer getPathScorer(ICountRetriever countRetriever) {
    switch (scorer.toLowerCase().trim()) {
      case "npmi":
        return new NPMIBasedScorer(countRetriever);
      case "pnpmi":
        return new PNPMIBasedScorer(countRetriever);
      default:
        LOGGER.info("Warning : it use default path scorer");
        return new NPMIBasedScorer(countRetriever);
    }
  }

  /**
   * @return The {@link IRIFilter} object with the unwanted properties
   */
  @Bean
  public Collection<IRIFilter> getFilter() {
    List<IRIFilter> filters = new ArrayList<IRIFilter>();
    LOGGER.info("there are "+ filteredProperties.length+" filtered properties");
    if(filteredProperties.length > 0) {
      filters.add(new EqualsFilter(filteredProperties));
    }
    LOGGER.info("there are "+ namespaceFilters.length+" namespace filter");
    for (int i = 0; i < namespaceFilters.length; i++) {
      filters.add(new NamespaceFilter(namespaceFilters[i], false));
    }
    return filters;
  }

  /**
   * @return The {@link QueryExecutionFactory} object depending on whether we are running the
   *         application on a local graph or on a remote endpoint
   */
  @Bean
  public QueryExecutionFactory getQueryExecutionFactory() {
    QueryExecutionFactory qef;
    if (filePath == null || filePath.isEmpty()) {
      boolean isSparqlEndpointNeedAuthentication = false;
      if(sparqlEndpointNeedAuthentication.trim().equals("true")){
        isSparqlEndpointNeedAuthentication = true;
      }
      if(isPostRequest.equalsIgnoreCase("post")) {
          qef = new QueryExecutionFactoryCustomHttp(serviceURL, true, typeOfQueryResult, isSparqlEndpointNeedAuthentication, sparqlEndpointUsername, sparqlEndpointPassword);
      }else{
        qef = new QueryExecutionFactoryCustomHttp(serviceURL, false, typeOfQueryResult, isSparqlEndpointNeedAuthentication, sparqlEndpointUsername, sparqlEndpointPassword);
      }
    } else {
      Model model = ModelFactory.createDefaultModel();
      model.read(filePath);
      qef = new QueryExecutionFactoryModel(model);
    }
    qef = new QueryExecutionFactoryCustomHttpTimeout(qef, timeOut);
    return qef;
  }

  /**
   * @return The {@link FactPreprocessor} object dependent on whether we want virtual types or not
   */
  @Bean
  public FactPreprocessor getPreprocessor(QueryExecutionFactory qef,Collection<IRIFilter> filters) {
    switch (factPreprocessorType.toLowerCase().trim()) {
      case ("predicatefactory"):
        return new PredicateFactory(qef);
      case ("virtualtypepredicatefactory"):
        return new VirtualTypePredicateFactory();
      case ("hybridpredicatefactory"):
        return new HybridPredicateFactory(qef,ShouldUseBGPVirtualTypeRestriction,filters);
      case ("hybridpredicatetentrisfactory"):
        return new HybridPredicateTentrisFactory(allPredicates("collected_predicates.json"));
      default:
        LOGGER.info("Warning : it use default preprocessor");
        return new PredicateFactory(qef);
    }
  }


  // read a json file with name [filename] and extract all predicates in the json array
  public Set<Predicate> allPredicates(String fileName) {
    Set<Predicate> predicates = new HashSet<Predicate>();
    JSONParser parser = new JSONParser();
    try {
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      InputStream is = classloader.getResourceAsStream(fileName);
      Object obj = parser.parse( new InputStreamReader(is));

      // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
      JSONArray Predicates = (JSONArray) obj;

      Iterator<JSONObject> iterator = Predicates.iterator();
      while (iterator.hasNext()) {
        JSONObject jsonPredicate = iterator.next();
        // get domain
        Set<String> domainsSet = new HashSet<>();
        JSONArray domainsJson = (JSONArray)jsonPredicate.get("Domain") ;
        for(int i = 0 ; i < domainsJson.size() ; i++){
          domainsSet.add(domainsJson.get(i).toString());
        }
        TypeBasedRestriction domain = new TypeBasedRestriction(domainsSet);
        // get range
        Set<String> rangesSet = new HashSet<>();
        JSONArray rangesJson = (JSONArray)jsonPredicate.get("Range") ;
        for(int i = 0 ; i < rangesJson.size() ; i++){
          rangesSet.add(rangesJson.get(i).toString());
        }
        TypeBasedRestriction range = new TypeBasedRestriction(rangesSet);

        Property p = new PropertyImpl(jsonPredicate.get("Predicate").toString());
        Predicate predicate = new Predicate(p,domain,range);
        predicates.add(predicate);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return predicates;
  }

  /**
   * @return The desired {@link ScoreSummarist} implementation.
   */
  @Bean
  public ScoreSummarist getSummarist() {
    switch (summaristType.toLowerCase().trim()) {
      case "adaptedrootmeansquaresummarist":
        return new AdaptedRootMeanSquareSummarist();
      case "cubicmeansummarist":
        return new CubicMeanSummarist();
      case "fixedsummarist":
        return new FixedSummarist();
      case "higherordermeansummarist":
        return new HigherOrderMeanSummarist();
      case "negscoreshandlingsummarist":
        return new NegScoresHandlingSummarist();
      case "originalsummarist":
        return new OriginalSummarist();
      case "squaredaveragesummarist":
        return new SquaredAverageSummarist();
      default:
        LOGGER.info("Warning : it use default score summarist");
        return new OriginalSummarist();
    }
  }

  /**
   * @return {@link PathMapper} .
   */
  @Bean
  public IMapper<Path, QRestrictedPath> getPathMapper(){
    return new PathMapper();
  }

  /**
   * @return {@link PropertyMapper} .
   */
  @Bean
  public IBidirectionalMapper<Property,String> getPropertyMapper(){
    return new PropertyMapper();
  }

  @Bean
  public IMapper<Pair<Property, Boolean>, PathElement> getPropertyElementMapper(){
    return new PropertyElementMapper();
  }
  /**
   * @return The desired {@link IPathImporter} implementation.
   */
  @Bean
  public IPathImporter getImporter() {
    return new DefaultImporter();
  }

  /**
   * @return The desired {@link IPathExporter} implementation.
   */
  @Bean
  public IPathExporter getExporter() {
    return new DefaultExporter(preprocessedPaths);
  }

  /**
   * @return The desired {@link IPathClauseGenerator} implementation.
   */
  @Bean
  IPathClauseGenerator getPathClauseGenerator(){
    switch (pathClauseGeneratorType.toLowerCase().trim()){
      case("proppathbasedpathclausegenerator"):
        return new PropPathBasedPathClauseGenerator();
      case ("bgpbasedpathclausegenerator"):
        return new BGPBasedPathClauseGenerator(foorbidLoop);
        // TODO add with no loop
      default:
        LOGGER.info("Warning : it use default path generator");
        return new PropPathBasedPathClauseGenerator();
    }
  }

  @Bean
  IPreProcessProvider getPreProcessProvider(){

    /*File pathInstancesCountFile = new File(addressOfPathInstancesCountFile);
    File predicateInstancesCountFile = new File(addressOfPredicateInstancesCountFile);
    File coOccurrenceCountFile = new File(addressOfCoOccurrenceCountFile);
    File maxCountFile = new File(addressOfMaxCountFile);*/
    List<Predicate> validPredicates = allValidPredicates();
    //return new PreProcessProvider(pathInstancesCountFile, predicateInstancesCountFile, coOccurrenceCountFile, maxCountFile, preProcessPathNPMIThreshold, validPredicates);
    LOGGER.info("serviceURL is :"+serviceURL);
    return new PreProcessProvider(addressOfPathInstancesCountFile, addressOfPredicateInstancesCountFile, addressOfCoOccurrenceCountFile, addressOfMaxCountFile, preProcessPathNPMIThreshold, validPredicates);
  }

  @Bean
  IQueryValidator getQueryValidator(){
    List<String> invalidQueries = extractInvalidQueries();
    IQueryValidator validator = new ListBaseQueryValidator(invalidQueries);
    return validator;
  }

  @Bean
  IPathSampler getPathSampler(QueryExecutionFactory qef){
    return new QRestrictedPathSampler(qef);
  }

  private List<String> extractInvalidQueries() {
    List<String> invalidQ = new ArrayList<>();
    String[] qq = invalidQueries.split(",");
    for (String q:qq) {
      invalidQ.add(q);
      LOGGER.info(q);
    }
    LOGGER.info("loads "+ invalidQ.size()+" invalid queries");
    return invalidQ;
  }

  private List<Predicate> allValidPredicates() {
    List<Predicate> predicates = new ArrayList<Predicate>(allPredicates("validPredicates.json"));
    return predicates;
  }

  /**
   * @return The desired {@link IPathVerbalizer} implementation based on HTTP request parameters.
   */
  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public IPathVerbalizer getVerbalizer(QueryExecutionFactory qef, RequestParameters details) {
    if (details.isVerbalize()) {
      return new MultiplePathVerbalizer(qef);
    } else {
      return new NoopVerbalizer();
    }
  }
}
// TODO: we can also use reflection instead of switch case statements?
// ScoreSummarist pathScorer = null;
//// get classes implementing curClass
// ServiceLoader<? extends ScoreSummarist> serviceLoader =
// ServiceLoader.load(ScoreSummarist.class);
// for (ScoreSummarist curScorer : serviceLoader) {
// Class<? extends ScoreSummarist> className = curScorer.getClass();
// if (className.getSimpleName().equals(summaristType)) {
// try {
// // call corresponding empty constructor
// Constructor<? extends ScoreSummarist> cons = className.getConstructor();
// pathScorer = cons.newInstance();
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
// }
// return pathScorer;
