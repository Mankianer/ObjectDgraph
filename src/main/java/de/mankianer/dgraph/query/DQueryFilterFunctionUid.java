package de.mankianer.dgraph.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class DQueryFilterFunctionUid extends DQueryFilterFunction {

  @NonNull private String fieldName, paramName;

  @Override
  public String buildFilterFunctionString() {
    return "uid($" + paramName + ")";
  }

  @Override
  public DGraphType getParamType() {
    return DGraphType.STRING;
  }
}
