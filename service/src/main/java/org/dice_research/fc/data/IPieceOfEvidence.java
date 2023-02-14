package org.dice_research.fc.data;

import org.dice_research.fc.serialization.PathSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A piece of evidence that is used to proof or refute a fact.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@JsonSerialize(using = PathSerializer.class)
public interface IPieceOfEvidence {
  /**
   * When the implementation of {@link IPieceOfEvidence} does not have a verbalization option.
   */
  String NO_OUTPUT = "Verbalization not available.";

  /**
   * A piece of evidence should have some kind of score that shows its strength.
   * 
   * @return the score of this piece of evidence
   */
  double getScore();

  /**
   * A piece of evidence should have a preferred representation, eg. a property path. It is used to
   * serialize the output.
   * 
   * @return The evidence's representation
   */
  String getEvidence();

  /**
   * A piece of evidence might be verbalized. Getter for the verbalized output.
   * 
   * @return The evidence's verbalized output.
   */
  default String getVerbalizedOutput() {
    return NO_OUTPUT;
  }

  /**
   * A piece of evidence might be verbalized. Setter for the verbalized output.
   * 
   * @param verbalizedPath
   */
  default void setVerbalizedOutput(String verbalizedPath) {
    // no-op
  };

  /**
   * a sample of a path
   *
   * @return a path's sample.
   */
  default String getSample() {
    return NO_OUTPUT;
  }

  /**
   * set a sample of a path
   *
   */
  default void setSample(String sample) {
   // no-op
  }
}
