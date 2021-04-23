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
}
