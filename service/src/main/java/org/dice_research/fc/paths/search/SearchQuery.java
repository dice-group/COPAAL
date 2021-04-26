package org.dice_research.fc.paths.search;

import java.util.BitSet;

public class SearchQuery {

  private String query;
  private BitSet directions;
  private int length;

  public SearchQuery(String query, BitSet directions, int length) {
    this.query = query;
    this.directions = directions;
    this.length = length;
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

  /**
   * @return the length
   */
  public int getLength() {
    return length;
  }

  /**
   * @param length the length to set
   */
  public void setLength(int length) {
    this.length = length;
  }

}
