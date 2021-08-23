package org.dice.fact_check.corraborative.sum;

/**
 * An interface of a class that can be used to summarize the given scores and
 * returns a single score.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface ScoreSummarist {

    /**
     * This method summarizes the given scores to a single score.
     * 
     * @param scores the scores that should be summarized
     * @return the summarized score
     */
    public double summarize(double[] scores);
}
