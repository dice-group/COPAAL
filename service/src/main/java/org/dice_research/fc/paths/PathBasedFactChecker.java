package org.dice_research.fc.paths;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.IFactChecker;
import org.dice_research.fc.data.FactCheckingResult;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.filter.*;
import org.dice_research.fc.sum.ScoreSummarist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class implements the typical process for checking a given fact.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@Component
public class PathBasedFactChecker implements IFactChecker {

  private static final Logger LOGGER = LoggerFactory.getLogger(PathBasedFactChecker.class);
  /**
   * The preprocessor used to prepare the given fact.
   */
  protected FactPreprocessor factPreprocessor;
  /**
   * The class that is used to search for (corroborative) paths.
   */
  protected IPathSearcher pathSearcher;
  /**
   * A class that can be used to filter paths.
   */
  protected IPathFilter pathFilter;
  /**
   * The path scorer that is used to score the single paths.
   */
  protected IPathScorer pathScorer;
  private double pathFilterThreshold;
  /**
   * A class that can be used to filter path scores.
   */
  protected IScoreFilter scoreFilter;
  /**
   * The class that is used to summarize the scores of the single paths to create a final score.
   */
  protected ScoreSummarist summarist;
  /**
   * The score if no paths were found.
   */
  protected double pathsNotFoundResult = 0;

  protected Map<String,Long> queryResultMap = new HashMap<>();

  /**
   * Constructor.
   * 
   * @param factPreprocessor The preprocessor used to prepare the given fact.
   * @param pathSearcher The class that is used to search for (corroborative) paths.
   * @param pathScorer The path scorer that is used to score the single paths.
   * @param summarist The class that is used to summarize the scores of the single paths to create a final score.
   */
  @Autowired
  public PathBasedFactChecker(FactPreprocessor factPreprocessor, IPathSearcher pathSearcher,
      IPathScorer pathScorer, ScoreSummarist summarist,double pathFilterThreshold, String[] propertyFilter) {
    super();
    this.factPreprocessor = factPreprocessor;
    this.pathSearcher = pathSearcher;
    this.pathScorer = pathScorer;
    this.summarist = summarist;
    this.pathFilterThreshold = pathFilterThreshold;
    scoreFilter = new ZeroScoreFilter(pathFilterThreshold);
    if(propertyFilter!=null) {
      LOGGER.info("propertyFilter number is :" + propertyFilter.length);
    }else{
      LOGGER.info("propertyFilter is null :" );
    }
    pathFilter = new PropertiesFilter(propertyFilter);
  }

  /**
   * Checks the given fact.
   * 
   * @param subject the subject of the fact to check
   * @param predicate the predicate of the fact to check
   * @param object the object of the fact to check
   * @return The result of the fact checking
   */
  @Override
  public FactCheckingResult check(Resource subject, Property predicate, Resource object) {
    try{
      LOGGER.trace(" -------------  START OF FACT CHECKING @ PathBasedFactChecker-------------");
      // Preprocess the data
      LOGGER.trace(" -------------  Start to preprocess the data  -------------");
      Statement fact = ResourceFactory.createStatement(subject, predicate, object);
      Predicate preparedPredicate = factPreprocessor.generatePredicate(fact);
      LOGGER.trace(" -------------  Preprocess the data Done   -------------");

      // Get a list of potential paths
      LOGGER.trace(" -------------  Start to get a list of potential paths  -------------");
      Collection<QRestrictedPath> paths = pathSearcher.search(subject, preparedPredicate, object);
      LOGGER.trace(" -------------  Get a list of potential paths Done  -------------");

      if(paths.isEmpty()) {
        LOGGER.trace(" -------------  No Path Found  -------------");
        return new FactCheckingResult(pathsNotFoundResult, paths, fact);
      }

      // Filter paths, score the paths with respect to the given triple and filter them again based on
      // the score
      LOGGER.trace(" -------------  Start to filter and Score  -------------");
      LOGGER.info("number of paths before filtering is {}",paths.size());
      LOGGER.info("pathfilter is "+pathFilter.getClass().getName());
      paths = paths.parallelStream().filter(pathFilter)
              .map(p -> pathScorer.score(subject, preparedPredicate, object, p))
              .filter(p -> scoreFilter.test(p.getScore())).collect(Collectors.toList());
      LOGGER.info("number of paths after filtering is {}",paths.size());
      LOGGER.trace(" -------------  Filter and Score Done  -------------");

      // Get the scores
      LOGGER.trace(" -------------  Start to get the scores  -------------");
      double[] scores = paths.stream().mapToDouble(p -> p.getScore()).toArray();
      if(LOGGER.isTraceEnabled()){
        LOGGER.trace("list of paths with their score");
        for(QRestrictedPath p :paths){
          LOGGER.trace("{} : {}",p.toString(),p.getScore());
        }
      }
      LOGGER.trace(" -------------  Get the scores Done  -------------");

      // Summarize the scores
      LOGGER.trace(" summarist is : {}",summarist.getClass().getName());
      LOGGER.trace(" scores are : {}", Arrays.toString(scores));
      double veracity = summarist.summarize(scores);
      LOGGER.debug(" calculated veracity is : {}",veracity);
      LOGGER.trace(" ------------- END OF FACT CHECKING -------------");

      return new FactCheckingResult(veracity, paths, fact);
    }catch (Exception ex){
      LOGGER.error(ex.getMessage());
      return new FactCheckingResult(-1, null, null);
    }
  }

  /**
   * @return the pathFilter
   */
  public IPathFilter getPathFilter() {
    return pathFilter;
  }

  /**
   * @param pathFilter the pathFilter to set
   */
  public void setPathFilter(IPathFilter pathFilter) {
    this.pathFilter = pathFilter;
  }

  /**
   * @return the scoreFilter
   */
  public IScoreFilter getScoreFilter() {
    return scoreFilter;
  }

  /**
   * @param scoreFilter the scoreFilter to set
   */
  public void setScoreFilter(IScoreFilter scoreFilter) {
    this.scoreFilter = scoreFilter;
  }
}
