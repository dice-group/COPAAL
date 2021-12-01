package org.dice_research.fc.paths.search;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.IPathSearcher;

/**
 * A simple implementation of an {@link IPathSearcher} which retrieves paths
 * based on the paths that it got before (using the
 * {@link #addPaths(String, Collection)} method). It should be noted that the
 * implementation does neither look at the given subject or object, nor at the
 * given predicate. The derived paths solely rely on the property.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class KnownPathsBasedPathSearcher implements IPathSearcher {

    protected Map<String, Set<QRestrictedPath>> paths = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public Collection<QRestrictedPath> search(Resource subject, Predicate predicate, Resource object) {
        String propertyUri = predicate.getProperty().getURI();
        if (this.paths.containsKey(propertyUri)) {
            return this.paths.get(propertyUri);
        } else {
            return Collections.EMPTY_SET;
        }
    }

    public void addPaths(String property, Collection<QRestrictedPath> paths) {
        if (this.paths.containsKey(property)) {
            this.paths.get(property).addAll(paths);
        } else {
            this.paths.put(property, new HashSet<>(paths));
        }
    }

}
