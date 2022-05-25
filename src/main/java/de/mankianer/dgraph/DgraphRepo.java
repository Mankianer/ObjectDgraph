package de.mankianer.dgraph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import de.mankianer.dgraph.query.DQuery;
import de.mankianer.dgraph.query.DQueryHelper;
import io.dgraph.AsyncTransaction;
import io.dgraph.DgraphAsyncClient;
import io.dgraph.DgraphProto;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

// TODO GEO and Default findByValue
@Log4j2
public class DgraphRepo<T extends DgraphEntity> {

  private final DgraphAsyncClient dgraphClient;
  private final Class<T> actualTypeArgument;
  private ObjectMapper saveMapper;
  private ObjectMapper loadMapper;

  public DgraphRepo(DgraphAsyncClient dgraphClient, Class<T> clazz) {
    this.dgraphClient = dgraphClient;
    actualTypeArgument = clazz;
    saveMapper = new ObjectMapper();
    loadMapper = new ObjectMapper();
  }

  public void saveToDgraph(T entity, Consumer<Optional<T>> callback) {
    AsyncTransaction txn = dgraphClient.newTransaction();
    String json = "{}";
    try {
      json = saveMapper.writeValueAsString(entity);
    } catch (JsonProcessingException e) {
      callback.accept(Optional.empty());
      throw new RuntimeException(e);
    }
    DgraphProto.Mutation mutation =
        DgraphProto.Mutation.newBuilder()
            .setSetJson(ByteString.copyFromUtf8(json.toString()))
            .setCommitNow(true)
            .build();

    CompletableFuture<DgraphProto.Response> mutate = txn.mutate(mutation);
    mutate.thenRun(
        () -> {
          try {
            String topLevelUid =
                mutate.get().getUidsMap().entrySet().stream()
                    .sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
                    .map(entry -> entry.getValue())
                    .findFirst()
                    .orElse(null);

            if (topLevelUid != null) {
              findByUid(topLevelUid, callback);
            } else {
              callback.accept(Optional.empty());
            }
          } catch (ExecutionException | InterruptedException e) {
            log.error("Error while executing query", e);
            callback.accept(Optional.empty());
          }

          txn.discard();
        });
  }

  public void findByUid(String uid, Consumer<Optional<T>> callback) {
    DQuery query = DQueryHelper.createFindByUidQuery(actualTypeArgument);

    AsyncTransaction txn = dgraphClient.newReadOnlyTransaction();
    CompletableFuture<DgraphProto.Response> responseCompletableFuture =
        txn.queryWithVars(query.buildQueryString(), Map.of("$uid", uid));
    responseCompletableFuture.thenRun(
        () -> {
          try {
            String json = responseCompletableFuture.get().getJson().toStringUtf8();
            json =
                json.substring(
                    ("{\"" + query.getFunction().getFunctionName() + "\":").length(),
                    json.length() - 1);
            T[] value = loadMapper.readValue(json, (Class<T[]>) actualTypeArgument.arrayType());
            if (value.length > 0) {
              callback.accept(Optional.of(value[0]));
            } else {
              callback.accept(Optional.empty());
            }
          } catch (JsonProcessingException e) {
            log.error("Error while converting Json to entity", e);
            callback.accept(Optional.empty());
          } catch (ExecutionException | InterruptedException e) {
            log.error("Error while executing query", e);
            callback.accept(Optional.empty());
          }
        });
    ;
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
