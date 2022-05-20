package de.mankianer.dgraph.query;

import lombok.Getter;

public enum DGraphType {
  STRING("string"),
  INT("int"),
  FLOAT("float"),
  DATETIME("dateTime"),
  DEFAULT("default"),
  GEO("geo"),
  BOOLEAN("bool");

  @Getter public String name;

  private DGraphType(String name) {
    this.name = name;
  }
}
