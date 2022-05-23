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
}
