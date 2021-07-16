package org.dice_research.fc.paths;

import java.util.Collection;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.IFactChecker;
import org.dice_research.fc.data.FactCheckingResult;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.filter.AlwaysTruePathFilter;
import org.dice_research.fc.paths.filter.IPathFilter;
import org.dice_research.fc.paths.imprt.MetaPathsProcessor;
import org.dice_research.fc.sum.ScoreSummarist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Similar to {@link PathBasedFactChecker}, but foregoes the search and scoring of individual paths
 * (as it loads these from file).
 * 
 * @author Alexandra Silva
 *
 */
@Component
public class ImportedFactChecker implements IFactChecker {
  /**
   * The class that is used to summarize the scores of the single paths to create a final score.
   */
  protected ScoreSummarist summarist;
  /**
   * The score if no paths were found.
   */
  protected double pathsNotFoundResult = 0;
  /**
   * A class that can be used to filter paths.
   */
  protected IPathFilter pathFilter = new AlwaysTruePathFilter();
  /**
   * The imported facts processor
   */
  protected MetaPathsProcessor metaPreprocessor;


  @Autowired
  public ImportedFactChecker(MetaPathsProcessor metaPreProcessor, ScoreSummarist summarist) {
    this.summarist = summarist;
    this.metaPreprocessor = metaPreProcessor;
  }

  @Override
  public FactCheckingResult check(Resource subject, Property predicate, Resource object) {
    Statement fact = ResourceFactory.createStatement(subject, predicate, object);
    
    // pre-process paths in file if needed
    Collection<QRestrictedPath> paths = metaPreprocessor.processMetaPaths(fact);
    if (paths.isEmpty()) {
      return new FactCheckingResult(pathsNotFoundResult, paths, fact);
    }

    // Get the scores
    double[] scores = paths.stream().mapToDouble(p -> p.getScore()).toArray();

    // Summarize the scores
    double veracity = summarist.summarize(scores);

    return new FactCheckingResult(veracity, paths, fact);
  }



}
