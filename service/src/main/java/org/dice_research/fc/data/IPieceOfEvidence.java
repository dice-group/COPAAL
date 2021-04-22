package org.dice_research.fc.data;

/**
 * A piece of evidence that is used to proof or refute a fact. 
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface IPieceOfEvidence {

  /**
   * A piece of evidence should have some kind of score that shows its strength.
   * 
   * @return the score of this piece of evidence
   */
  double getScore();
}
