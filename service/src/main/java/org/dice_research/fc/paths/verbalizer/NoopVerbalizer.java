package org.dice_research.fc.paths.verbalizer;

import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.IPieceOfEvidence;

/**
 * No-op verbalizer.
 * 
 * @author Alexandra Silva
 *
 */
public class NoopVerbalizer implements IPathVerbalizer {

  private static final String NO_VERBALIZATION = "Verbalization is disabled.";

  @Override
  public String verbalizePaths(Resource subject, Resource object, IPieceOfEvidence path) {
    return NO_VERBALIZATION;
  }

}
