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

  public V2StreamAttributes getStreamInfo(AuthSession oboSession, String streamId) {
    try {
      return streamsApi.v2StreamsSidInfoGet(streamId, oboSession.getSessionToken());
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public List<StreamAttributes> listStreams(AuthSession oboSession, StreamFilter filter) {
    try {
      return streamsApi.v1StreamsListPost(oboSession.getSessionToken(), null, null, filter);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

}
