package org.dice_research.fc.paths.scorer;

import java.util.Set;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;

/**
 * Classes implementing this interface can derive counts for paths, properties and typed entities.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface ICountRetriever {

  /**
   * Retrieves the count of the given path.
   * 
   * @param path the path for which the instances should be counted
   * @param domainRestriction the restriction for the domain of the path
   * @param rangeRestriction the restriction for the range of the path
   * @return the count of the path in the reference graph
   */
  int countPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction, ITypeRestriction rangeRestriction);

  /**
   * Retrieves the count for the given predicate.
   * 
   * @param predicate the predicate that should be counted
   * @return the count of the path in the reference graph
   */
  int countPredicateInstances(Predicate predicate);

  /**
   * Retrieves the count for the co-occurrence of the given path and predicate
   * 
   * @param predicate the predicate that should be counted
   * @param path the path for which the instances should be counted
   * @return the count of the path in the reference graph
   */
  int countCooccurrences(Predicate predicate, QRestrictedPath path);

  /**
   * Derives a maximum count that can be used to create probabilities from the counts retrieved by
   * the other methods of the {@link ICountRetriever} interface.
   * 
   * @param predicate the predicate for which the counts should be normalized
   * @return a maximum count
   */
  int deriveMaxCount(Resource subject, Predicate predicate, Resource Object);

  /**
   * Retrieves the count of how many resources share the same types as the subject
   * 
   * @param types the set of types we want to check for
   * @param predicate the predicate of the input triple
   * @return the count of triples with the same type
   * 
   * @deprecated The counter decides internally how he handles deriving the maximum count in {@link #deriveMaxCount(Property)}
   */
  @Deprecated
  int countTriplesSameTypeSubj(Set<Node> types, Property predicate);

  /**
   * Retrieves the count of how many resources share the same types as the subject
   * 
   * @param types the set of types we want to check for
   * @param predicate the predicate of the input triple
   * @return the count of triples with the same type
   * 
   * @deprecated The counter decides internally how he handles deriving the maximum count in {@link #deriveMaxCount(Property)}
   */
  @Deprecated
  int countTriplesSameTypeObj(Set<Node> types, Property predicate);

}
