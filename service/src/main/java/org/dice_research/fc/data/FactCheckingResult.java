package org.dice_research.fc.data;

import java.util.Collection;
import org.apache.jena.rdf.model.Statement;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

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
  private Collection<? extends IPieceOfEvidence> piecesOfEvidence;

  /**
   * The fact we just checked
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Statement fact;

  public FactCheckingResult(double veracityValue,
      Collection<? extends IPieceOfEvidence> piecesOfEvidence, Statement fact) {
    super();
    this.veracityValue = veracityValue;
    this.piecesOfEvidence = piecesOfEvidence;
    this.fact = fact;
  }

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
  public Collection<? extends IPieceOfEvidence> getPiecesOfEvidence() {
    return piecesOfEvidence;
  }

  /**
   * @param piecesOfEvidence the piecesOfEvidence to set
   */
  public void setPiecesOfEvidence(Collection<? extends IPieceOfEvidence> piecesOfEvidence) {
    this.piecesOfEvidence = piecesOfEvidence;
  }

  /**
   * @return the fact
   */
  public Statement getFact() {
    return fact;
  }

  /**
   * @param fact the fact we just checked
   */
  public void setFact(Statement fact) {
    this.fact = fact;
  }
  
}
