package org.dice.FactCheck.preprocess.model;

/**
 * This interface is implemented by classes that express a type-based restriction for a variable in
 * a SPARQL query.
 *
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface ITypeRestriction {

    /**
     * When this method is called, the restriction is added to the SPARQL query. It is assumed that
     * the given variable name represents the node for which the type should be restricted and that
     * the given {@link StringBuilder} contains the SPARQL query in a state in which the restriction
     * can be added.
     *
     * @param variable the variable for which the restriction should be generated (Excluding the
     *        {@code '?'} symbol of SPARQL
     * @param builder the unfinished SPARQL query to which the restriction should be added
     */
    public void addRestrictionToQuery(String variable, StringBuilder builder);
}
