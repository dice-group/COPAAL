package org.dice.FactCheck.Corraborative.filter.npmi;

/**
 * A simple interface to filter NPMI values.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface NPMIFilter {

    /**
     * Checks whether the calculated NPMI value is useful.
     * 
     * @param npmi                            the NPMI value
     * @param pathLength                      the length of the path
     * @param count_path_Predicate_Occurrence the co-occurrence count of the
     *                                        predicate and the path
     * @param count_Path_Occurrence           the count of the path
     * @param count_predicate_Occurrence      the count of the predicate
     * @return {@code true} if the NPMI value should be useful, otherwise
     *         {@code false}
     */
    boolean npmiIsOk(double npmi, int pathLength, double count_path_Predicate_Occurrence, double count_Path_Occurrence,
            int count_predicate_Occurrence);
}
