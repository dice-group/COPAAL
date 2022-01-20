package org.dice_research.fc.sum;

import org.springframework.stereotype.Component;

/**
 * This class implements the original sum strategy of the 2019 paper.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@Component
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
