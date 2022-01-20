package org.dice_research.fc.paths.filter;

import java.util.function.Predicate;
import org.dice_research.fc.data.QRestrictedPath;

/**
 * A class for filtering paths.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface IPathFilter extends Predicate<QRestrictedPath> {

}
