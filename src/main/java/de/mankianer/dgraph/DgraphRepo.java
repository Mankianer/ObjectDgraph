package de.mankianer.dgraph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphProto;
import io.dgraph.Transaction;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

// TODO GEO and Default findByValue
@Log4j2
public class DgraphRepo<T extends DgraphEntity> {

  private final DgraphClient dgraphClient;
  private ObjectMapper saveMapper;

  public DgraphRepo(DgraphClient dgraphClient) {
    this.dgraphClient = dgraphClient;
    saveMapper = new ObjectMapper();
  }

  public T saveToDgraph(T entity) {
    Transaction txn = dgraphClient.newTransaction();
    try {
      String json = "{}";
      try {
        json = saveMapper.writeValueAsString(entity);
      } catch (JsonProcessingException e) {
        log.warn("Error while converting entity to Json", e);
      }
      DgraphProto.Mutation mutation =
          DgraphProto.Mutation.newBuilder()
              .setSetJson(ByteString.copyFromUtf8(json.toString()))
              .setCommitNow(true)
              .build();

      DgraphProto.Response response = txn.mutate(mutation);
      String topLevelUid =
          response.getUidsMap().entrySet().stream()
              .sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
              .map(entry -> entry.getValue())
              .findFirst()
              .orElse(null);
      if (topLevelUid != null) {
        entity = findByUid(topLevelUid);
      }
      return entity;
    } finally {
      txn.discard();
    }
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
