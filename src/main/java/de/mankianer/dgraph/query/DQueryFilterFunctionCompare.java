package de.mankianer.dgraph.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class DQueryFilterFunctionCompare extends DQueryFilterFunction {

  @NonNull private String fieldName, paramName;

  @NonNull private DGraphType paramType;

  @NonNull private DQueryFilterFunctionCompare.CompareTypes compareTypes;

  @Override
  public String buildFilterFunctionString() {
    return compareTypes.getName() + "(" + fieldName + ", $" + paramName + ")";
  }

  public enum CompareTypes {
    EQUALS("eq"),
    GREATER_OR_EQUALS("ge"),
    GREATER("gt"),
    LESS_OR_EQUALS("le"),
    LESS("lt");

    @Getter private String name;

    CompareTypes(String name) {

      this.name = name;
    }
  }
}
