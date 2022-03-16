package org.dice_research.fc.tentris;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTentrisAdapter implements TentrisAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTentrisAdapter.class);

  protected final String endpoint;

  public SimpleTentrisAdapter(String endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  public void close() throws Exception {}

  @Override
  public String executeSingleResultQuery(String query, String service) {
    try {
      StringBuilder builder = new StringBuilder();
      builder.append(endpoint);
      builder.append(service);
      builder.append("?query=");
      builder.append(URLEncoder.encode(query, "UTF-8"));
      URL requestUrl = new URL(builder.toString());
      try (InputStream in = requestUrl.openStream()) {
        if (in != null) {
          return IOUtils.toString(in, StandardCharsets.UTF_8).trim();
        } else {
          LOGGER.warn("Got a response without content. Returning null.");
          return null;
        }
      }
    } catch (Exception e) {
      LOGGER.error("Got an exception while running count query \"" + query + "\". Returning null.",
          e);
      return null;
    }
  }

}
