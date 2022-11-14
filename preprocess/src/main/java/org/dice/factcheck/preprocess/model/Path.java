package org.dice.factcheck.preprocess.model;

import org.apache.commons.math3.util.Pair;
import java.util.LinkedList;
import java.util.Objects;
import org.dice_research.fc.data.Predicate;
/**
 * This model is a Path of predicates and
 *
 * @author Farshad Afshari
 *
 */
public class Path implements Cloneable{
    private LinkedList<Pair<Predicate, Boolean>> paths;

    public Path() {
        this(new LinkedList<>());
    }

    public Path(LinkedList<Pair<Predicate, Boolean>> paths) {
        this.paths = paths;
    }

    // initiate the class with just one instance
    public Path(Predicate predicate , Boolean inverted) {
        Pair<Predicate,Boolean> pair = new Pair<>(predicate,inverted);
        LinkedList<Pair<Predicate, Boolean>> ll = new LinkedList<>();
        ll.add(pair);
        this.paths = new LinkedList<Pair<Predicate, Boolean>>(ll);
    }

    // add part to the path
    public void addPart(Predicate input,Boolean isInverted){
        this.paths.add(new Pair<>(input,isInverted));
    }

    public LinkedList<Pair<Predicate, Boolean>> getPaths() {
        return paths;
    }

    public Pair<Predicate, Boolean> getLastNode(){
        if(paths.size()==0) return null;
        return paths.get(paths.size()-1);
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

    public Path clone() throws CloneNotSupportedException {
        LinkedList<Pair<Predicate, Boolean>> clonedPaths = new LinkedList<>();
        for(Pair<Predicate, Boolean> pair : paths){
            Pair<Predicate, Boolean> clonedPair = new Pair<Predicate, Boolean>((Predicate) pair.getFirst().clone(), pair.getSecond());
            clonedPaths.add(clonedPair);
        }
        return new Path(clonedPaths);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Pair<Predicate, Boolean> p : paths){
            if(!p.getSecond()){
                sb.append("^");
            }
            sb.append("<");
            sb.append(p.getFirst().getProperty().getURI());
            sb.append(">");
        }
        return sb.toString();
    }
}
