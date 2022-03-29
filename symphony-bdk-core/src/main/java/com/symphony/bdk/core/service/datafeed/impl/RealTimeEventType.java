package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Event;

import org.apiguardian.api.API;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Enumeration of possible types of Real Time Events that can be retrieved from the DataFeed.
 * More information : https://docs.developers.symphony.com/building-bots-on-symphony/datafeed/real-time-events
 */
@API(status = API.Status.INTERNAL)
enum RealTimeEventType {

  MESSAGESENT((listener, event) -> {
    listener.onMessageSent(event.getInitiator(), event.getPayload().getMessageSent());
  }),
  MESSAGESUPPRESSED((listener, event) -> {
    listener.onMessageSuppressed(event.getInitiator(), event.getPayload().getMessageSuppressed());
  }),
  SYMPHONYELEMENTSACTION((listener, event) -> {
    listener.onSymphonyElementsAction(event.getInitiator(), event.getPayload().getSymphonyElementsAction());
  }),
  SHAREDPOST((listener, event) -> {
    listener.onSharedPost(event.getInitiator(), event.getPayload().getSharedPost());
  }),
  INSTANTMESSAGECREATED((listener, event) -> {
    listener.onInstantMessageCreated(event.getInitiator(), event.getPayload().getInstantMessageCreated());
  }),
  ROOMCREATED((listener, event) -> {
    listener.onRoomCreated(event.getInitiator(), event.getPayload().getRoomCreated());
  }),
  ROOMUPDATED((listener, event) -> {
    listener.onRoomUpdated(event.getInitiator(), event.getPayload().getRoomUpdated());
  }),
  ROOMDEACTIVATED((listener, event) -> {
    listener.onRoomDeactivated(event.getInitiator(), event.getPayload().getRoomDeactivated());
  }),
  ROOMREACTIVATED((listener, event) -> {
    listener.onRoomReactivated(event.getInitiator(), event.getPayload().getRoomReactivated());
  }),
  USERJOINEDROOM((listener, event) -> {
    listener.onUserJoinedRoom(event.getInitiator(), event.getPayload().getUserJoinedRoom());
  }),
  USERLEFTROOM((listener, event) -> {
    listener.onUserLeftRoom(event.getInitiator(), event.getPayload().getUserLeftRoom());
  }),
  USERREQUESTEDTOJOINROOM((listener, event) -> {
    listener.onUserRequestedToJoinRoom(event.getInitiator(), event.getPayload().getUserRequestedToJoinRoom());
  }),
  ROOMMEMBERPROMOTEDTOOWNER((listener, event) -> {
    listener.onRoomMemberPromotedToOwner(event.getInitiator(), event.getPayload().getRoomMemberPromotedToOwner());
  }),
  ROOMMEMBERDEMOTEDFROMOWNER((listener, event) -> {
    listener.onRoomMemberDemotedFromOwner(event.getInitiator(), event.getPayload().getRoomMemberDemotedFromOwner());
  }),
  CONNECTIONACCEPTED((listener, event) -> {
    listener.onConnectionAccepted(event.getInitiator(), event.getPayload().getConnectionAccepted());
  }),
  CONNECTIONREQUESTED((listener, event) -> {
    listener.onConnectionRequested(event.getInitiator(), event.getPayload().getConnectionRequested());
  });

  private final BiConsumer<RealTimeEventListener, V4Event> execConsumer;

  RealTimeEventType(BiConsumer<RealTimeEventListener, V4Event> consumer) {
    this.execConsumer = consumer;
  }

  public static Optional<RealTimeEventType> fromV4Event(V4Event event) {

    if (event == null || event.getType() == null) {
      return Optional.empty();
    }

    try {
      return Optional.of(RealTimeEventType.valueOf(event.getType()));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public void dispatch(RealTimeEventListener listener, V4Event event) {
    this.execConsumer.accept(listener, event);
  }
}
