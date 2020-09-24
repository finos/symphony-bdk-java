package com.symphony.bdk.core.service.stream;


import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamFilter;
import com.symphony.bdk.gen.api.model.V2StreamAttributes;
import com.symphony.bdk.http.api.ApiException;

import java.util.List;

class OboStreamService {

  protected final StreamsApi streamsApi;
  protected final RetryWithRecoveryBuilder retryBuilder;

  protected OboStreamService(StreamsApi streamsApi, RetryWithRecoveryBuilder retryBuilder) {
    this.streamsApi = streamsApi;
    this.retryBuilder = retryBuilder;
  }

  /**
   * {@link StreamService#getStreamInfo(String)}
   *
   * @param authSession Bot Session or Obo Session
   * @param streamId    The stream id
   * @return The information about the stream with the given id.
   * @see <a href="https://developers.symphony.com/restapi/reference#stream-info-v2">Stream Info V2</a>
   */
  public V2StreamAttributes getStreamInfo(AuthSession authSession, String streamId) {
    return executeAndRetry("getStreamInfo",
        () -> streamsApi.v2StreamsSidInfoGet(streamId, authSession.getSessionToken()), authSession);
  }

  /**
   * {@link StreamService#listStreams(StreamFilter)}
   *
   * @param authSession Bot Session or Obo Session
   * @param filter      The stream searching criteria
   * @return The list of streams retrieved according to the searching criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-user-streams">List Streams</a>
   */
  public List<StreamAttributes> listStreams(AuthSession authSession, StreamFilter filter) {
    return executeAndRetry("listStreams",
        () -> streamsApi.v1StreamsListPost(authSession.getSessionToken(), null, null, filter), authSession);
  }

  protected <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier, AuthSession authSession) {
    final RetryWithRecoveryBuilder retryBuilderWithAuthSession = RetryWithRecoveryBuilder.from(retryBuilder)
        .clearRecoveryStrategies() // to remove refresh on bot session put by default
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
    return RetryWithRecovery.executeAndRetry(retryBuilderWithAuthSession, name, supplier);
  }
}
