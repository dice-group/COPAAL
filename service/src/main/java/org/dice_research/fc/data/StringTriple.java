package org.dice_research.fc.data;

import java.util.Objects;

/**
 * A very simple, {@link String}-based representation of a triple.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class StringTriple {

  /**
   * The subject of the triple.
   */
  public String subject;
  /**
   * The predicate of the triple.
   */
  public String predicate;
  /**
   * The object of the triple.
   */
  public String object;

  /**
   * Constructor.
   * 
   * @param subject The subject of the triple.
   * @param predicate The predicate of the triple.
   * @param object The object of the triple.
   */
  public StringTriple(String subject, String predicate, String object) {
    super();
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((object == null) ? 0 : object.hashCode());
    result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
    result = prime * result + ((subject == null) ? 0 : subject.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StringTriple that = (StringTriple) o;
    return Objects.equals(getSubject(), that.getSubject()) &&
            Objects.equals(getPredicate(), that.getPredicate()) &&
            Objects.equals(getObject(), that.getObject());
  }

/*  @Override
  public boolean equalds(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StringTriple other = (StringTriple) obj;
    if (object == null) {
      if (other.object != null)
        return false;
    } else if (!object.equals(other.object))
      return false;
    if (predicate == null) {
      if (other.predicate != null)
        return false;
    } else if (!predicate.equals(other.predicate))
      return false;
    if (subject == null) {
      if (other.subject != null)
        return false;
    } else if (!subject.equals(other.subject))
      return false;
    return true;
  }*/

  /**
   * @return the subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * @param subject the subject to set
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * @return the predicate
   */
  public String getPredicate() {
    return predicate;
  }

  /**
   * @param predicate the predicate to set
   */
  public void setPredicate(String predicate) {
    this.predicate = predicate;
  }

  /**
   * @return the object
   */
  public String getObject() {
    return object;
  }

  /**
   * @param object the object to set
   */
  public void setObject(String object) {
    this.object = object;
  }

}
