package org.dice_research.fc.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

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
  protected double score = Double.NaN;
  /**
   * The elements of this path as {@link Pair}s. The first part of the pair is the IRI of the
   * property. The second part represents whether the property is inverted, i.e., if the second part
   * is {@code true} the property can be used as it is. If the second part is {@code false}, the
   * property has to be inverted.
   */
  protected List<Pair<Property, Boolean>> pathElements;

  /**
   * The verbalized output.
   */
  protected String verbalizedOutput;

  /**
   * one path sample
   */
  protected String sample;

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

  /**
   * @return the verbalized output
   */
  @Override
  public String getVerbalizedOutput() {
    return verbalizedOutput;
  }

  /**
   * @param verbalizedOutput the verbalized output to set
   */
  @Override
  public void setVerbalizedOutput(String verbalizedOutput) {
    this.verbalizedOutput = verbalizedOutput;
  }

  /**
   * @return the sample
   */
  @Override
  public String getSample() {
    return sample;
  }

  /**
   * @param sample the sample of a path
   */
  @Override
  public void setSample(String sample) {
    this.sample = sample;
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
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (getClass() != obj.getClass()) {
      return false;
    }

    QRestrictedPath other = (QRestrictedPath) obj;

    if (pathElements == null) {
      if (other.pathElements != null){
        return false;
      }
    }
    else {
      if (!pathElements.equals(other.pathElements)) {
        return false;
      }
    }

    return Double.doubleToLongBits(score) == Double.doubleToLongBits(other.score);
  }



  public int length() {
    if (pathElements != null) {
      return pathElements.size();
    } else {
      return 0;
    }
  }

  /**
   * @return The corresponding SPARQL property path.
   */
  @Override
  public String getEvidence() {
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
      builder.append("<");
      builder.append(element.getFirst().getURI());
      builder.append(">");
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

  public String toStringWithTag() {
    StringBuilder sb = new StringBuilder();
    for(Pair<Property, Boolean> p : pathElements){
      if(!p.getSecond()){
        sb.append("^");
      }
      sb.append("<");
      sb.append(p.getFirst().getURI());
      sb.append(">");
    }
    return sb.toString();
  }

  public static QRestrictedPath toQRestrictedPathFromStringWithTag(String path){
    QRestrictedPath temp = new QRestrictedPath();
    String[] parts = path.split(">");
    temp.pathElements = new ArrayList<Pair<Property, Boolean>>();
    for(int i = 0 ; i < parts.length ; i++) {
      parts[i] = parts[i].replace("<","");
      if(parts[i].charAt(0) == '^'){
        parts[i] = parts[i].replace("^","");
        temp.pathElements.add(new Pair<>(ResourceFactory.createProperty(parts[i]) , false));
      }else{
        temp.pathElements.add(new Pair<>(ResourceFactory.createProperty(parts[i]), true));
      }
    }
    return temp;
  }

  public String queryForGetAnExample(){
    StringBuilder sb = new StringBuilder();
    sb.append("select * where { ");
    for(int i = 0 ; i < this.pathElements.size() ; i++){
      Pair<Property, Boolean> current = this.pathElements.get(i);

      String first = "?x" + i;
      String second = "?x" + (i+1);

      if(!current.getSecond()){
        // is false swap first and second
        first = "?x" + (i+1);
        second = "?x" + i;
      }
      sb.append(first+" <"+ current.getFirst().getURI()+"> " + second+" . ");
    }
    sb.append(" } LIMIT 1");
    return sb.toString();
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


  /**
   * Creates a new {@link QRestrictedPath} object from a property path and a score.
   * 
   * @param propertyPath
   * @param score
   * @return
   */
  public static QRestrictedPath create(String propertyPath, double score) {

    // The REGEX to extract the property path and inverse operator from.
    final String PROPERTY_PATH_REGEX = "(\\^?)<(.+?)>";

    List<Pair<Property, Boolean>> pathElements = new ArrayList<Pair<Property, Boolean>>();

    Pattern pattern = Pattern.compile(PROPERTY_PATH_REGEX);
    Matcher matcher = pattern.matcher(propertyPath);
    while (matcher.find()) {
      String operator = matcher.group(1);
      String uri = matcher.group(2);

      // can we deal with the property as is or is it inverse
      boolean isAsIs = operator.contains("^") ? false : true;

      Property property = ResourceFactory.createProperty(uri);
      pathElements.add(new Pair<Property, Boolean>(property, isAsIs));
    }

    return new QRestrictedPath(pathElements, score);

  }

  /**
   * Creates a new {@link QRestrictedPath} object from a a property path, a score and the verbalized
   * output.
   * 
   * @param propertyPath
   * @param verbalizedOutput
   * @param score
   * @return
   */
  public static QRestrictedPath create(String propertyPath, String verbalizedOutput, double score) {
    QRestrictedPath path = create(propertyPath, score);
    path.setVerbalizedOutput(verbalizedOutput);
    return path;

  }
}
