package org.dice_research.fc.paths.imprt;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.serialization.PathDeserializer;
import org.dice_research.fc.serialization.PropertyDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * JSON importer for persisted path files.
 * 
 * @author Alexandra Silva
 *
 */
@Component
public class DefaultImporter implements IPathImporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultImporter.class);

  @Override
  public Entry<Property, List<QRestrictedPath>> importPaths(String filePath) {
    
    // register deserializers
    SimpleModule module = new SimpleModule();
    module.addKeyDeserializer(Property.class, new PropertyDeserializer());
    module.addDeserializer(QRestrictedPath.class, new PathDeserializer());
    ObjectMapper mapper = new ObjectMapper().registerModule(module);

    // get the map
    Entry<Property, List<QRestrictedPath>> typedMap = null;
    try {
      typedMap = mapper.readValue(new File(filePath),
          new TypeReference<Entry<Property, List<QRestrictedPath>>>() {});
    } catch (IOException e) {
      LOGGER.error("Could not parse paths in {} to a Map.", filePath);
      e.printStackTrace();
    }

    return typedMap;
  }
}
