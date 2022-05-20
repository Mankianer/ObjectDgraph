package de.mankianer.dgraph.query;

import java.util.Map;

public class DgraphQueryUtils {

  /**
   * @param queryMap values used to be in Query
   * @return FieldsPart for DGraphQuery
   */
  public static String convertQueryMapToField(Map<String, Map> queryMap) {
    final String[] queryString = {""};
    queryMap.forEach(
        (key, value) -> {
          queryString[0] += key;
          if (value != null) {
            queryString[0] += "\n{\n" + convertQueryMapToField(value) + "}";
          }
          queryString[0] += "\n";
        });

    return queryString[0];
  }
}
