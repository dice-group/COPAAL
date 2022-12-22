package org.dice_research.fc.paths.model;

import javax.persistence.*;

/**
 * This class is a model for maximum number of predicate which saved in a DB
 * It will be use in scoring part
 * @author Farshad Afshari
 *
 */

@Entity
@Table(name = "CMaxPredicate")
public class CountMaxPredicate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "Predicate")
    private String predicate;

    @Column(name = "Domain", length = 4000)
    private String domain;

    @Column(name = "Range", length = 4000)
    private String range;

    @Column(name = "ccount")
    private long cCount;

    @Column(name = "GraphName")
    private String graphName;

    public CountMaxPredicate(){}

    public CountMaxPredicate(String predicate, String domain, String range, String graphName, long cCount) {
        this.predicate = predicate;
        this.domain = domain;
        this.range = range;
        this.cCount = cCount;
        this.graphName = graphName;
    }

    public long getId() {
        return id;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public long getcCount() {
        return cCount;
    }

    public void setcCount(long cCount) {
        this.cCount = cCount;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }
}
