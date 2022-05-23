package de.mankianer.dgraph.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mankianer.dgraph.DgraphEntity;
import lombok.Data;

import java.util.List;

@Data
public class TestEntity extends DgraphEntity {

  private String aString;
  private int anInt;
  private double aDouble;
  private boolean aBoolean;
  private TestEntitySimple aTestEntity;
  private List<TestEntitySimple> aList;
  private List<TestEntitySimple> _ignore;
  @JsonIgnore private List<TestEntitySimple> ignore;
}
