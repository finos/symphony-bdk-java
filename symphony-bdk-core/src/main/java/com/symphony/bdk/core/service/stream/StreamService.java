package com.symphony.bdk.core.service.stream;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.stream.constant.AttachmentSort;
import com.symphony.bdk.gen.api.StreamsApi;

import com.symphony.bdk.gen.api.model.RoomDetail;
import com.symphony.bdk.gen.api.model.Stream;

import com.symphony.bdk.gen.api.model.StreamAttachmentItem;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamFilter;
import com.symphony.bdk.gen.api.model.V2AdminStreamFilter;
import com.symphony.bdk.gen.api.model.V2AdminStreamList;
import com.symphony.bdk.gen.api.model.V2MembershipList;
import com.symphony.bdk.gen.api.model.V2RoomSearchCriteria;
import com.symphony.bdk.gen.api.model.V2StreamAttributes;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;

import com.symphony.bdk.gen.api.model.V3RoomDetail;

import com.symphony.bdk.gen.api.model.V3RoomSearchResults;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class StreamService {

  private final StreamsApi streamsApi;
  private final AuthSession authSession;

  public StreamService(StreamsApi streamsApi, AuthSession authSession) {
    this.streamsApi = streamsApi;
    this.authSession = authSession;
  }

  public Stream createIMorMIM(List<Long> uids) {
    try {
      return streamsApi.v1ImCreatePost(authSession.getSessionToken(), uids);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public V3RoomDetail createRoomChat(V3RoomAttributes roomAttributes) {
    try {
      return streamsApi.v3RoomCreatePost(authSession.getSessionToken(), roomAttributes);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public V3RoomSearchResults searchRooms(V2RoomSearchCriteria query) {
    try {
      return streamsApi.v3RoomSearchPost(authSession.getSessionToken(), query, null, null);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public V3RoomDetail getRoomInfo(String roomId) {
    try {
      return streamsApi.v3RoomIdInfoGet(roomId, authSession.getSessionToken());
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public RoomDetail setRoomActive(String roomId, Boolean active) {
    try {
      return streamsApi.v1RoomIdSetActivePost(roomId, active, authSession.getSessionToken());
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public V3RoomDetail updateRoom(String roomId, V3RoomAttributes roomAttributes) {
    try {
      return streamsApi.v3RoomIdUpdatePost(roomId, authSession.getSessionToken(), roomAttributes);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public List<StreamAttributes> listStreams(StreamFilter filter) {
    try {
      return streamsApi.v1StreamsListPost(authSession.getSessionToken(), null, null, filter);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public V2StreamAttributes getStreamInfo(String streamId) {
    try {
      return streamsApi.v2StreamsSidInfoGet(streamId, authSession.getSessionToken());
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public List<StreamAttachmentItem> getAttachmentsOfStream(String streamId, Long sinceInMillis, Long toInMillis, AttachmentSort sort) {
    try {
      return streamsApi.v1StreamsSidAttachmentsGet(streamId, authSession.getSessionToken(), sinceInMillis, toInMillis, null, sort.name());
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public Stream createAdminIMorMIM(List<Long> uids) {
    try {
      return streamsApi.v1AdminImCreatePost(authSession.getSessionToken(), uids);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public RoomDetail setRoomActiveAdmin(String streamId, Boolean active) {
    try {
      return streamsApi.v1AdminRoomIdSetActivePost(streamId, active, authSession.getSessionToken());
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public V2AdminStreamList listStreamsAdmin(V2AdminStreamFilter filter) {
    try {
      return streamsApi.v2AdminStreamsListPost(authSession.getSessionToken(), null, null, filter);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  public V2MembershipList listStreamMembers(String streamId) {
    try {
      return streamsApi.v1AdminStreamIdMembershipListGet(streamId, authSession.getSessionToken(), null, null);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }
}
