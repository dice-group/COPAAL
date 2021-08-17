package org.dice_research.fc.config;


//import java.util.*;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.IBidirectionalMapper;
import org.dice_research.fc.IFactChecker;
import org.dice_research.fc.IMapper;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.*;

import org.dice_research.fc.paths.export.DefaultExporter;
import org.dice_research.fc.paths.export.IPathExporter;
import org.dice_research.fc.paths.imprt.*;

//import org.dice_research.fc.paths.imprt.ThirdPartyPathImporter;
import org.dice_research.fc.paths.map.PathMapper;
import org.dice_research.fc.paths.map.PropertyElementMapper;
import org.dice_research.fc.paths.map.PropertyMapper;
import org.dice_research.fc.paths.model.Path;
import org.dice_research.fc.paths.model.PathElement;

import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.paths.scorer.NPMIBasedScorer;
import org.dice_research.fc.paths.scorer.PNPMIBasedScorer;
import org.dice_research.fc.paths.scorer.count.ApproximatingCountRetriever;
import org.dice_research.fc.paths.scorer.count.PropPathBasedPairCountRetriever;
import org.dice_research.fc.paths.scorer.count.decorate.CachingCountRetrieverDecorator;
import org.dice_research.fc.paths.scorer.count.max.DefaultMaxCounter;
import org.dice_research.fc.paths.scorer.count.max.MaxCounter;
import org.dice_research.fc.paths.scorer.count.max.VirtualTypesMaxCounter;
import org.dice_research.fc.paths.search.SPARQLBasedSOPathSearcher;
import org.dice_research.fc.sparql.filter.EqualsFilter;
import org.dice_research.fc.sparql.filter.IRIFilter;
import org.dice_research.fc.sparql.filter.NamespaceFilter;
import org.dice_research.fc.sparql.query.QueryExecutionFactoryCustomHttp;
import org.dice_research.fc.sparql.query.QueryExecutionFactoryCustomHttpTimeout;
import org.dice_research.fc.sum.AdaptedRootMeanSquareSummarist;
import org.dice_research.fc.sum.CubicMeanSummarist;
import org.dice_research.fc.sum.FixedSummarist;
import org.dice_research.fc.sum.HigherOrderMeanSummarist;
import org.dice_research.fc.sum.NegScoresHandlingSummarist;
import org.dice_research.fc.sum.OriginalSummarist;
import org.dice_research.fc.sum.ScoreSummarist;
import org.dice_research.fc.sum.SquaredAverageSummarist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Configuration class containing the variables present in the applications.properties file and the
 * bean configurations.
 *
 */
@Configuration
@PropertySource(value = "classpath:application.properties")
public class Config {

  /**
   * The SPARQL endpoint URL
   */
  @Value("${info.service.url.default:}")
  private String serviceURL;

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
  @Value("${dataset.virtual-types:false}")
  private boolean isVirtualTypes;

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
    switch (metaPaths) {
      case "EstherPathProcessor":
        return new EstherPathProcessor(preprocessedPaths, qef);
      default:
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
      IPathScorer pathScorer, ScoreSummarist summarist, MetaPathsProcessor metaProcessor) {
    if (isPathsLoad) {
      return new ImportedFactChecker(factPreprocessor, pathSearcher, pathScorer, summarist,
          metaProcessor);
    } else {
      //return new PathBasedFactChecker(factPreprocessor, pathSearcher, pathScorer, summarist);
      return new PathBasedFactCheckerSavePathScore(factPreprocessor, pathSearcher, pathScorer, summarist);
    }
  }

  /**
   * 
   * @param qef
   * @param filter
   * @return The desired {@link IPathSearcher} implementation.
   */
  @Bean
  public IPathSearcher getPathSearcher(QueryExecutionFactory qef, Collection<IRIFilter> filter) {
    return new SPARQLBasedSOPathSearcher(qef, maxLength, filter);
  }

  /**
   * @param qef
   * @param maxCounter
   * @return The desired {@link ICountRetriever} implementation.
   */
  @Bean
  public ICountRetriever getCountRetriever(QueryExecutionFactory qef, MaxCounter maxCounter) {
    ICountRetriever countRetriever;
    switch (counter) {
      case "ApproximatingCountRetriever":
        countRetriever = new ApproximatingCountRetriever(qef, maxCounter);
        break;
      case "PropPathBasedPairCountRetriever":
        countRetriever = new PropPathBasedPairCountRetriever(qef, maxCounter);
        break;
      default:
        countRetriever = new PropPathBasedPairCountRetriever(qef, maxCounter);
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
    if (isVirtualTypes) {
      maxCounter = new VirtualTypesMaxCounter(qef);
    } else {
      maxCounter = new DefaultMaxCounter(qef);
    }
    return maxCounter;
  }

  /**
   * @param countRetriever
   * @return The desired {@link IPathScorer} implementation.
   */
  @Bean
  public IPathScorer getPathScorer(ICountRetriever countRetriever) {
    switch (scorer) {
      case "NPMI":
        return new NPMIBasedScorer(countRetriever);
      case "PNPMI":
        return new PNPMIBasedScorer(countRetriever);
      default:
        return new NPMIBasedScorer(countRetriever);
    }
  }

  /**
   * @return The {@link IRIFilter} object with the unwanted properties
   */
  @Bean
  public Collection<IRIFilter> getFilter() {
    List<IRIFilter> filters = new ArrayList<IRIFilter>();
    filters.add(new EqualsFilter(filteredProperties));
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
      qef = new QueryExecutionFactoryCustomHttp(serviceURL);
    } else {
      Model model = ModelFactory.createDefaultModel();
      model.read(filePath);
      qef = new QueryExecutionFactoryModel(model);
    }
    qef = new QueryExecutionFactoryCustomHttpTimeout(qef, 30000);
    return qef;
  }

  /**
   * @return The {@link FactPreprocessor} object dependent on whether we want virtual types or not
   */
  @Bean
  public FactPreprocessor getPreprocessor(QueryExecutionFactory qef) {
    if (isVirtualTypes) {
      return new EmptyPredicateFactory();
    } else {
      return new PredicateFactory(qef);
    }
  }

  /**
   * @return The desired {@link ScoreSummarist} implementation.
   */
  @Bean
  public ScoreSummarist getSummarist() {
    switch (summaristType) {
      case "AdaptedRootMeanSquareSummarist":
        return new AdaptedRootMeanSquareSummarist();
      case "CubicMeanSummarist":
        return new CubicMeanSummarist();
      case "FixedSummarist":
        return new FixedSummarist();
      case "HigherOrderMeanSummarist":
        return new HigherOrderMeanSummarist();
      case "NegScoresHandlingSummarist":
        return new NegScoresHandlingSummarist();
      case "OriginalSummarist":
        return new OriginalSummarist();
      case "SquaredAverageSummarist":
        return new SquaredAverageSummarist();
      default:
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

  @Bean
  public IPathImporter getImporter() {
    return new DefaultImporter();
  }

  @Bean
  public IPathExporter getExporter() {
    return new DefaultExporter(preprocessedPaths);
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