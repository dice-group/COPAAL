package org.dice_research.fc.paths.verbalizer;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.jena.graph.Node;

/**
 * Class to represent a SPARQL query variable
 * 
 * @author Alexandra Silva
 *
 */
public class Variable {

  /**
   * Variable name
   */
  private String name;
  /**
   * Possible values the variable represents
   */
  private LinkedHashSet<Node> values;
  /**
   * Subset of predicates that can generate the set of values
   */
  private Set<Node> predicates;
  /**
   * Is the variable in the subject position
   */
  private boolean isSubject;

  /**
   * Constructor
   * 
   * @param name Variable name
   */
  public Variable(String name) {
    this.name = name;
    values = new LinkedHashSet<>();
    predicates = new HashSet<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LinkedHashSet<Node> getValues() {
    return values;
  }

  public void addValue(Node value) {
    values.add(value);
  }

  public void addValues(Variable var) {
    values.addAll(var.getValues());
  }

  public void setValues(LinkedHashSet<Node> values) {
    this.values = values;
  }

  public Set<Node> getPredicates() {
    return predicates;
  }

  public void setPredicates(Set<Node> predicates) {
    this.predicates = predicates;
  }

  public void addPredicate(Node value) {
    predicates.add(value);
  }

  public boolean isSubject() {
    return isSubject;
  }

  public void setSubject() {
    this.isSubject = true;
  }

  public void setObject() {
    this.isSubject = false;
  }
  
  public boolean containsPredicate(Node predicate) {
    return predicates.contains(predicate);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    Variable other = (Variable) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}