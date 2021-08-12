package org.dice.fact_check.corraborative.sum;

/**
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class AdaptedRootMeanSquareSummarist implements ScoreSummarist {

    @Override
    public double summarize(double[] scores) {
        double score = 0;
        for (int s = scores.length - 1; s >= 0; s--) {
            if (scores[s] >= 0) {
                score += Math.pow(Math.min(scores[s], 1.0), 2.0);
            } else {
                score -= Math.pow(Math.max(scores[s], -1.0), 2.0);
            }
        }
        score /= scores.length;
        return (score < 0 ? -1 : 1) * Math.sqrt(Math.abs(score));
    }

}
