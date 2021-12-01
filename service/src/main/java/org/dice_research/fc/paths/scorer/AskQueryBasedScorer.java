package org.dice_research.fc.paths.scorer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.sparql.path.IPathClauseGenerator;

/**
 * This is an implementation of the {@link AbstractPathExistenceCheckingScorer}
 * that relies on SPARQL ASK queries to check the existence of the given,
 * already scored paths.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class AskQueryBasedScorer extends AbstractPathExistenceCheckingScorer {

    protected QueryExecutionFactory qef;
    protected IPathClauseGenerator pathClauseGenerator;

    public AskQueryBasedScorer(QueryExecutionFactory qef, IPathClauseGenerator pathClauseGenerator) {
        this.qef = qef;
        this.pathClauseGenerator = pathClauseGenerator;
    }

    @Override
    protected boolean pathExists(Resource subject, QRestrictedPath path, Resource object) {
        String query = prepareQuery(subject, path, object);
        try (QueryExecution qe = qef.createQueryExecution(query)) {
            return qe.execAsk();
        }
    }

    private String prepareQuery(Resource subject, QRestrictedPath path, Resource object) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("ASK { ");
        pathClauseGenerator.addPath(path, queryBuilder, "subject", "object");
        queryBuilder.append(" }");
        // FIXME this is a horrible hack to make it work. The path clause generator
        // shouldn't assume to get variables...
        return queryBuilder.toString().replace("?subject", "<" + subject.getURI() + ">").replace("?object",
                "<" + object.getURI() + ">");
    }
}
