package org.dice_research.fc.paths.scorer;

import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.IPathScorer;

/**
 * An NPMI-based path scorer as described in the COPAAL paper.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class NPMIBasedScorer implements IPathScorer {

  /**
   * The maximum result returned by this scorer.
   */
  protected double maxResult = 1.0;
  /**
   * The minimum result returned by this scorer.
   */
  protected double minResult = -1.0;
  /**
   * The score if a path does not exist in the reference knowledge graph.
   */
  protected double pathDoesNotExistResult = 0;
  /**
   * The score if a property does not exist in the reference knowledge graph.
   */
  protected double propertyDoesNotExistResult = 0;
  /**
   * The score if a path and a property do not co-occur in the reference knowledge graph.
   */
  protected double noCooccurrenceResult = -0.1;
  /**
   * The score if there are no types for subject or object.
   */
  protected double typesAreEmptyResult = 0.1;
  /**
   * The class used to derive the counts
   */
  protected ICountRetriever countRetriever;

  @Override
  public QRestrictedPath score(Resource subject, Predicate predicate, Resource Object,
      QRestrictedPath path) {
    double score = calculateScore(subject, predicate, Object, path);
    path.setScore(score);
    return path;
  }

  public double calculateScore(Resource subject, Predicate predicate, Resource object,
      QRestrictedPath path) {
    int pathCounts = countRetriever.countPathInstances(path, predicate.getDomain(), predicate.getRange());
    if (pathCounts == 0) {
      return pathDoesNotExistResult;
    }
    int predicateCounts = countRetriever.countPredicateInstances(predicate);
    if (predicateCounts == 0) {
      return propertyDoesNotExistResult;
    }
    int cooccurrenceCounts = countRetriever.countCooccurrences(predicate, path);
    if (cooccurrenceCounts == 0) {
      return noCooccurrenceResult;
    }
    int maxCount = countRetriever.deriveMaxCount(subject, predicate, object);
    if (pathCounts == 0) {
      throw new IllegalStateException("The maximum count is 0. That is not supported.");
    }
    return calculateScore(pathCounts, predicateCounts, cooccurrenceCounts, maxCount);
  }

  protected double calculateScore(double pathCounts, double predicateCounts,
      double cooccurrenceCounts, double deriveMaxCount) {
    // P - probability, c - count
    // PMI (without log) = P(p,path) / P(p)*P(path)
    // = (c(p,path)/c(max)) / (c(p)/c(max))*(c(path)/c(max))
    // = c(p,path)*c(max) / c(p)*(c(path)
    double npmi = Math.log((cooccurrenceCounts * deriveMaxCount) / (predicateCounts * pathCounts))
        / Math.log(cooccurrenceCounts / deriveMaxCount);
    if (npmi > maxResult) {
      return maxResult;
    } else if (npmi < minResult) {
      return minResult;
    } else {
      return npmi;
    }
  }

}