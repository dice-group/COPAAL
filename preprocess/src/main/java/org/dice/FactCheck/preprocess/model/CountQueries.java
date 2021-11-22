package org.dice.FactCheck.preprocess.model;

import java.util.HashSet;
import java.util.Set;

public class CountQueries {
    private Set<String> CoOccurrenceCountQueries;
    private Set<String> PathInstancesCountQueries;
    private Set<String> MaxCountQueries;
    private Set<String> PredicateInstancesCountQueries;
    private Set<String> TypeInstancesCountQueries;

    public CountQueries(){
        this.CoOccurrenceCountQueries = new HashSet<>();
        this.PathInstancesCountQueries = new HashSet<>();
        this.MaxCountQueries = new HashSet<>();
        this.PredicateInstancesCountQueries = new HashSet<>();
        this.TypeInstancesCountQueries = new HashSet<>();
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

    public void addToPredicateInstancesCountQueries(String s){this.PredicateInstancesCountQueries.add(s); }

    public void addToTypeInstancesCountQueries(String s){
        this.TypeInstancesCountQueries.add(s);
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

    public Set<String> getPredicateInstancesCountQueries() { return PredicateInstancesCountQueries; }

    public Set<String> getTypeInstancesCountQueries() {return TypeInstancesCountQueries;}

    public String whatIsTheSize(){
        return "CoOccurrenceCountQueries size is :\n"+CoOccurrenceCountQueries.size()+"\nPathInstancesCountQueries size is :\n" +PathInstancesCountQueries.size()+
                "\nMaxCountQueries\n"+MaxCountQueries.size()+"\nPredicateInstancesCountQueries\n"+PredicateInstancesCountQueries.size()+"\nTypeInstancesCountQueries\n"+TypeInstancesCountQueries.size();
    }
}
