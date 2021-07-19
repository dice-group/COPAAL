package org.dice_research.fc.paths.model;

import org.apache.jena.rdf.model.Resource;

import javax.persistence.*;

@Entity
@Table(name = "paths")
public class Path {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "Subject")
    private String subject;

    @Column(name = "Predicate")
    private String predicate;

    @Column(name = "Object")
    private String object;

    @Column(name = "FactPreprocessor")
    private String factPreprocessorClassName;

    @Column(name = "CounterRetriever")
    private String counterRetrieverName;

    @Column(name = "PathSearcher")
    private String pathSearcherClassName;

    @Column(name = "PathScorer")
    private String pathScorerClassName;

    public Path(){}

    public Path(long id, String subject, String predicate, String object, String factPreprocessorClassName, String counterRetrieverName, String pathSearcherClassName, String pathScorerClassName) {
        this.id = id;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.factPreprocessorClassName = factPreprocessorClassName;
        this.counterRetrieverName = counterRetrieverName;
        this.pathSearcherClassName = pathSearcherClassName;
        this.pathScorerClassName = pathScorerClassName;
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
        return factPreprocessorClassName;
    }

    public void setFactPreprocessorClassName(String factPreprocessorClassName) {
        this.factPreprocessorClassName = factPreprocessorClassName;
    }

    public String getCounterRetrieverName() {
        return counterRetrieverName;
    }

    public void setCounterRetrieverName(String counterRetrieverName) {
        this.counterRetrieverName = counterRetrieverName;
    }

    public String getPathSearcherClassName() {
        return pathSearcherClassName;
    }

    public void setPathSearcherClassName(String pathSearcherClassName) {
        this.pathSearcherClassName = pathSearcherClassName;
    }

    public String getPathScorerClassName() {
        return pathScorerClassName;
    }

    public void setPathScorerClassName(String pathScorerClassName) {
        this.pathScorerClassName = pathScorerClassName;
    }
}
