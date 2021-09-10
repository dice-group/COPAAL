package org.dice_research.fc.paths.scorer.count.decorate;

import java.util.concurrent.ExecutionException;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice_research.fc.sparql.restrict.OneOfRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * This is a simple extension of the {@link SamplingCountRetrieverDecorator} that uses an internal
 * cache for the sampling results.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class CachingSamplingCountRetrieverDecorator extends SamplingCountRetrieverDecorator {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CachingSamplingCountRetrieverDecorator.class);

  private static final long DEFAULT_MAX_SIZE_RESTRICTION_CACHE = 1000;

  /**
   * Cache used for the samling results.
   */
  protected LoadingCache<CacheKey, OneOfRestriction> samplingResultCache;

  /**
   * Constructor.
   * 
   * @param decorated The decorated count retriever that is used to get the counts for the
   *        predicates and paths restricted to the sampled list of instances
   * @param seed The seed that is used to initialize the random number generator
   * @param maxSampleSize The maximum size of a list of instances
   * @param qef The {@link QueryExecutionFactory} used to derive the list of instances
   */
  public CachingSamplingCountRetrieverDecorator(ICountRetriever decorated, long seed,
      int maxSampleSize, QueryExecutionFactory qef) {
    this(decorated, seed, maxSampleSize, qef, DEFAULT_MAX_SIZE_RESTRICTION_CACHE);
  }

  /**
   * Constructor.
   * 
   * @param decorated The decorated count retriever that is used to get the counts for the
   *        predicates and paths restricted to the sampled list of instances
   * @param seed The seed that is used to initialize the random number generator
   * @param maxSampleSize The maximum size of a list of instances
   * @param qef The {@link QueryExecutionFactory} used to derive the list of instances
   * @param maxCacheSize the size of the cache that is used to cache sample results
   */
  public CachingSamplingCountRetrieverDecorator(ICountRetriever decorated, long seed,
      int maxSampleSize, QueryExecutionFactory qef, long maxCacheSize) {
    super(decorated, seed, maxSampleSize, qef);
    samplingResultCache = CacheBuilder.newBuilder().maximumSize(maxCacheSize)
        .build(new CacheLoader<CacheKey, OneOfRestriction>() {
          public OneOfRestriction load(CacheKey entry) {
            return replaceRestriction(entry.restriction, entry.isSubject);
          }
        });
  }

  @Override
  protected OneOfRestriction replaceRestriction(ITypeRestriction restriction, boolean isSubject) {
    try {
      return samplingResultCache.get(new CacheKey(restriction, isSubject));
    } catch (ExecutionException e) {
      LOGGER.error("Got an exception while accessing the sampling result cache. Returning null.",
          e);
      return null;
    }
  }

  /**
   * Local class used to represent the key of an entry in the cache. It is simply the call of the
   * {@link CachingSamplingCountRetrieverDecorator#replaceRestriction(ITypeRestriction, boolean)}
   * method wrapped as an object.
   * 
   * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
   *
   */
  private static class CacheKey {
    public ITypeRestriction restriction;
    public boolean isSubject;

    public CacheKey(ITypeRestriction restriction, boolean isSubject) {
      super();
      this.restriction = restriction;
      this.isSubject = isSubject;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (isSubject ? 1231 : 1237);
      result = prime * result + ((restriction == null) ? 0 : restriction.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      CacheKey other = (CacheKey) obj;
      if (isSubject != other.isSubject)
        return false;
      if (restriction == null) {
        if (other.restriction != null)
          return false;
      } else if (!restriction.equals(other.restriction))
        return false;
      return true;
    }
  }

}
