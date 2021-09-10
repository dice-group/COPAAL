package org.dice_research.fc.paths;


/**
 * An interface implementing the decorator pattern for {@link IPathSearcher}.
 *
 * @author Farshad Afshari
 *
 */

public interface IPathSearcherDecorator extends IPathSearcher{
    /**
     * Returns the decorated instance of {@link IPathSearcher}.
     *
     * @return the decorated instance
     */
    IPathSearcher getDecorated();
}
