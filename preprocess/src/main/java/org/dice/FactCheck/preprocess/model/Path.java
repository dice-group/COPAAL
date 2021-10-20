package org.dice.FactCheck.preprocess.model;

import org.apache.commons.math3.util.Pair;
import java.util.LinkedList;

/**
 * This class is a Path of predicates and
 *
 * @author Farshad Afshari
 *
 */
public class Path {
    private LinkedList<Pair<Predicate, Boolean>> paths;

    public Path() {}

    public Path(LinkedList<Pair<Predicate, Boolean>> paths) {
        this.paths = paths;
    }

    public void addPart(Predicate input,Boolean isInverted){
        paths.add(new Pair<>(input,isInverted));
    }

    public LinkedList<Pair<Predicate, Boolean>> getPaths() {
        return paths;
    }
}
