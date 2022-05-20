package de.mankianer.dgraph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Utils {

  static ObjectMapper objectMapper = new ObjectMapper();

  public static String toJson(Object entity) throws JsonProcessingException {
    return objectMapper.writeValueAsString(entity);
  }

  public static <T> T fromJson(String json, Class<T> type) throws JsonProcessingException {
    return objectMapper.readValue(json, type);
  }
}
