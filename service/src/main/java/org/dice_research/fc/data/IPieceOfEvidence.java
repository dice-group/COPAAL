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
   * A piece of evidence should have some kind of score that shows its strength.
   * 
   * @return the score of this piece of evidence
   */
  double getScore();
  
  /**
   * A piece of evidence should have a preferred representation, 
   * eg. a property path. It is used to serialize the output.
   * 
   * @return The evidence's representation
   */
  String getEvidence();
}
