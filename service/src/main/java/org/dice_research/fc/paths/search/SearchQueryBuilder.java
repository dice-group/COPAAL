package org.dice_research.fc.paths.search;

import java.util.BitSet;

public class SearchQueryBuilder {

  private StringBuilder queryBuilder;
  private BitSet directions;
  private int length;

  public SearchQueryBuilder(int maxLength) {
    directions = new BitSet(maxLength);
    queryBuilder = new StringBuilder();
    this.length = maxLength;
  }

  public SearchQueryBuilder(SearchQueryBuilder other) {
    directions = (BitSet) other.directions.clone();
    queryBuilder = new StringBuilder(other.queryBuilder);
  }

  public SearchQuery build() {
    return new SearchQuery(queryBuilder.toString(), directions, length);
  }

  /**
   * @return the queryBuilder
   */
  public StringBuilder getQueryBuilder() {
    return queryBuilder;
  }

  /**
   * @param queryBuilder the queryBuilder to set
   */
  public void setQueryBuilder(StringBuilder queryBuilder) {
    this.queryBuilder = queryBuilder;
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
