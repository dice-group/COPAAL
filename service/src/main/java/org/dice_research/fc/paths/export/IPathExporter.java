package org.dice_research.fc.paths.export;

import java.util.List;
import java.util.Map.Entry;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.data.QRestrictedPath;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Implementations of this interface can be used to persist paths.
 * 
 * @author Alexandra Silva
 *
 */
public interface IPathExporter {

  /**
   * @param entry Path we want to persist
   * @return Path to saved file.
   * @throws JsonProcessingException
   */
  String exportPaths(Entry<Property, List<QRestrictedPath>> entry) throws JsonProcessingException;

}
