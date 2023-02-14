package org.dice_research.fc.serialization;

import java.io.IOException;
import org.dice_research.fc.data.IPieceOfEvidence;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * JSON serializer class of {@link IPieceOfEvidence}.
 * 
 * @author Alexandra Silva
 *
 */
public class PathSerializer extends StdSerializer<IPieceOfEvidence> {
  
  private static final long serialVersionUID = -2924153086733024344L;

  public PathSerializer() {
    this(null);
  }

  protected PathSerializer(Class<IPieceOfEvidence> arg0) {
    super(arg0);
  }

  @Override
  public void serialize(IPieceOfEvidence path, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.useDefaultPrettyPrinter();
    gen.writeStartObject();
    gen.writeNumberField("score", path.getScore());
    gen.writeStringField("evidence", path.getEvidence());
    gen.writeStringField("verbalization", path.getVerbalizedOutput());
    gen.writeStringField("sample",path.getSample());
    gen.writeEndObject();

  }

}
