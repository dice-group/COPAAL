package org.dice.fact_check.corraborative.sum;

/**
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class HigherOrderMeanSummarist implements ScoreSummarist {

    @Override
    public double summarize(double[] scores) {
        double score = 0;
        double temp;
        for (int s = scores.length - 1; s >= 0; s--) {
            if (scores[s] >= 0) {
                temp = Math.min(scores[s], 1.0);
            } else {
                temp = Math.max(scores[s], -1.0);
            }
            score += Math.pow(temp, 9.0);
        }
        score /= scores.length;
        return (score < 0 ? -1 : 1) * Math.pow(Math.abs(score), 1.0 / 9.0);
    }

}
