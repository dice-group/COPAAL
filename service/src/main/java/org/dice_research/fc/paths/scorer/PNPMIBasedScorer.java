package org.dice_research.fc.paths.scorer;

/**
 * An PNPMI-based path scorer.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class PNPMIBasedScorer extends NPMIBasedScorer {

  public PNPMIBasedScorer(ICountRetriever countRetriever) {
    super(countRetriever);
    minResult = 0;
  }
  
}
