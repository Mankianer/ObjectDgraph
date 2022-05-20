package de.mankianer.dgraph.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class DQueryChainedFilterFunction {

  @NonNull private DQueryFilterFunction filterFunction;
  @NonNull private FilterConnection filterConnection;

  public String buildFilterChainFunctionString() {
    return filterConnection.name() + " " + filterFunction.buildFilterFunctionString();
  }

  public enum FilterConnection {
    AND,
    OR;
  }
}
