package de.mankianer.dgraph.query;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DgraphQueryUtilsTest {

  @Test
  void testConvertQueryMapToField() {

    Map<String, Map> empty = new HashMap<>();
    Map<String, Map> object3 = new HashMap<>();
    object3.put("object3", empty);
    object3.put("object1", empty);
    Map<String, Map> object4 = new HashMap<>();
    object4.put("object1", object3);
    Map<String, Map> full = new HashMap<>();
    full.put("name", empty);
    full.put("object1", empty);
    full.put("object2", empty);
    full.put("object3", object3);
    full.put("object4", object4);
    full.put("null", null);

      assertEquals("""
                 object2
                 {
                 }
                 object1
                 {
                 }
                 null
                 object4
                 {
                 object1
                 {
                 object1
                 {
                 }
                 object3
                 {
                 }
                 }
                 }
                 object3
                 {
                 object1
                 {
                 }
                 object3
                 {
                 }
                 }
                 name
                 {
                 }
                 """, DgraphQueryUtils.convertQueryMapToField(full));
  }

  @Test
  void getFieldMapSimpleTest(){
    HashMap<String, Map> map = new HashMap<>();
    map.put("uid", null);
    assertEquals(map, DgraphQueryUtils.getFieldMap(TestEntitySimple.class));
  }

  @Test
  void getFieldMapTest(){
    HashMap<String, Map> map = new HashMap<>();
    map.put("uid", null);
    map.put("aBoolean", null);
    map.put("aString", null);
    map.put("anInt", null);
    map.put("aDouble", null);

    HashMap<String, Map> simple = new HashMap<>();
    simple.put("uid", null);
    map.put("aTestEntity", simple);
    map.put("aList", simple);

    assertEquals(map, DgraphQueryUtils.getFieldMap(TestEntity.class));
  }
}
