package org.dice_research.fc.paths.model;

import javax.persistence.*;

/**
 * This class is a model for number of predicate which saved in a DB
 * It will be use in scoring part
 * @author Farshad Afshari
 *
 */

@Entity
@Table(name = "CPredicate")
public class CountPredicate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "Predicate")
    private String predicate;

    @Column(name = "GraphName")
    private String graphName;

    @Column(name = "ccount")
    private Long cCount;

    public CountPredicate(){}

    public CountPredicate(String predicate, String graphName, Long cCount) {
        this.predicate = predicate;
        this.graphName = graphName;
        this.cCount = cCount;
    }

    public long getId() {
        return id;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicateURI(String predicate) {
        this.predicate = predicate;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public Long getcCount() {
        return cCount;
    }

    public void setcCount(Long cCount) {
        this.cCount = cCount;
    }
}
