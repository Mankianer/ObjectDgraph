package de.mankianer.dgraph;

import io.dgraph.DgraphAsyncClient;
import io.dgraph.DgraphGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RepoTest {

  private static DgraphAsyncClient dgraphClient;
  private DgraphRepo<TestEntity> repo;

  @BeforeAll
  public static void setup() {
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 9080).usePlaintext().build();
    DgraphGrpc.DgraphStub stub = DgraphGrpc.newStub(channel);
    dgraphClient = new DgraphAsyncClient(stub);
  }

  @BeforeEach
  public void beforeEach() {
    repo = new DgraphRepo<>(dgraphClient, TestEntity.class);
  }

  @Test
  public void ConnectionTest() {
    assertNotNull(dgraphClient.checkVersion());
  }

  @Test
  public void SaveSimpleTest() {
    TestEntity entity = new TestEntity();
    entity.setAString("test");
    long timeout = 3000;
    Long[] start = {System.currentTimeMillis()};
    repo.saveToDgraph(
        entity,
        e -> {
          assertNotNull(e.get().getUid());
          start[0] = 0l;
        });
    while (System.currentTimeMillis() - start[0] < timeout) {}
    assertEquals(0l, start[0], "Save to dgraph timed out");
  }

  @Test
  public void findByUID() {
    long timeout = 3000;
    Long[] start = {System.currentTimeMillis()};
    repo.findByUid(
        "0x4e21",
        e -> {
          assertNotNull(e.get().getUid());
          start[0] = 0l;
        });
    while (System.currentTimeMillis() - start[0] < timeout) {}
    assertEquals(0l, start[0], "Save to dgraph timed out");
  }
}
