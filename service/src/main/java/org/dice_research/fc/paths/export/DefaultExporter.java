package org.dice_research.fc.paths.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.data.QRestrictedPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Stores the pre-processed paths and respective scores in JSON format.
 * 
 * @author Alexandra Silva
 *
 */
@Component
public class DefaultExporter implements IPathExporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExporter.class);

  /**
   * Parent folder where all the paths will be stored.
   */
  private final String FOLDER;
  
  /**
   * Constructor.
   */
  @Autowired
  public DefaultExporter(String folder) {
    FOLDER = folder;
  }
  
  @Override
  public String exportPaths(Entry<Property, List<QRestrictedPath>> pair)
      throws JsonProcessingException {
    // create folder if not existing
    File parentDir = new File(FOLDER);
    parentDir.mkdir();
    
    // prepare JSON
    SimpleModule module = new SimpleModule();
    ObjectMapper mapper = new ObjectMapper().registerModule(module);
    String serialized = mapper.writeValueAsString(pair);

    // add paths to the respective property's file
    File propDir = new File(FOLDER + pair.getKey().getLocalName() +".json");
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(propDir));) {
      bw.write(serialized);
    } catch (IOException e) {
      LOGGER.error("Error writing to file in {} ", propDir.getAbsolutePath());
    }
    return propDir.getAbsolutePath();
  }
}
