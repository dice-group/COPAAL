package org.dice_research.fc.paths.imprt;

import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.FactCheckingResult;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.FactPreprocessor;
import org.dice_research.fc.paths.IPathScorer;
import org.dice_research.fc.paths.IPathSearcher;
import org.dice_research.fc.paths.PathBasedFactChecker;
import org.dice_research.fc.sum.ScoreSummarist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Similar to {@link PathBasedFactChecker}, but loads the search paths instead and might forego the
 * scoring of individual paths (if these are present in file).
 * 
 * @author Alexandra Silva
 *
 */
@Component
public class ImportedFactChecker extends PathBasedFactChecker {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportedFactChecker.class);

  /**
   * The imported facts processor
   */
  protected MetaPathsProcessor metaPreprocessor;


  @Autowired
  public ImportedFactChecker(FactPreprocessor factPreprocessor, IPathSearcher pathSearcher,
      IPathScorer pathScorer, ScoreSummarist summarist, MetaPathsProcessor metaPreprocessor) {
    super(factPreprocessor, pathSearcher, pathScorer, summarist);
    this.metaPreprocessor = metaPreprocessor;
  }

  @Override
  public FactCheckingResult check(Resource subject, Property predicate, Resource object) {
    Statement fact = ResourceFactory.createStatement(subject, predicate, object);
    Predicate preparedPredicate = factPreprocessor.generatePredicate(fact);

    // pre-process paths in file
    Collection<QRestrictedPath> paths = metaPreprocessor.processMetaPaths(fact);

    // search for paths if preprocessed paths can't be found
    if (paths == null) {
      LOGGER.warn("Couldn't find the files for paths of {} in {}. Switching to path search.",
          predicate.getURI());
      paths = pathSearcher.search(subject, preparedPredicate, object);
    }

    // return default score if no paths are found
    if (paths.isEmpty()) {
      return new FactCheckingResult(pathsNotFoundResult, paths, fact);
    }

    // calculate scores and verbalize if needed only
    paths = paths.parallelStream().filter(pathFilter).map(p -> {
      if (Double.isNaN(p.getScore())) {
        LOGGER.warn("Couldn't find scores for paths of predicate {}. Executing path scoring.",
            predicate.getURI());
        return pathScorer.score(subject, preparedPredicate, object, p);
      } else {
        return p; 
      }
    }).filter(p -> scoreFilter.test(p.getScore())).collect(Collectors.toList());

    // Get the scores
    double[] scores = paths.stream().mapToDouble(p -> p.getScore()).toArray();

    // Summarize the scores
    double veracity = summarist.summarize(scores);

    return new FactCheckingResult(veracity, paths, fact);
  }



}
