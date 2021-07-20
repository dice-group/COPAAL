package org.dice_research.fc.paths.imprt;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    // pre-process paths in file if needed
    Collection<QRestrictedPath> paths = metaPreprocessor.processMetaPaths(fact);
    if (paths.isEmpty()) {
      return new FactCheckingResult(pathsNotFoundResult, paths, fact);
    }

    // calculate scores if needed only
    Stream<QRestrictedPath> pathStream = paths.stream();
    if (pathStream.findFirst().get().getScore() == Double.NaN) {
      Predicate preparedPredicate = factPreprocessor.generatePredicate(fact);
      paths = paths.parallelStream().filter(pathFilter)
          .map(p -> pathScorer.score(subject, preparedPredicate, object, p))
          .filter(p -> scoreFilter.test(p.getScore())).collect(Collectors.toList());
    }

    // Get the scores
    double[] scores = paths.stream().mapToDouble(p -> p.getScore()).toArray();

    // Summarize the scores
    double veracity = summarist.summarize(scores);

    return new FactCheckingResult(veracity, paths, fact);
  }



}
