package org.dice_research.fc.paths.verbalizer;


/**
 * Should be used by exceptions thrown by the verbalization function.
 * 
 * @author Alexandra Silva
 *
 */
public class VerbalizerException extends Exception {
  
  /**
   * 
   */
  private static final long serialVersionUID = -4168566618160591095L;

  public VerbalizerException() {
  }
  
  public VerbalizerException(String message) {
    super(message);
  }

}
