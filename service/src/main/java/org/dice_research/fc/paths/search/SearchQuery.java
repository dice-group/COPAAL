package org.dice_research.fc.paths.search;

import java.util.BitSet;

public class SearchQuery {

  private String query;
  private BitSet directions;

  public SearchQuery(String query, BitSet directions) {
    this.query = query;
    this.directions = directions;
  }

  /**
   * @return the query
   */
  public String getQuery() {
    return query;
  }

  /**
   * @param query the query to set
   */
  public void setQuery(String query) {
    this.query = query;
  }

  /**
   * @return the directions
   */
  public BitSet getDirections() {
    return directions;
  }

  /**
   * @param directions the directions to set
   */
  public void setDirections(BitSet directions) {
    this.directions = directions;
  }

}
