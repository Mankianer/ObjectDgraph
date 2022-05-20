package de.mankianer.dgraph.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class DQueryFunction {

  @NonNull private String functionName;

  @NonNull private DQueryRootFilter queryRootFilter;

  private DQueryFilter filter;

  public Map<String, DGraphType> getParamList() {
    HashMap<String, DGraphType> ret = new HashMap<>();
    ret.put(
        queryRootFilter.getRootFilterFunction().getParamName(),
        queryRootFilter.getRootFilterFunction().getParamType());
    if (filter != null) {
      ret.put(
          filter.getFirstFilterFunction().getParamName(),
          filter.getFirstFilterFunction().getParamType());
      if (filter.getChainedFilter() != null) {
        filter
            .getChainedFilter()
            .forEach(
                f ->
                    ret.put(
                        f.getFilterFunction().getParamName(),
                        f.getFilterFunction().getParamType()));
      }
    }
    return ret;
  }

  public String buildFunctionString() {
    return functionName
        + " ("
        + queryRootFilter.buildRootFilterString()
        + ") "
        + ((filter != null) ? filter.buildFilterString() : "");
  }
}
