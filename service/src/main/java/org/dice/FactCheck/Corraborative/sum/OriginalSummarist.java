package org.dice.FactCheck.Corraborative.sum;

import java.util.List;

/**
 * This class implements the original sum strategy of the 2019 paper.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class OriginalSummarist implements ScoreSummarist {

    @Override
    public double summarize(List<Double> scores) {
        double score = 1.0;
        for (int s = scores.size() - 1; s >= 0; s--) {
            if (scores.get(s) > 1) continue;
            score = score * (1 - scores.get(s));
        }
        return 1 - score;
    }
    
}
