package org.dice_research.fc.paths.scorer.count.stream;

import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dice_research.fc.tentris.TentrisAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an abstract base implementation of a count retriever. It's main functionality is to send
 * queries to a SPARQL endpoint and count the number of results in the JSON result stream using the
 * {@link StreamingSPARQLResultCounter} class.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 * @deprecated Since it was planned to be used for Tentris, the {@link TentrisAdapter} should be
 *             used instead.
 */
@Deprecated
public abstract class AbstractStreamingBasedCountRetriever implements AutoCloseable {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractStreamingBasedCountRetriever.class);

  protected static final String COUNT_VARIABLE_NAME = "sum";

  protected final String endpoint;
  protected final CloseableHttpClient httpClient;

  public AbstractStreamingBasedCountRetriever(String endpoint) {
    this(HttpClients.createDefault(), endpoint);
  }

  public AbstractStreamingBasedCountRetriever(CloseableHttpClient httpClient, String endpoint) {
    this.endpoint = endpoint;
    this.httpClient = httpClient;
  }

  @Override
  public void close() throws Exception {
    this.httpClient.close();
  }

  protected long executeCountQuery(StringBuilder queryBuilder) {
    String query = queryBuilder.toString();
    try {
      long time = System.currentTimeMillis();
      LOGGER.debug("Starting count query {}", query);
      String url = endpoint + "?query=" + URLEncoder.encode(query, "UTF-8");
      HttpGet request = new HttpGet(url);
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        // Get HttpResponse Status
        if (response.getStatusLine().getStatusCode() != 200) {
          return 0L;
        }
        HttpEntity entity = response.getEntity();
        long result = 0;
        if (entity != null) {
          result = StreamingSPARQLResultCounter.countResults(entity.getContent());
          EntityUtils.consumeQuietly(entity);
        } else {
          return 0L;
        }
        LOGGER.debug("Got a query result ({}) after {}ms.", result,
            System.currentTimeMillis() - time);
        return result;
      }
    } catch (Exception e) {
      LOGGER.error("Got an exception while running count query \"" + query + "\". Returning 0.", e);
      return 0L;
    }
  }
}
