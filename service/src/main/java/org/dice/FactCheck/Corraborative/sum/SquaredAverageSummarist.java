package org.dice.FactCheck.Corraborative.sum;

/**
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class SquaredAverageSummarist implements ScoreSummarist {

    @Override
    public double summarize(double[] scores) {
        double score = 0;
        for (int s = scores.length - 1; s >= 0; s--) {
            if (scores[s] >= 0) {
                score += Math.sqrt(Math.min(scores[s], 1.0));
            } else {
                score -= Math.sqrt(Math.min(Math.abs(scores[s]), 1.0));
            }
        }
        return score /= scores.length;
    }

}
