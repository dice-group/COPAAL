package org.dice_research.fc.paths.export;

import java.util.List;
import java.util.Map.Entry;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.data.QRestrictedPath;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface IPathExporter {

  /**
   * TODO change to IPieceOfEvidence
   * @param propertyURI
   * @throws JsonProcessingException
   */
  String exportPaths(Entry<Property, List<QRestrictedPath>> entry) throws JsonProcessingException;

}
