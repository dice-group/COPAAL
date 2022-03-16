package org.dice_research.fc.tentris;

public interface TentrisAdapter extends AutoCloseable {

  public static final String COUNT_SERVICE = "/count";
  public static final String ASK_SERVICE = "/ask";

  public default long executeCountQuery(String query) {
    String result = executeSingleResultQuery(query, COUNT_SERVICE);
    if (result != null) {
      return Long.parseLong(result);
    } else {
      return 0L;
    }
  }

  public default boolean executeAskQuery(String query) {
  String result = executeSingleResultQuery(query, ASK_SERVICE);
  if (result != null) {
    return Boolean.parseBoolean(result);
  } else {
    return false;
  }
}

  public String executeSingleResultQuery(String query, String service);

}
