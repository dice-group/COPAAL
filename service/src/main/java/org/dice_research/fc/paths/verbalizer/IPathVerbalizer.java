package org.dice_research.fc.paths.verbalizer;

import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.QRestrictedPath;

/**
 * Implementations of this interface should describe the retrieval of the verbalized output.
 * 
 * @author Alexandra Silva
 *
 */
public interface IPathVerbalizer {

  /**
   * Returns the verbalized output of a given path.
   * 
   * @param subject The given fact's subject.
   * @param object The given fact's object.
   * @param path The path between subject and object.
   */
  String verbalizePaths(Resource subject, Resource object, QRestrictedPath path);

}
