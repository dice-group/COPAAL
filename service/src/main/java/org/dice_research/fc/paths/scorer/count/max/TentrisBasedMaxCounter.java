package org.dice_research.fc.paths.scorer.count.max;

import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice_research.fc.tentris.TentrisAdapter;

/**
 * This extension of the {@link AbstractStreamingBasedCountRetriever} is an {@link IMaxCounter}
 * implementation which is optimized for the Tentris triple store.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class TentrisBasedMaxCounter implements IMaxCounter {

  protected TentrisAdapter adapter;

  public TentrisBasedMaxCounter(TentrisAdapter adapter) {
    this.adapter = adapter;
  }

  @Override
  public long deriveMaxCount(Predicate predicate) {
    return countTypeInstances(predicate.getDomain()) * countTypeInstances(predicate.getRange());
  }

  protected long countTypeInstances(ITypeRestriction restriction) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT DISTINCT ?s WHERE { ");
    restriction.addRestrictionToQuery("s", queryBuilder);
    queryBuilder.append(" }");
    return adapter.executeCountQuery(queryBuilder.toString());
  }
}
