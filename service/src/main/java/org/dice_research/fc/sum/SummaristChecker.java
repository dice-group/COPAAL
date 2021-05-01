package org.dice_research.fc.sum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple main method that takes one of the {@link ScoreSummarist}
 * implementations and prints the results for combinations of two scores to the
 * output.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class SummaristChecker {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SummaristChecker.class);

    public static void main(String[] args) {
        ScoreSummarist summarist;

        summarist = new FixedSummarist();

        double stepSize = 0.02;
        double start = -1.0;
        double end = 1.0;

        double[] scores = new double[2];
        int stepx = 0;
        int stepy;

        scores[0] = start;
        while (scores[0] <= end) {
            stepy = 0;
            scores[1] = start;
            while (scores[1] <= end) {
                LOGGER.info(String.format("%1.2f,%1.2f,%1.2f", scores[0], scores[1], summarist.summarize(scores)));
                ++stepy;
                scores[1] = start + (stepy * stepSize);
            }
            ++stepx;
            scores[0] = start + (stepx * stepSize);
        }
    }
}
