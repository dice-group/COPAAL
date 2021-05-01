package org.dice.FactCheck.Corraborative.sum;

import org.springframework.stereotype.Component;

/**
 * This is a fixed version of the {@link OriginalSummarist} used in the paper.
 * It has been suggested by Sven Kuhlmann. It distinguishes between positive and
 * negative scores and calculates a positive and negative confidence. The
 * positive confidence is calculated by subtracting the product of the
 * multiplication of the positive scores as follows:
 * <code>1 - ((1 - s_1) * (1 - s_2) * ...)</code>. The negative confidence is
 * calculated in a similar way with
 * <code>1 - ((1 + s_1) * (1 + s_2) * ...)</code> where the single scores are
 * negative numbers. The overall score is calculated as positive confidence
 * minus negative confidence.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@Component
public class FixedSummarist implements ScoreSummarist {

    @Override
    public double summarize(double[] scores) {
        double posMult = 1;
        double negMult = 1;
        for (int s = scores.length - 1; s >= 0; s--) {
            if (scores[s] >= 0) {
                posMult *= 1 - Math.min(scores[s], 1.0);
            } else {
                negMult *= 1 + Math.max(scores[s], -1.0);
            }
        }
        double posConfidence = 1 - posMult;
        double negConfidence = 1 - negMult;
        return posConfidence - negConfidence;
    }

}
