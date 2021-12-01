package org.dice_research.fc.paths.scorer.count;

import org.apache.http.impl.client.CloseableHttpClient;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.paths.scorer.count.max.IMaxCounter;
import org.dice_research.fc.paths.scorer.count.stream.AbstractStreamingBasedCountRetriever;
import org.dice_research.fc.sparql.path.IPathClauseGenerator;
import org.dice_research.fc.sparql.path.PropPathBasedPathClauseGenerator;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;

/**
 * This extension of the {@link AbstractSPARQLBasedCountRetriever} counts the
 * exact number of subject-object pairs that are connected by the given
 * q-restricted path. The implementation relies on the usage of SPARQL property
 * paths.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class TentrisBasedCountRetriever extends AbstractStreamingBasedCountRetriever implements ICountRetriever {

    protected IPathClauseGenerator pathClauseGenerator;

    /**
     * The max count retriever.
     */
    protected IMaxCounter maxCounter;

    public TentrisBasedCountRetriever(String endpoint, IMaxCounter maxCounter) {
        this(endpoint, maxCounter, new PropPathBasedPathClauseGenerator());
    }

    public TentrisBasedCountRetriever(CloseableHttpClient httpClient, String endpoint, IMaxCounter maxCounter) {
        this(httpClient, endpoint, maxCounter, new PropPathBasedPathClauseGenerator());
    }

    public TentrisBasedCountRetriever(String endpoint, IMaxCounter maxCounter, IPathClauseGenerator pathClauseGenerator) {
        super(endpoint);
        this.maxCounter = maxCounter;
        this.pathClauseGenerator = pathClauseGenerator;
    }

    public TentrisBasedCountRetriever(CloseableHttpClient httpClient, String endpoint, IMaxCounter maxCounter,
            IPathClauseGenerator pathClauseGenerator) {
        super(httpClient, endpoint);
        this.maxCounter = maxCounter;
        this.pathClauseGenerator = pathClauseGenerator;
    }

    @Override
    public long deriveMaxCount(Predicate predicate) {
        return maxCounter.deriveMaxCount(predicate);
    }

    @Override
    public long countPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction,
            ITypeRestriction rangeRestriction) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT ?s ?o WHERE { ");
        domainRestriction.addRestrictionToQuery("s", queryBuilder);
        rangeRestriction.addRestrictionToQuery("o", queryBuilder);
        pathClauseGenerator.addPath(path, queryBuilder, "s", "o");
        queryBuilder.append(" }");
        return executeCountQuery(queryBuilder);
    }

    @Override
    public long countCooccurrences(Predicate predicate, QRestrictedPath path) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT ?s ?o WHERE { ?s <");
        queryBuilder.append(predicate.getProperty().getURI());
        queryBuilder.append("> ?o . ");
        predicate.getDomain().addRestrictionToQuery("s", queryBuilder);
        predicate.getRange().addRestrictionToQuery("o", queryBuilder);
        pathClauseGenerator.addPath(path, queryBuilder, "s", "o");
        queryBuilder.append(" }");
        return executeCountQuery(queryBuilder);
    }

    @Override
    public long countPredicateInstances(Predicate predicate) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT ?s ?o WHERE { ?s <");
        queryBuilder.append(predicate.getProperty().getURI());
        queryBuilder.append("> ?o . ");
        predicate.getDomain().addRestrictionToQuery("s", queryBuilder);
        predicate.getRange().addRestrictionToQuery("o", queryBuilder);
        queryBuilder.append(" }");
        return executeCountQuery(queryBuilder);
    }

}
