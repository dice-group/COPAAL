package org.dice_research.fc.paths.scorer;

import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.IPathScorer;

/**
 * This simple abstract implementation of an {@link IPathScorer} only checks
 * whether the given path exists for the gievn subject and object. If it does,
 * it returns the same path object (note that we assume that the path has
 * already been scored beforehand!). Otherwise, a new path object with the same
 * path element and a sentinel score (default={@value #DEFAULT_SENTINEL_SCORE})
 * is returned.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public abstract class AbstractPathExistenceCheckingScorer implements IPathScorer {

    public static final double DEFAULT_SENTINEL_SCORE = 0.0;

    /**
     * The sentinel score used for paths that couldn't be found.
     */
    protected double sentinelScore;

    public AbstractPathExistenceCheckingScorer() {
        this(DEFAULT_SENTINEL_SCORE);
    }

    public AbstractPathExistenceCheckingScorer(double sentinelScore) {
        this.sentinelScore = sentinelScore;
    }

    @Override
    public QRestrictedPath score(Resource subject, Predicate predicate, Resource object, QRestrictedPath path) {
        if (pathExists(subject, path, object)) {
            return path;
        } else {
            return new QRestrictedPath(path.getPathElements(), sentinelScore);
        }
    }

    protected abstract boolean pathExists(Resource subject, QRestrictedPath path, Resource object);

}
