package org.dice_research.fc.serialization;

import java.io.IOException;
import org.dice_research.fc.data.IPieceOfEvidence;
import org.dice_research.fc.data.QRestrictedPath;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * JSON deserializer for {@link QRestrictedPath} objects.
 * 
 * @author Alexandra Silva
 *
 */
public class PathDeserializer extends StdDeserializer<QRestrictedPath> {

  private static final long serialVersionUID = -8548360483723092608L;

  public PathDeserializer() {
    this(null);
  }

  protected PathDeserializer(Class<IPieceOfEvidence> arg0) {
    super(arg0);
  }

  @Override
  public QRestrictedPath deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    JsonNode node = parser.getCodec().readTree(parser);
    Number number = node.get("score").numberValue();
    String propertyPath = node.get("evidence").asText();
    double score = Double.NaN;
    if (number != null) {
      score = number.doubleValue();
    }
    return QRestrictedPath.create(propertyPath, score);
  }

}
