package org.dice_research.fc.paths;

import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.IFactChecker;
import org.dice_research.fc.data.FactCheckingResult;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.filter.AlwaysTruePathFilter;
import org.dice_research.fc.paths.filter.AlwaysTrueScoreFilter;
import org.dice_research.fc.paths.filter.IPathFilter;
import org.dice_research.fc.paths.filter.IScoreFilter;
import org.dice_research.fc.sum.ScoreSummarist;
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
  protected IPathFilter pathFilter = new AlwaysTruePathFilter();
  /**
   * The path scorer that is used to score the single paths.
   */
  protected IPathScorer pathScorer;
  /**
   * A class that can be used to filter path scores.
   */
  protected IScoreFilter scoreFilter = new AlwaysTrueScoreFilter();
  /**
   * The class that is used to summarize the scores of the single paths to create a final score.
   */
  protected ScoreSummarist summarist;
  /**
   * The score if no paths were found.
   */
  protected double pathsNotFoundResult = 0;
  
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
      IPathScorer pathScorer, ScoreSummarist summarist) {
    super();
    this.factPreprocessor = factPreprocessor;
    this.pathSearcher = pathSearcher;
    this.pathScorer = pathScorer;
    this.summarist = summarist;
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
    // Preprocess the data
    Statement fact = ResourceFactory.createStatement(subject, predicate, object);
    Predicate preparedPredicate = factPreprocessor.generatePredicate(fact);

    // Get a list of potential paths
    Collection<QRestrictedPath> paths = pathSearcher.search(subject, preparedPredicate, object);
    
    if(paths.isEmpty()) {
      return new FactCheckingResult(pathsNotFoundResult, paths, fact);
    }

    // Filter paths, score the paths with respect to the given triple and filter them again based on
    // the score and verbalize if needed
    paths = paths.parallelStream().filter(pathFilter)
        .map(p -> pathScorer.score(subject, preparedPredicate, object, p))
        .filter(p -> scoreFilter.test(p.getScore())).collect(Collectors.toList());

    // Get the scores
    double[] scores = paths.stream().mapToDouble(p -> p.getScore()).toArray();

    // Summarize the scores
    double veracity = summarist.summarize(scores);

    return new FactCheckingResult(veracity, paths, fact);
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
