package org.dice.fact_check.corraborative.sum;

/**
 * This class implements the original sum strategy of the 2019 paper.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class OriginalSummarist implements ScoreSummarist {

    @Override
    public double summarize(double[] scores) {
        double score = 1.0;
        for (int s = scores.length - 1; s >= 0; s--) {
            if (scores[s] > 1) continue;
            score = score * (1 - scores[s]);
        }
        return 1 - score;
    }
    
}
