package de.mankianer.dgraph;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {

  @Test
  void toJson() throws JsonProcessingException {
    TestClass test = new TestClass("test", 20);
    assertEquals("{\"name\":\"test\",\"age\":20}", Utils.toJson(test).replaceAll("\\s", ""));
  }

  void fromJson() throws JsonProcessingException {
    TestClass test = new TestClass("test", 20);
    assertEquals(test, Utils.fromJson("{\"name\":\"test\",\"age\":20}", TestClass.class));
  }

  @Data
  @AllArgsConstructor
  class TestClass {
    private String name;
    private int age;
  }
}
