package org.dice_research.fc.paths.scorer;

import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.IPropertyBasedPathScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * An NPMI-based path scorer as described in the COPAAL paper.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@Component
public class NPMIBasedScorer implements IPropertyBasedPathScorer {
  private static final Logger LOGGER = LoggerFactory.getLogger(NPMIBasedScorer.class);
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
  protected double pathDoesNotExistResult = 0.0;
  /**
   * The score if a property does not exist in the reference knowledge graph.
   */
  protected double propertyDoesNotExistResult = 0.0;
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
  
  @Autowired
  public NPMIBasedScorer(ICountRetriever countRetriever) {
    super();
    this.countRetriever = countRetriever;
    LOGGER.trace("Count retriever is {}",countRetriever.getClass().getName());
  }

  @Override
  public QRestrictedPath score(Predicate predicate, QRestrictedPath path) {
    double score = calculateScore(predicate, path);
    path.setScore(score);
    return path;
  }

  public double calculateScore(Predicate predicate, QRestrictedPath path) {
    LOGGER.debug("Start calculating score for this predicate : {} and this QRestrictedPath : {}",predicate.getProperty().getURI(),path.toString());
    long pathCounts = countRetriever.countPathInstances(path, predicate.getDomain(), predicate.getRange());
    LOGGER.debug("pathCounts is : {}",pathCounts);
    if (pathCounts == 0) {
      return pathDoesNotExistResult;
    }
    long predicateCounts = countRetriever.countPredicateInstances(predicate);
    LOGGER.debug("predicateCounts is : {}",predicateCounts);
    if (predicateCounts == 0) {
      return propertyDoesNotExistResult;
    }
    long cooccurrenceCounts = countRetriever.countCooccurrences(predicate, path);
    LOGGER.debug("cooccurrenceCounts is : {}",cooccurrenceCounts);
    if (cooccurrenceCounts == 0) {
      return noCooccurrenceResult;
    }
    long maxCount = countRetriever.deriveMaxCount(predicate);
    LOGGER.debug("maxCount is : {}",maxCount);

    return calculateScore(pathCounts, predicateCounts, cooccurrenceCounts, maxCount);
  }

  protected double calculateScore(double pathCounts, double predicateCounts,
      double cooccurrenceCounts, double deriveMaxCount) {
    // P - probability, c - count
    // PMI (without log) = P(p,path) / P(p)*P(path)
    // = (c(p,path)/c(max)) / (c(p)/c(max))*(c(path)/c(max))
    // = c(p,path)*c(max) / c(p)*(c(path)
    LOGGER.debug("npmi calculated with this  = Math.log(({} * {}) / ({} * {})) / -Math.log({} / {})",cooccurrenceCounts,deriveMaxCount,predicateCounts,pathCounts,cooccurrenceCounts,deriveMaxCount);
    double npmi = Math.log((cooccurrenceCounts * deriveMaxCount) / (predicateCounts * pathCounts))
        / -Math.log(cooccurrenceCounts / deriveMaxCount);
    LOGGER.debug("calculated npmi is : {}",npmi);
    LOGGER.trace("maxResult is : {}, minResult is : {}",maxResult,minResult);
    if (npmi > maxResult) {
      return maxResult;
    } else if (npmi < minResult) {
      return minResult;
    } else {
      return npmi;
    }
  }

  /**
   * @return the maxResult
   */
  public double getMaxResult() {
    return maxResult;
  }

  /**
   * @param maxResult the maxResult to set
   */
  public void setMaxResult(double maxResult) {
    this.maxResult = maxResult;
  }

  /**
   * @return the minResult
   */
  public double getMinResult() {
    return minResult;
  }

  /**
   * @param minResult the minResult to set
   */
  public void setMinResult(double minResult) {
    this.minResult = minResult;
  }

  /**
   * @return the pathDoesNotExistResult
   */
  public double getPathDoesNotExistResult() {
    return pathDoesNotExistResult;
  }

  /**
   * @param pathDoesNotExistResult the pathDoesNotExistResult to set
   */
  public void setPathDoesNotExistResult(double pathDoesNotExistResult) {
    this.pathDoesNotExistResult = pathDoesNotExistResult;
  }

  /**
   * @return the propertyDoesNotExistResult
   */
  public double getPropertyDoesNotExistResult() {
    return propertyDoesNotExistResult;
  }

  /**
   * @param propertyDoesNotExistResult the propertyDoesNotExistResult to set
   */
  public void setPropertyDoesNotExistResult(double propertyDoesNotExistResult) {
    this.propertyDoesNotExistResult = propertyDoesNotExistResult;
  }

  /**
   * @return the noCooccurrenceResult
   */
  public double getNoCooccurrenceResult() {
    return noCooccurrenceResult;
  }

  /**
   * @param noCooccurrenceResult the noCooccurrenceResult to set
   */
  public void setNoCooccurrenceResult(double noCooccurrenceResult) {
    this.noCooccurrenceResult = noCooccurrenceResult;
  }

  /**
   * @return the typesAreEmptyResult
   */
  public double getTypesAreEmptyResult() {
    return typesAreEmptyResult;
  }

  /**
   * @param typesAreEmptyResult the typesAreEmptyResult to set
   */
  public void setTypesAreEmptyResult(double typesAreEmptyResult) {
    this.typesAreEmptyResult = typesAreEmptyResult;
  }

  /**
   * @return the countRetriever
   */
  public ICountRetriever getCountRetriever() {
    return countRetriever;
  }

  /**
   * @param countRetriever the countRetriever to set
   */
  public void setCountRetriever(ICountRetriever countRetriever) {
    this.countRetriever = countRetriever;
  }

}
