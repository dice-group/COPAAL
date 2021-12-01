package org.dice_research.fc.paths.scorer.count.max;

import org.apache.http.impl.client.CloseableHttpClient;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.paths.scorer.count.stream.AbstractStreamingBasedCountRetriever;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;

/**
 * This extension of the {@link AbstractStreamingBasedCountRetriever} is an
 * {@link IMaxCounter} implementation which is optimized for the Tentris triple
 * store.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class TentrisBasedMaxCounter extends AbstractStreamingBasedCountRetriever implements IMaxCounter {

    public TentrisBasedMaxCounter(String endpoint) {
        super(endpoint);
    }

    public TentrisBasedMaxCounter(CloseableHttpClient httpClient, String endpoint) {
        super(httpClient, endpoint);
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
        return executeCountQuery(queryBuilder);
    }
}
