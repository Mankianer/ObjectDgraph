package de.mankianer.dgraph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DgraphEntity {

  private String uid;

  @JsonIgnore
  public List<Field> getAllFields() {
    List<Field> declaredFields = new ArrayList<>();
    declaredFields.addAll(List.of(getClass().getDeclaredFields()));
    Class<?> superclass = getClass();
    do {
      superclass = superclass.getSuperclass();
      declaredFields.addAll(List.of(superclass.getDeclaredFields()));
    } while (!DgraphEntity.class.getName().equals(superclass.getName())); // better head while?

    return declaredFields;
  }

  @JsonIgnore
  public Map<String, Field> getAllFieldMap() {
    Map<String, Field> declaredFields = new HashMap<>();
    Arrays.stream(getClass().getDeclaredFields())
        .forEach(field -> declaredFields.put(field.getName(), field));
    Class<?> superclass = getClass();
    do {
      superclass = superclass.getSuperclass();
      Arrays.stream(superclass.getDeclaredFields())
          .forEach(field -> declaredFields.put(field.getName(), field));
    } while (!DgraphEntity.class.getName().equals(superclass.getName())); // better head while?

    return declaredFields;
  }
}
