package org.dice_research.fc.paths.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a model for pathElement which saved in a DB
 *
 * @author Farshad Afshari
 *
 */

@Entity
@Table(name = "PathElements")
public class PathElement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * many to many connection with path table
     * */
    @ManyToMany( mappedBy = "pathElements")
    private List<Path> paths = new ArrayList<>();

    @Column(name = "Inverted")
    private Boolean inverted;

    @Column(name = "Property")
    private String property;

    public PathElement() {
    }

    public PathElement(Boolean inverted, String property) {
        this.inverted = inverted;
        this.property = property;
    }

    public long getId() {
        return id;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }

    public void addPath(Path paths) {
        this.paths.add(paths);
    }

    public Boolean isInverted() {
        return inverted;
    }

    public void setInverted(Boolean inverted) {
        this.inverted = inverted;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
