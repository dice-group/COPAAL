package org.dice_research.fc.paths.scorer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * An PNPMI-based path scorer.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@Component
public class PNPMIBasedScorer extends NPMIBasedScorer {

  @Autowired
  public PNPMIBasedScorer(ICountRetriever countRetriever) {
    super(countRetriever);
    minResult = 0;
  }
  
}
