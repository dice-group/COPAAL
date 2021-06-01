package org.dice_research.fc.data;

import java.util.ArrayList;
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
  protected double score = 0;
  /**
   * The elements of this path as {@link Pair}s. The first part of the pair is the IRI of the
   * property. The second part represents whether the property is inverted, i.e., if the second part
   * is {@code true} the property can be used as it is. If the second part is {@code false}, the
   * property has to be inverted.
   */
  protected List<Pair<Property, Boolean>> pathElements;

  /**
   * Constructor.
   */
  public QRestrictedPath() {
    this.pathElements = new ArrayList<Pair<Property, Boolean>>();
  }

  /**
   * Constructor.
   * 
   * @param The elements of this path as {@link Pair}s
   */
  public QRestrictedPath(List<Pair<Property, Boolean>> pathElements) {
    super();
    this.pathElements = pathElements;
  }
  
  /**
   * Constructor.
   * 
   * @param The elements of this path as {@link Pair}s
   * @param The path's veracity score
   */
  public QRestrictedPath(List<Pair<Property, Boolean>> pathElements, double score) {
    super();
    this.pathElements = pathElements;
    this.score = score;
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
    if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score))
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

  /**
   * 
   * @return the equivalent SPARQL property path that can be used in queries
   */
  public String getPropertyPath() {
    StringBuilder builder = new StringBuilder();
    boolean first = true;
    for (Pair<Property, Boolean> element : pathElements) {
      if (first) {
        first = false;
      } else {
        builder.append("/");
      }
      if (!element.getSecond()) {
        builder.append("^");
      }
      builder.append("<").append(element.getFirst().getURI()).append(">");
    }
    return builder.toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    boolean first = true;
    for (Pair<Property, Boolean> element : pathElements) {
      if (first) {
        first = false;
      } else {
        builder.append(",");
      }
      if (!element.getSecond()) {
        builder.append("^");
      }
      builder.append(element.getFirst().getURI());
    }
    builder.append("]");
    return builder.toString();
  }

  public static QRestrictedPath create(Property properties[], boolean direction[]) {
    if (properties.length != direction.length) {
      throw new IllegalArgumentException("The length of the properties array (" + properties.length
          + ") has to match the length of the direction array (" + direction.length + ").");
    }
    List<Pair<Property, Boolean>> pathElements = new ArrayList<>(properties.length);
    for (int i = 0; i < properties.length; ++i) {
      pathElements.add(new Pair<Property, Boolean>(properties[i], direction[i]));
    }
    return new QRestrictedPath(pathElements);
  }
}
