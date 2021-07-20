package org.dice_research.fc.serialization;

import java.io.IOException;
import org.apache.jena.rdf.model.ResourceFactory;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

/**
 * Map key deserializer for {@link Property} objects.
 * 
 * @author Alexandra Silva
 *
 */
public class PropertyDeserializer extends KeyDeserializer{

  @Override
  public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
    return ResourceFactory.createProperty(key);
  }

}
