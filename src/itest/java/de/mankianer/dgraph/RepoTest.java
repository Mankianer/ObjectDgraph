package de.mankianer.dgraph;

import io.dgraph.DgraphAsyncClient;
import io.dgraph.DgraphGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    repo = new DgraphRepo<>(dgraphClient);
  }

  @Test
  public void ConnectionTest() {
    assertNotNull(dgraphClient.checkVersion());
  }

  @Test
  public void SaveSimpleTest() {
    TestEntity entity = new TestEntity();
    entity.setAString("test");
    TestEntity testEntity = repo.saveToDgraph(entity);
    assertNotNull(testEntity.getUid());
  }
}
