package de.mankianer.dgraph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import de.mankianer.dgraph.query.DQuery;
import de.mankianer.dgraph.query.DQueryHelper;
import io.dgraph.AsyncTransaction;
import io.dgraph.DgraphAsyncClient;
import io.dgraph.DgraphProto;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

// TODO GEO and Default findByValue
@Log4j2
public class DgraphRepo<T extends DgraphEntity> {

  private final DgraphAsyncClient dgraphClient;
  private final Class<T> actualTypeArgument;
  private ObjectMapper saveMapper;
  private ObjectMapper loadMapper;

  public DgraphRepo(DgraphAsyncClient dgraphClient) {
    this.dgraphClient = dgraphClient;
    actualTypeArgument =
        (Class<T>)
            ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    saveMapper = new ObjectMapper();
    loadMapper = new ObjectMapper();
  }

  public T saveToDgraph(T entity) {
    AsyncTransaction txn = dgraphClient.newTransaction();
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

      String topLevelUid = "";
      //      DgraphProto.Response response = txn.mutate(mutation);
      //      String topLevelUid =
      //          response.getUidsMap().entrySet().stream()
      //              .sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
      //              .map(entry -> entry.getValue())
      //              .findFirst()
      //              .orElse(null);
      if (topLevelUid != null) {
        entity = findByUid(topLevelUid).findAny().get();
      }
      return entity;
    } finally {
      txn.discard();
    }
  }

  public Stream<T> findByUid(String uid) {
    DQuery query = DQueryHelper.createFindByUidQuery(actualTypeArgument);

    query.getFunction().getParamList();

    AsyncTransaction txn = dgraphClient.newReadOnlyTransaction();
    CompletableFuture<DgraphProto.Response> responseCompletableFuture =
        txn.queryWithVars("", Map.of());
    Stream<T> ret =
        Stream.generate(
                () -> {
                  while (!responseCompletableFuture.isDone()) {}

                  try {
                    DgraphProto.Response response = responseCompletableFuture.get();
                    return loadMapper.readValue(
                        response.getJson().toStringUtf8(), actualTypeArgument);
                  } catch (InterruptedException e) {
                    log.error("Error while waiting for response", e);
                  } catch (ExecutionException e) {
                    log.error("Error while waiting for response", e);
                  } catch (JsonMappingException e) {
                    log.error("Error while waiting for response", e);
                  } catch (JsonProcessingException e) {
                    log.error("Error while waiting for response", e);
                  }
                  return null;
                })
            .limit(1);
    return ret;
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
