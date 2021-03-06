package de.mankianer.dgraph.query;

import de.mankianer.dgraph.DgraphEntity;
import de.mankianer.dgraph.query.DQueryChainedFilterFunction.FilterConnection;
import de.mankianer.dgraph.query.DQueryFilter.DQueryFilterBuilder;
import de.mankianer.dgraph.query.DQueryFilterFunctionCompare.CompareTypes;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DQueryHelper {
  /**
   * @param queryFieldMap values to be used in Query
   * @return a Query for request Dgraph
   */
  public static DQuery createFindByAndFilterFunctionsQuery(
      Class<? extends DgraphEntity> clazz,
      @NonNull DQueryFilterFunction rootFilterFunction,
      DQueryFilterFunction... filterFunctions) {
    DQueryFunction function =
        getQueryFunctionByFunctionNameAndAndFilterFunctions(
            "findFilters", rootFilterFunction, filterFunctions);
    Map<String, Map> queryFieldMap = DgraphQueryUtils.getFieldMap(clazz);
    return DQuery.builder()
        .queryname("findFilters")
        .queryMap(queryFieldMap)
        .function(function)
        .build();
  }

  /**
   * @param filedName searching DgraphEntity field
   * @param paramName searching DgraphQuery parameter
   * @param queryFieldMap values to be used in Query
   * @return a Query for request Dgraph
   */
  public static DQuery createFindByValueQuery(
      String filedName,
      String paramName,
      DGraphType paramType,
      Class<? extends DgraphEntity> clazz) {
    DQueryFilterFunction filterFunction =
        getFieldEqualParamFilterFunction(filedName, paramName, paramType);
    return createFindByAndFilterFunctionsQuery(clazz, filterFunction);
  }

  /**
   * @param filedName searching DgraphEntity field
   * @param clazz targeted DgraphEntity
   * @return a Query for request Dgraph
   */
  public static DQuery createFindByValueQuery(
      Class<? extends DgraphEntity> clazz, String filedName) {
    return createFindByValueQuery(
        filedName,
        filedName,
        DgraphQueryUtils.findDgraphTypeByFieldNameAndClass(filedName, clazz),
        clazz);
  }

  /**
   * @param queryFieldMap values to be used in Query
   * @return a Query for request Dgraph
   */
  public static DQuery createFindByUidQuery(Class<? extends DgraphEntity> clazz) {
    DQueryFilterFunction filterFunction = getUidFilterFunction();
    return createFindByAndFilterFunctionsQuery(clazz, filterFunction);
  }

  private static DQueryFunction getQueryFunctionByRootFilterAndFilterAndFunctionName(
      DQueryRootFilter rootFilter, DQueryFilter filter, String functionName) {
    return DQueryFunction.builder()
        .functionName(functionName)
        .queryRootFilter(rootFilter)
        .filter(filter)
        .build();
  }

  private static DQueryFunction getQueryFunctionByFunctionNameAndAndFilterFunctions(
      String functionName,
      @NonNull DQueryFilterFunction rootFilterFunction,
      DQueryFilterFunction... filterFunctions) {
    DQueryRootFilter rootFilter =
        DQueryRootFilter.builder().rootFilterFunction(rootFilterFunction).build();
    DQueryFilter filter = getQueryFilterByAndFilterFunctions(filterFunctions);
    return getQueryFunctionByRootFilterAndFilterAndFunctionName(rootFilter, filter, functionName);
  }

  private static DQueryFilter getQueryFilterByAndFilterFunctions(
      DQueryFilterFunction... filterFunctions) {
    if (filterFunctions.length > 0) {
      DQueryFilterBuilder dQueryFilterBuilder = DQueryFilter.builder();
      dQueryFilterBuilder.firstFilterFunction(filterFunctions[0]);
      List<DQueryFilterFunction> dQueryFilterFunctions = new ArrayList<>(List.of(filterFunctions));
      dQueryFilterFunctions.remove(0);
      dQueryFilterBuilder.chainedFilter(
          dQueryFilterFunctions.stream()
              .map(
                  filterFunction ->
                      DQueryChainedFilterFunction.builder()
                          .filterFunction(filterFunction)
                          .filterConnection(FilterConnection.AND)
                          .build())
              .collect(Collectors.toList()));
      return dQueryFilterBuilder.build();
    }
    return null;
  }

  public static DQueryFilterFunctionUid getUidFilterFunction() {
    return DQueryFilterFunctionUid.builder().fieldName("uid").paramName("uid").build();
  }

  public static DQueryFilterFunctionCompare getFieldEqualParamFilterFunction(
      String filedName, String paramName, DGraphType paramType) {
    return getFieldCompareParamFilterFunction(filedName, paramName, paramType, CompareTypes.EQUALS);
  }

  public static DQueryFilterFunctionCompare getFieldCompareParamFilterFunction(
      String filedName, String paramName, DGraphType paramType, CompareTypes compareType) {
    return DQueryFilterFunctionCompare.builder()
        .fieldName(filedName)
        .paramName(paramName)
        .paramType(paramType)
        .compareTypes(compareType)
        .build();
  }
}
