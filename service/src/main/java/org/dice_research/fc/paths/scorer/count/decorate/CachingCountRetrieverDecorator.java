package org.dice_research.fc.paths.scorer.count.decorate;

import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CachingCountRetrieverDecorator extends AbstractCountRetrieverDecorator {

  private static final long DEFAULT_MAX_SIZE_PATH_CACHE = 1000;
  private static final long DEFAULT_MAX_SIZE_PRED_CACHE = 200;
  private static final long DEFAULT_MAX_SIZE_COOC_CACHE = 1000;
  private static final long DEFAULT_MAX_SIZE_MAX_COUNT_CACHE = DEFAULT_MAX_SIZE_PRED_CACHE;

  protected LoadingCache<PathInstanceCountQuery, Long> pathInstanceCache;
  protected LoadingCache<Predicate, Long> predicateInstanceCache;
  protected LoadingCache<CooccurrenceCountQuery, Long> cooccurrenceCache;
  protected LoadingCache<Predicate, Long> maxCountCache;

  /**
   * Constructor that uses default sizes for the internal caches.
   * 
   * @param decorated the {@link ICountRetriever} instance used internally.
   */
  public CachingCountRetrieverDecorator(ICountRetriever decorated) {
    this(decorated, DEFAULT_MAX_SIZE_PATH_CACHE, DEFAULT_MAX_SIZE_PRED_CACHE,
        DEFAULT_MAX_SIZE_COOC_CACHE, DEFAULT_MAX_SIZE_MAX_COUNT_CACHE);
  }

  /**
   * Constructor that allows the configuration of the maximum cache sizes.
   * 
   * @param decorated the {@link ICountRetriever} instance used internally.
   * @param maxSizePathCache maximum size for the cache of path counts
   * @param maxSizePredCache maximum size for the cache of predicate counts
   * @param maxSizeCoocCache maximum size for the cache of co-occurrence counts
   * @param maxSizeMaxCountCache maximum size for the cache of maximum count per predicate
   */
  public CachingCountRetrieverDecorator(ICountRetriever decorated, long maxSizePathCache,
      long maxSizePredCache, long maxSizeCoocCache, long maxSizeMaxCountCache) {
    super(decorated);
    pathInstanceCache = CacheBuilder.newBuilder().maximumSize(maxSizePathCache)
        .build(new CacheLoader<PathInstanceCountQuery, Long>() {
          public Long load(PathInstanceCountQuery key) {
            return decorated.countPathInstances(key.path, key.domainRestriction,
                key.rangeRestriction);
          }
        });
    predicateInstanceCache = CacheBuilder.newBuilder().maximumSize(maxSizePredCache)
        .build(new CacheLoader<Predicate, Long>() {
          public Long load(Predicate key) {
            return decorated.countPredicateInstances(key);
          }
        });
    cooccurrenceCache = CacheBuilder.newBuilder().maximumSize(maxSizeCoocCache)
        .build(new CacheLoader<CooccurrenceCountQuery, Long>() {
          public Long load(CooccurrenceCountQuery key) {
            return decorated.countCooccurrences(key.predicate, key.path);
          }
        });
    maxCountCache = CacheBuilder.newBuilder().maximumSize(maxSizeMaxCountCache)
        .build(new CacheLoader<Predicate, Long>() {
          public Long load(Predicate key) {
            return decorated.deriveMaxCount(key);
          }
        });
  }

  @Override
  public long countPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction,
      ITypeRestriction rangeRestriction) {
    return pathInstanceCache
        .getUnchecked(new PathInstanceCountQuery(path, domainRestriction, rangeRestriction));
  }

  @Override
  public long countPredicateInstances(Predicate predicate) {
    return predicateInstanceCache.getUnchecked(predicate);
  }

  @Override
  public long countCooccurrences(Predicate predicate, QRestrictedPath path) {
    return cooccurrenceCache.getUnchecked(new CooccurrenceCountQuery(predicate, path));
  }

  @Override
  public long deriveMaxCount(Predicate predicate) {
    return maxCountCache.getUnchecked(predicate);
  }


  /**
   * A simple class that is used to wrap the elements of a call of the
   * {@link ICountRetriever#countPathInstances(QRestrictedPath, ITypeRestriction, ITypeRestriction)}
   * method.
   * 
   * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
   *
   */
  public static class PathInstanceCountQuery {
    public QRestrictedPath path;
    public ITypeRestriction domainRestriction;
    public ITypeRestriction rangeRestriction;

    public PathInstanceCountQuery(QRestrictedPath path, ITypeRestriction domainRestriction,
        ITypeRestriction rangeRestriction) {
      super();
      this.path = path;
      this.domainRestriction = domainRestriction;
      this.rangeRestriction = rangeRestriction;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((domainRestriction == null) ? 0 : domainRestriction.hashCode());
      result = prime * result + ((path == null) ? 0 : path.hashCode());
      result = prime * result + ((rangeRestriction == null) ? 0 : rangeRestriction.hashCode());
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
      PathInstanceCountQuery other = (PathInstanceCountQuery) obj;
      if (domainRestriction == null) {
        if (other.domainRestriction != null)
          return false;
      } else if (!domainRestriction.equals(other.domainRestriction))
        return false;
      if (path == null) {
        if (other.path != null)
          return false;
      } else if (!path.equals(other.path))
        return false;
      if (rangeRestriction == null) {
        if (other.rangeRestriction != null)
          return false;
      } else if (!rangeRestriction.equals(other.rangeRestriction))
        return false;
      return true;
    }
  }

  /**
   * A simple class that is used to wrap the elements of a call of the
   * {@link ICountRetriever#countCooccurrences(Predicate, QRestrictedPath)} method.
   * 
   * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
   *
   */
  public static class CooccurrenceCountQuery {
    public Predicate predicate;
    public QRestrictedPath path;

    public CooccurrenceCountQuery(Predicate predicate, QRestrictedPath path) {
      this.predicate = predicate;
      this.path = path;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((path == null) ? 0 : path.hashCode());
      result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
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
      CooccurrenceCountQuery other = (CooccurrenceCountQuery) obj;
      if (path == null) {
        if (other.path != null)
          return false;
      } else if (!path.equals(other.path))
        return false;
      if (predicate == null) {
        if (other.predicate != null)
          return false;
      } else if (!predicate.equals(other.predicate))
        return false;
      return true;
    }
  }
}
