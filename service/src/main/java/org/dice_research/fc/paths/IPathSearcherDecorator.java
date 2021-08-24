package org.dice_research.fc.paths;

import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;

import java.util.Collection;

/**
 * A decorator abstract class for IPathSearcher, when a class want implement a decorator for IPathSearcher should extend this
 *
 * @author Farshad Afshari
 *
 */
public abstract class IPathSearcherDecorator implements IPathSearcher{
    protected IPathSearcher decoratedPathSearcher;

    public IPathSearcherDecorator(IPathSearcher decoratedPathSearcher){
        this.decoratedPathSearcher = decoratedPathSearcher;
    }

    public Collection<QRestrictedPath> search(Resource subject, Predicate predicate, Resource object){
        return decoratedPathSearcher.search(subject,predicate,object);
    }
}
