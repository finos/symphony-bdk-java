package com.symphony.bdk.core.service.stream;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamFilter;
import com.symphony.bdk.gen.api.model.V2StreamAttributes;

import java.util.List;

class OboStreamService {

  protected final StreamsApi streamsApi;

  protected OboStreamService(StreamsApi streamsApi) {
    this.streamsApi = streamsApi;
  }

  /**
   * {@link StreamService#getStreamInfo(String)}
   *
   * @param oboSession  Obo Session
   * @param streamId    The stream id
   * @return            The information about the stream with the given id.
   * @see               <a href="https://developers.symphony.com/restapi/reference#stream-info-v2">Stream Info V2</a>
   */
  public V2StreamAttributes getStreamInfo(AuthSession oboSession, String streamId) {
    try {
      return streamsApi.v2StreamsSidInfoGet(streamId, oboSession.getSessionToken());
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * {@link StreamService#listStreams(StreamFilter)}
   *
   * @param oboSession  Obo Session
   * @param filter      The stream searching criteria
   * @return            The list of streams retrieved according to the searching criteria.
   * @see               <a href="https://developers.symphony.com/restapi/reference#list-user-streams">List Streams</a>
   */
  public List<StreamAttributes> listStreams(AuthSession oboSession, StreamFilter filter) {
    try {
      return streamsApi.v1StreamsListPost(oboSession.getSessionToken(), null, null, filter);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

}
