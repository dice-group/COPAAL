package org.dice_research.fc;

/**
 * This interface for Mapping from F to T and vice versa .
 *
 * @author Farshad Afshari
 *
 */
public interface IBidirectionalMapper<F,T> extends IMapper <F,T>{
    F reverseMap(T from);
}
