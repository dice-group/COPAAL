package org.dice_research.fc;

/**
 * This interface for Mapping from F to T.
 *
 * @author Farshad Afshari
 *
 */
public interface IMapper <F,T>{
    T map(F from);
}
