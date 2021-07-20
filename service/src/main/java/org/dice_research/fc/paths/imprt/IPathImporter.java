package org.dice_research.fc.paths.imprt;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.data.QRestrictedPath;

/**
 * Classes implementing this interface should describe different methods of importing paths to
 * COPAAL's framework.
 * 
 * @author Alexandra Silva
 *
 */
public interface IPathImporter {

  /**
   * FIXME should be IPieceOfEvidence instead.
   * 
   * @param filePath
   * @return
   * @throws IOException
   */
  Entry<Property, List<QRestrictedPath>> importPaths(String filePath) throws IOException;

}
