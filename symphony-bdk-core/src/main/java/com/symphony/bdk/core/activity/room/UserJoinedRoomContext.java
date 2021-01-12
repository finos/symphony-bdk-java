package com.symphony.bdk.core.activity.room;

import com.symphony.bdk.core.activity.ActivityContext;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4UserJoinedRoom;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

/**
 * Default implementation of the {@link ActivityContext} handled by the {@link UserJoinedRoomActivity}.
 */
@Getter
@Setter
@API(status = API.Status.STABLE)
public class UserJoinedRoomContext extends ActivityContext<V4UserJoinedRoom> {

  /**
   * The room id extracted from event source.
   */
  private String roomId;

  /**
   * The user id who joined the room extracted from event source.
   */
  private Long userId;

  public UserJoinedRoomContext(V4Initiator initiator, V4UserJoinedRoom sourceEvent) {
    super(initiator, sourceEvent);
  }
}
