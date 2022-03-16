package org.dice_research.fc.tentris;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientBasedTentrisAdapter implements TentrisAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientBasedTentrisAdapter.class);

  protected final String endpoint;
  protected final CloseableHttpClient httpClient;

  public ClientBasedTentrisAdapter(String endpoint) {
    this(HttpClients.createDefault(), endpoint);
  }

  public ClientBasedTentrisAdapter(CloseableHttpClient httpClient, String endpoint) {
    this.endpoint = endpoint;
    this.httpClient = httpClient;
  }

  @Override
  public void close() throws Exception {
    this.httpClient.close();
  }

  @Override
  public String executeSingleResultQuery(String query, String service) {
    try {
      StringBuilder builder = new StringBuilder();
      builder.append(endpoint);
      builder.append(service);
      builder.append("?query=");
      builder.append(URLEncoder.encode(query, "UTF-8"));
      String url = builder.toString();
      HttpGet request = new HttpGet(url);
      HttpEntity entity = null;
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        // Get HttpResponse Status
        if (response.getStatusLine().getStatusCode() != 200) {
          LOGGER.warn("Got a response with the status code {}. Returning null.",
              response.getStatusLine().getStatusCode());
          return null;
        }
        entity = response.getEntity();
        if (entity != null) {
          return IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8).trim();
        } else {
          LOGGER.warn("Got a response without content. Returning null.");
          return null;
        }
      } finally {
        if (entity != null) {
          EntityUtils.consumeQuietly(entity);
        }
      }
    } catch (Exception e) {
      LOGGER.error("Got an exception while running count query \"" + query + "\". Returning null.",
          e);
      return null;
    }
  }

}
