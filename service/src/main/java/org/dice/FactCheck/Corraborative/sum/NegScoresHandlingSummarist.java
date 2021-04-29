package org.dice.FactCheck.Corraborative.sum;

/**
 * An improved handling of scores that takes care of negative scores.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class NegScoresHandlingSummarist implements ScoreSummarist {

    @Override
    public double summarize(double[] scores) {
        double posScore = 1;
        double negScore = 1;
        double score;
        for (int s = scores.length - 1; s >= 0; s--) {
            score = scores[s];
            if(score >= 0) {
                posScore *= 1 - Math.min(score, 1.0);
            } else {
                negScore *= 1 + Math.max(score, -1.0);
            }
        }
        return (1 - posScore) * negScore;
    }

}
