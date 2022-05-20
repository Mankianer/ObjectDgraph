package de.mankianer.dgraph;

import java.time.LocalDateTime;

// TODO GEO and Default findByValue
public class DgraphRepo<T extends DgraphEntity> {

  public T saveToDgraph(T entity) {
    return null;
  }

  public T findByUid(String uid) {
    return null;
  }

  public T findByValue(String name, String value) {
    return null;
  }

  public <P extends Number> T findByValue(String name, P value) {
    return null;
  }

  public T findByValue(String name, Boolean value) {
    return null;
  }

  public T findByValue(String name, LocalDateTime value) {
    return null;
  }
}
