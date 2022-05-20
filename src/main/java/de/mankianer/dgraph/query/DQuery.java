package de.mankianer.dgraph.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Log4j2
@Builder
public class DQuery {

  @NonNull private String queryname;
  @NonNull private Map queryMap;

  @NonNull private DQueryFunction function;

  public String buildQueryParamList() {
    return function.getParamList().entrySet().stream()
        .map(e -> "$" + e.getKey() + ": " + e.getValue().getName() + "")
        .collect(Collectors.joining(", "));
  }

  public String buildQueryString() {
    return "query "
        + getQueryname()
        + "("
        + buildQueryParamList()
        + ") {\n"
        + getFunction().buildFunctionString()
        + "{\n"
        + DgraphQueryUtils.convertQueryMapToField(getQueryMap())
        + "}\n}";
  }
}
