package org.dice_research.fc.paths;

/**
 * A decorator abstract class for IPathSearcher, when a class want implement a decorator for IPathSearcher should extend this
 *
 * @author Farshad Afshari
 *
 */
public abstract class AbstractPathSearcherDecorator implements IPathSearcherDecorator{
    /**
     * The decorated instance.
     */
    protected IPathSearcher decorator;

    public AbstractPathSearcherDecorator(IPathSearcher decoratedPathSearcher){
        this.decorator = decoratedPathSearcher;
    }

    @Override
    public IPathSearcher getDecorated(){
        return decorator;
    }
}
