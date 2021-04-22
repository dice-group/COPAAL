package org.dice_research.fc.paths;

import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Property;

/**
 * Classes implementing this interface can derive counts for paths, properties
 * and typed entities.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface ICountRetriever {

	/**
	 * Retrieves the count of the given path.
	 * 
	 * @param path the path for which the instances should be counted
	 * @return the count of the path in the reference graph
	 */
	int countPathInstances(Object path);

	/**
	 * Retrieves the count for the given predicate.
	 * 
	 * @param predicate the predicate that should be counted
	 * @return the count of the path in the reference graph
	 */
	int countPredicateInstances(Property predicate);

	/**
	 * Retrieves the count for the co-occurrence of the given path and predicate
	 * 
	 * @param predicate the predicate that should be counted
	 * @param path      the path for which the instances should be counted
	 * @return the count of the path in the reference graph
	 */
	int countPredicateInstances(String predicate, Object path);

	/**
	 * Derives a maximum count that can be used to create probabilities from the
	 * counts retrieved by the other methods of the {@link ICountRetriever}
	 * interface.
	 * 
	 * @param predicate the predicate for which the counts should be normalized
	 * @return a maximum count
	 */
	int deriveMaxCount(String predicate);

	/**
	 * Retrieves the count of how many resources share the same types as the subject
	 * 
	 * @param types     the set of types we want to check for
	 * @param predicate the predicate of the input triple
	 * @return the count of triples with the same type
	 */
	int countTriplesSameTypeSubj(Set<Node> types, Property predicate);

	/**
	 * Retrieves the count of how many resources share the same types as the subject
	 * 
	 * @param types     the set of types we want to check for
	 * @param predicate the predicate of the input triple
	 * @return the count of triples with the same type
	 */
	int countTriplesSameTypeObj(Set<Node> types, Property predicate);

}
