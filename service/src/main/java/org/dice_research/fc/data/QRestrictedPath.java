package org.dice_research.fc.data;

import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;

/**
 * A q-restricted path as defined in the paper of Syed et al. (2019).
 * 
 * 
 * The elements of this path are represented as {@link Pair}s. The first part of the pair is the IRI
 * of the property. The second part represents whether the property is inverted, i.e., if the second
 * part is {@code true} the property can be used as it is. If the second part is {@code false}, the
 * property has to be inverted.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class QRestrictedPath implements IPieceOfEvidence {

  /**
   * The score of the path
   */
  private double score = 0;
  /**
   * The elements of this path as {@link Pair}s. The first part of the pair is the IRI of the
   * property. The second part represents whether the property is inverted, i.e., if the second part
   * is {@code true} the property can be used as it is. If the second part is {@code false}, the
   * property has to be inverted.
   */
  private List<Pair<Property, Boolean>> pathElements;

  public QRestrictedPath() {}

  public QRestrictedPath(List<Pair<Property, Boolean>> pathElements) {
    super();
    this.pathElements = pathElements;
  }

  @Override
  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }

  /**
   * @return the pathElements
   */
  public List<Pair<Property, Boolean>> getPathElements() {
    return pathElements;
  }

  /**
   * @param pathElements the pathElements to set
   */
  public void setPathElements(List<Pair<Property, Boolean>> pathElements) {
    this.pathElements = pathElements;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((pathElements == null) ? 0 : pathElements.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    QRestrictedPath other = (QRestrictedPath) obj;
    if (pathElements == null) {
      if (other.pathElements != null)
        return false;
    } else if (!pathElements.equals(other.pathElements))
      return false;
    return true;
  }

  public int length() {
    if (pathElements != null) {
      return pathElements.size();
    } else {
      return 0;
    }
  }
}
