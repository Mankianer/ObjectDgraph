package de.mankianer.dgraph.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class DQueryFilter {

  @NonNull private DQueryFilterFunction firstFilterFunction;

  private List<DQueryChainedFilterFunction> chainedFilter;

  public String buildFilterString() {
    String chainedFilterString =
        chainedFilter.stream()
            .map(c -> c.buildFilterChainFunctionString())
            .collect(Collectors.joining(" "));
    return "@filter( "
        + firstFilterFunction.buildFilterFunctionString()
        + " "
        + chainedFilterString
        + ")";
  }
}
