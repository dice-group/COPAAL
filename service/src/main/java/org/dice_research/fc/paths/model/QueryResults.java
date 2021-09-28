package org.dice_research.fc.paths.model;

import javax.persistence.*;


/**
 * This class is a model for saving query result
 *
 * @author Farshad Afshari
 *
 */

@Entity
@Table(name = "QueryResults")
public class QueryResults {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "Query")
    private String query;

    @Column(name = "Response")
    private String response;

    @Column(name = "IsDone")
    private boolean isdone;

    public QueryResults() {
    }

    public QueryResults(String query, String response, boolean isdone) {
        this.query = query;
        this.response = response;
        this.isdone = isdone;
    }

    public long getId() {
        return id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isIsdone() {
        return isdone;
    }

    public void setIsdone(boolean isdone) {
        this.isdone = isdone;
    }
}
