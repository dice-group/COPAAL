package org.dice_research.fc.paths.ext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.FactPreprocessor;
import org.dice_research.fc.paths.IPathSearcher;
import org.dice_research.fc.paths.IPropertyBasedPathScorer;
import org.dice_research.fc.paths.filter.AlwaysTruePathFilter;
import org.dice_research.fc.paths.filter.AlwaysTrueScoreFilter;
import org.dice_research.fc.paths.filter.IPathFilter;
import org.dice_research.fc.paths.filter.IScoreFilter;

/**
 * Default implementation of the {@link IPathExtractor} interface.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class SamplingPathExtractor implements IPathExtractor {

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
  protected IPropertyBasedPathScorer pathScorer;
  /**
   * A class that can be used to filter path scores.
   */
  protected IScoreFilter scoreFilter = new AlwaysTrueScoreFilter();

  /**
   * Constructor.
   * 
   * @param factPreprocessor The preprocessor used to prepare the given fact.
   * @param pathSearcher The class that is used to search for (corroborative) paths.
   * @param pathScorer The path scorer that is used to score the single paths.
   * @param summarist The class that is used to summarize the scores of the single paths to create a
   *        final score.
   */
  public SamplingPathExtractor(FactPreprocessor factPreprocessor, IPathSearcher pathSearcher,
      IPropertyBasedPathScorer pathScorer) {
    this.factPreprocessor = factPreprocessor;
    this.pathSearcher = pathSearcher;
    this.pathScorer = pathScorer;
  }

  @Override
  public List<QRestrictedPath> extract(String propertyURI) {

    // TODO sample a set of triples that have the given property as predicate
    List<Triple> triples = null;

    Set<QRestrictedPath> paths = new HashSet<>();
    Resource subject;
    Resource object;
    Statement fact;
    Predicate preparedPredicate = null;
    for (Triple triple : triples) {
      if (triple.getSubject().isURI() && triple.getObject().isURI()) {
        subject = ResourceFactory.createResource(triple.getSubject().getURI());
        object = ResourceFactory.createResource(triple.getObject().getURI());

        // Preprocess the data
        fact = ResourceFactory.createStatement(subject, ResourceFactory.createProperty(propertyURI),
            object);
        preparedPredicate = factPreprocessor.generatePredicate(fact);

        // Get potential paths
        paths.addAll(pathSearcher.search(subject, preparedPredicate, object));
      }
    }
    
    final Predicate lastPreparedPredicate = preparedPredicate;

    // Filter paths, score the paths with respect to the given triple and filter them again based on
    // the score
    return paths.parallelStream().filter(pathFilter)
        .map(p -> pathScorer.score(lastPreparedPredicate, p))
        .filter(p -> scoreFilter.test(p.getScore())).collect(Collectors.toList());
  }
}
