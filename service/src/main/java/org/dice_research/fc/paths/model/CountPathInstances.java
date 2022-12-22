package org.dice_research.fc.paths.model;


import javax.persistence.*;

/**
 * This class is a model for number of Instances of a path which saved in a DB
 * It will be use in scoring part
 * @author Farshad Afshari
 *
 */

@Entity
@Table(name = "CPathInstances")
public class CountPathInstances {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "Path", length = 4000)
    private String path;

    @Column(name = "GraphName")
    private String graphName;

    @Column(name = "Domain", length = 4000)
    private String domain;

    @Column(name = "Range", length = 4000)
    private String range;

    @Column(name = "ccount")
    private Long cCount;

    public CountPathInstances(){}

    public CountPathInstances(String path, String graphName, String domain, String range, Long cCount) {
        this.path = path;
        this.graphName = graphName;
        this.domain = domain;
        this.range = range;
        this.cCount = cCount;
    }

    public long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
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

    public Long getcCount() {
        return cCount;
    }

    public void setcCount(Long cCount) {
        this.cCount = cCount;
    }
}
