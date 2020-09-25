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
  public void onMessageSent(RealTimeEvent<V4MessageSent> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onSharedPost(RealTimeEvent<V4SharedPost> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onInstantMessageCreated(RealTimeEvent<V4InstantMessageCreated> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onRoomCreated(RealTimeEvent<V4RoomCreated> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onRoomUpdated(RealTimeEvent<V4RoomUpdated> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onRoomDeactivated(RealTimeEvent<V4RoomDeactivated> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onRoomReactivated(RealTimeEvent<V4RoomReactivated> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onUserRequestedToJoinRoom(RealTimeEvent<V4UserRequestedToJoinRoom> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onUserJoinedRoom(RealTimeEvent<V4UserJoinedRoom> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onUserLeftRoom(RealTimeEvent<V4UserLeftRoom> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onRoomMemberPromotedToOwner(RealTimeEvent<V4RoomMemberPromotedToOwner> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onRoomMemberDemotedFromOwner(RealTimeEvent<V4RoomMemberDemotedFromOwner> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onConnectionRequested(RealTimeEvent<V4ConnectionRequested> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onConnectionAccepted(RealTimeEvent<V4ConnectionAccepted> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onMessageSuppressed(RealTimeEvent<V4MessageSuppressed> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }

  @EventListener
  public void onSymphonyElementsAction(RealTimeEvent<V4SymphonyElementsAction> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
  }
}
