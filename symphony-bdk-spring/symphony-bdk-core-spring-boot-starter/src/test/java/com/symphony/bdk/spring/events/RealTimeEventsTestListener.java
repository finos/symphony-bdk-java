package com.symphony.bdk.spring.events;

import com.symphony.bdk.gen.api.model.V4ConnectionAccepted;
import com.symphony.bdk.gen.api.model.V4ConnectionRequested;
import com.symphony.bdk.gen.api.model.V4InstantMessageCreated;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4MessageSuppressed;
import com.symphony.bdk.gen.api.model.V4RoomCreated;
import com.symphony.bdk.gen.api.model.V4RoomDeactivated;
import com.symphony.bdk.gen.api.model.V4RoomMemberDemotedFromOwner;
import com.symphony.bdk.gen.api.model.V4RoomMemberPromotedToOwner;
import com.symphony.bdk.gen.api.model.V4RoomReactivated;
import com.symphony.bdk.gen.api.model.V4RoomUpdated;
import com.symphony.bdk.gen.api.model.V4SharedPost;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;
import com.symphony.bdk.gen.api.model.V4UserJoinedRoom;
import com.symphony.bdk.gen.api.model.V4UserLeftRoom;
import com.symphony.bdk.gen.api.model.V4UserRequestedToJoinRoom;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * This class is used by the {@link RealTimeEventsDispatcherTest} only.
 */
@Component
public class RealTimeEventsTestListener {

  @EventListener
  public void onMessageSent(RealTimeEvent<? extends V4MessageSent> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onSharedPost(RealTimeEvent<? extends V4SharedPost> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onInstantMessageCreated(RealTimeEvent<? extends V4InstantMessageCreated> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onRoomCreated(RealTimeEvent<? extends V4RoomCreated> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onRoomUpdated(RealTimeEvent<? extends V4RoomUpdated> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onRoomDeactivated(RealTimeEvent<? extends V4RoomDeactivated> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onRoomReactivated(RealTimeEvent<? extends V4RoomReactivated> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onUserRequestedToJoinRoom(RealTimeEvent<? extends V4UserRequestedToJoinRoom> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onUserJoinedRoom(RealTimeEvent<? extends V4UserJoinedRoom> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onUserLeftRoom(RealTimeEvent<? extends V4UserLeftRoom> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onRoomMemberPromotedToOwner(RealTimeEvent<? extends V4RoomMemberPromotedToOwner> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onRoomMemberDemotedFromOwner(RealTimeEvent<? extends V4RoomMemberDemotedFromOwner> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onConnectionRequested(RealTimeEvent<? extends V4ConnectionRequested> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onConnectionAccepted(RealTimeEvent<? extends V4ConnectionAccepted> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onMessageSuppressed(RealTimeEvent<? extends V4MessageSuppressed> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onSymphonyElementsAction(RealTimeEvent<? extends V4SymphonyElementsAction> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }
}
