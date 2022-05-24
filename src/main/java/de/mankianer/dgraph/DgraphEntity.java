package de.mankianer.dgraph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
}
