package org.dice_research.fc.sparql.restrict;

import java.util.Set;
import org.apache.jena.vocabulary.RDF;

/**
 * A type restriction which is based on a set of given type IRIs.
 *
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class TypeBasedRestriction implements ITypeRestriction {

  /**
   * The set of types a resource should have.
   */
  protected Set<String> types;
  /**
   * The IRI of the property representing the type relation. As default value, {@link RDF#type} is
   * used.
   */
  protected String typeIRI;

  public TypeBasedRestriction(Set<String> types) {
    this(types, RDF.type.getURI());
  }

  public TypeBasedRestriction(Set<String> types, String typeIRI) {
    this.types = types;
    this.typeIRI = typeIRI;
  }

  @Override
  public void addRestrictionToQuery(String variable, StringBuilder builder) {
    for (String type : types) {
      builder.append(" ?");
      builder.append(variable);
      builder.append(" <");
      builder.append(typeIRI);
      builder.append("> <");
      builder.append(type);
      builder.append("> . ");
    }
  }

  @Override
  public boolean isEmpty() {
    return types.isEmpty();
  }

  @Override
  public Object getRestriction() {
    return this.types;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((typeIRI == null) ? 0 : typeIRI.hashCode());
    result = prime * result + ((types == null) ? 0 : types.hashCode());
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
    TypeBasedRestriction other = (TypeBasedRestriction) obj;
    if (typeIRI == null) {
      if (other.typeIRI != null)
        return false;
    } else if (!typeIRI.equals(other.typeIRI))
      return false;
    if (types == null) {
      if (other.types != null)
        return false;
    } else if (!types.equals(other.types))
      return false;
    return true;
  }

  @Override
  public boolean usesPropertyAsRestriction() {
    return false;
  }
}
