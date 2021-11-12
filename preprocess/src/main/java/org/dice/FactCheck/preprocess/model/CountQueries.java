package org.dice.FactCheck.preprocess.model;

import java.util.HashSet;
import java.util.Set;

public class CountQueries {
    private Set<String> CoOccurrenceCountQueries;
    private Set<String> PathInstancesCountQueries;
    private Set<String> MaxCountQueries;

    public CountQueries(){
        this.CoOccurrenceCountQueries = new HashSet<>();
        this.PathInstancesCountQueries = new HashSet<>();
        this.MaxCountQueries = new HashSet<>();
    }

    public void addToCoOccurrenceCountQueries(String s){
        this.CoOccurrenceCountQueries.add(s);
    }

    public void addToPathInstancesCountQueries(String s){
        this.PathInstancesCountQueries.add(s);
    }

    public void addToMaxCountQueries(String s){
        this.MaxCountQueries.add(s);
    }

    public Set<String> getCoOccurrenceCountQueries() {
        return CoOccurrenceCountQueries;
    }

    public Set<String> getPathInstancesCountQueries() {
        return PathInstancesCountQueries;
    }

    public Set<String> getMaxCountQueries() {
        return MaxCountQueries;
    }
}
