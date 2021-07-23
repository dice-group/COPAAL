package org.dice_research.fc.paths.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a model for path which saved in a DB
 *
 * @author Farshad Afshari
 *
 */

@Entity
@Table(name = "Paths")
public class Path {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * This make many to many relation with PathElement table
     */

    @ManyToMany
    @JoinTable(
            name = "pathelements_paths",
            joinColumns = @JoinColumn(name = "path_id"),
            inverseJoinColumns = @JoinColumn(name = "pathelements_id")
    )
    private List<PathElement> pathElements = new ArrayList<>();

    @Column(name = "Subject")
    private String subject;

    @Column(name = "Predicate")
    private String predicate;

    @Column(name = "Object")
    private String object;

    @Column(name = "Factpreprocessor")
    private String factPreprocessor;

    @Column(name = "CounterRetriever")
    private String counterRetriever;

    @Column(name = "PathSearcher")
    private String pathSearcher;

    @Column(name = "PathScorer")
    private String pathScorer;

    @Column(name = "Score")
    private double score;

    public Path(){}

    public Path( String subject, String predicate, String object, String factPreprocessor, String counterRetriever, String pathSearcher, String pathScorer, double score) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.factPreprocessor = factPreprocessor;
        this.counterRetriever = counterRetriever;
        this.pathSearcher = pathSearcher;
        this.pathScorer = pathScorer;
        this.score = score;
    }

    public void addPathElement(PathElement pathElements){
        this.pathElements.add(pathElements);
    }

    public long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getFactPreprocessorClassName() {
        return factPreprocessor;
    }

    public void setFactPreprocessorClassName(String factPreprocessorClassName) {
        this.factPreprocessor = factPreprocessorClassName;
    }

    public String getCounterRetrieverName() {
        return counterRetriever;
    }

    public void setCounterRetrieverName(String counterRetrieverName) {
        this.counterRetriever = counterRetrieverName;
    }

    public String getPathSearcherClassName() {
        return pathSearcher;
    }

    public void setPathSearcherClassName(String pathSearcherClassName) {
        this.pathSearcher = pathSearcherClassName;
    }

    public String getPathScorerClassName() {
        return pathScorer;
    }

    public void setPathScorerClassName(String pathScorerClassName) {
        this.pathScorer = pathScorerClassName;
    }

    public List<PathElement> getPathElements() {
        return pathElements;
    }

    public void setPathElements(List<PathElement> pathElements) {
        this.pathElements = pathElements;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
