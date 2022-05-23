package de.mankianer.dgraph.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DQueryHelperTest {

  @Test
  void createFindByValueQuery() {
    String queryString =
        DQueryHelper.createFindByValueQuery(
                "aString",
                "pString",
                DGraphType.STRING,
                DgraphQueryUtils.getFieldMap(TestEntity.class))
            .buildQueryString();
    assertEquals("""
            query findFilters($pString: string) {
            findFilters (func: eq(aString, $pString)) {
            aBoolean
            uid
            aString
            aList
            {
            uid
            }
            anInt
            aDouble
            aTestEntity
            {
            uid
            }
            }
            }""", queryString);
  }

  @Test
  void createFindByUidQuery() {
    String queryString = DQueryHelper.createFindByUidQuery( DgraphQueryUtils.getFieldMap(TestEntity.class)).buildQueryString();
    assertEquals("""
             query findFilters($uid: string) {
             findFilters (func: uid($uid)) {
             aBoolean
             uid
             aString
             aList
             {
             uid
             }
             anInt
             aDouble
             aTestEntity
             {
             uid
             }
             }
             }""", queryString);
  }

  @Test
  void createFindByAndFilterFunctionsQuery() {
    DQueryFilterFunction filterFunction = DQueryFilterFunctionUid.builder().fieldName("uid").paramName("uid").build();
    DQueryFilterFunction filterFunction2 = DQueryHelper.getFieldEqualParamFilterFunction("aString", "pString", DGraphType.STRING);
    DQueryFilterFunction filterFunction3 = DQueryHelper.getFieldEqualParamFilterFunction("aInt", "pInt", DGraphType.INT);
    String queryString =
        DQueryHelper.createFindByAndFilterFunctionsQuery(DgraphQueryUtils.getFieldMap(TestEntity.class), filterFunction, filterFunction2, filterFunction3)
            .buildQueryString();
    assertEquals("""
            query findFilters($uid: string, $pString: string, $pInt: int) {
            findFilters (func: uid($uid)) @filter( eq(aString, $pString) AND eq(aInt, $pInt)){
            aBoolean
            uid
            aString
            aList
            {
            uid
            }
            anInt
            aDouble
            aTestEntity
            {
            uid
            }
            }
            }""", queryString);
  }
}
