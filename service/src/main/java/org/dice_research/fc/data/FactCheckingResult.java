package org.dice_research.fc.data;

import java.util.Collection;

/**
 * The result of a fact checker.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class FactCheckingResult {
  
  /**
   * The main result of the fact checking is a veracity value
   */ 
  private double veracityValue;
  
  /**
   * The pieces of evidence that have been used to come to the veracity value
   */
  private Collection<IPieceOfEvidence> piecesOfEvidence;

  // TODO Add fact that has been checked

  /**
   * @return the veracityValue
   */
  public double getVeracityValue() {
    return veracityValue;
  }

  /**
   * @param veracityValue the veracityValue to set
   */
  public void setVeracityValue(double veracityValue) {
    this.veracityValue = veracityValue;
  }

  /**
   * @return the piecesOfEvidence
   */
  public Collection<IPieceOfEvidence> getPiecesOfEvidence() {
    return piecesOfEvidence;
  }

  /**
   * @param piecesOfEvidence the piecesOfEvidence to set
   */
  public void setPiecesOfEvidence(Collection<IPieceOfEvidence> piecesOfEvidence) {
    this.piecesOfEvidence = piecesOfEvidence;
  }
  
  
}
