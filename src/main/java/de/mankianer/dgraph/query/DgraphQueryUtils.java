package de.mankianer.dgraph.query;

import de.mankianer.dgraph.DgraphEntity;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class DgraphQueryUtils {

  /**
   * @param queryMap values used to be in Query
   * @return FieldsPart for DGraphQuery
   */
  public static String convertQueryMapToField(Map<String, Map> queryMap) {
    final String[] queryString = {""};
    queryMap.forEach(
        (key, value) -> {
          queryString[0] += key;
          if (value != null) {
            queryString[0] += "\n{\n" + convertQueryMapToField(value) + "}";
          }
          queryString[0] += "\n";
        });

    return queryString[0];
  }

  /**
   * gets Class of a Class-Field if it's a List it returns the Type of the List.
   *
   * @return correct Type of DgraphEntity Field of Object or List
   */
  public static Class<?> convertFieldToClass(Field field) {
    Class<?> fieldClass = field.getType();
    if (field.getType().equals(List.class)) {
      ParameterizedType listType = (ParameterizedType) field.getGenericType();
      fieldClass = (Class<?>) listType.getActualTypeArguments()[0];
    }
    return fieldClass;
  }

  /**
   * Mapping the Fields of a DgraphEntity for Query in a treeLike Map. Ignorse Fileds
   * with @JsonIgnore annotation.
   *
   * @return Map<S, Map < S, Map < S, . . .>>> if Key is not null it is a DgraphEntity
   */
  public static Map<String, Map> getFieldMap(Class<? extends DgraphEntity> clazz) {
    HashMap<String, Map> fieldMap = new HashMap<>();
    try {
      var instance = clazz.getDeclaredConstructor().newInstance();
      List<Field> allFields = instance.getAllFields();
      allFields.forEach(
          field -> {
            if (field.getName().startsWith("_")) return;
            if (Arrays.stream(field.getDeclaredAnnotations())
                .anyMatch(
                    annotation ->
                        "JsonIgnore".equals(annotation.annotationType().getSimpleName()))) {
              return;
            }
            Class<?> fieldClass = convertFieldToClass(field);
            try {
              var fieldInstance = fieldClass.getDeclaredConstructor().newInstance();
              if (fieldInstance instanceof DgraphEntity) {
                fieldMap.put(
                    field.getName(),
                    getFieldMap((Class<? extends DgraphEntity>) fieldInstance.getClass()));
              } else {
                fieldMap.put(field.getName(), null);
              }
            } catch (Exception e) {
              fieldMap.put(field.getName(), null);
            }
          });
    } catch (Exception e) {
      log.error("Error while parsing DGraphEntity to DgraphQuery, not critical!", e);
      fieldMap.put("uid", null);
    }
    return fieldMap;
  }

  /** Maps a Class<?> to a DGraphType for Query */
  public static DGraphType findDGraphType(Class<?> clazz) {
    if (clazz.equals(Integer.class)) {
      return DGraphType.INT;
    } else if (clazz.equals(Float.class) || clazz.equals(Double.class)) {
      return DGraphType.FLOAT;
    } else if (clazz.equals(Boolean.class)) {
      return DGraphType.BOOLEAN;
    } else if (clazz.equals(LocalDateTime.class) || clazz.equals(LocalDate.class)) {
      return DGraphType.DATETIME;
    }
    return DGraphType.DEFAULT;
  }
}
