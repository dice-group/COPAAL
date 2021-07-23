package org.dice_research.fc.paths.imprt;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.data.QRestrictedPath;
import org.springframework.stereotype.Component;

/**
 * Classes implementing this interface should describe different methods of importing paths to
 * COPAAL's framework.
 * 
 * @author Alexandra Silva
 *
 */
@Component
public interface IPathImporter {

  /** 
   * @param filePath Path where the file is saved.
   * @return The paths corresponding to a predicate.
   * @throws IOException
   */
  Entry<Property, List<QRestrictedPath>> importPaths(String filePath) throws IOException;

}
