package org.dice.FactCheck.preprocess.model;

import org.apache.commons.math3.util.Pair;
import java.util.LinkedList;
import java.util.Objects;

/**
 * This class is a Path of predicates and
 *
 * @author Farshad Afshari
 *
 */
public class Path {
    private LinkedList<Pair<Predicate, Boolean>> paths;

    public Path() {this.paths = new LinkedList<>();}

    public Path(LinkedList<Pair<Predicate, Boolean>> paths) {
        this.paths = paths;
    }

    public void addPart(Predicate input,Boolean isInverted){
        this.paths.add(new Pair<>(input,isInverted));
    }

    public LinkedList<Pair<Predicate, Boolean>> getPaths() {
        return paths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Objects.equals(getPaths(), path.getPaths());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPaths());
    }
}
