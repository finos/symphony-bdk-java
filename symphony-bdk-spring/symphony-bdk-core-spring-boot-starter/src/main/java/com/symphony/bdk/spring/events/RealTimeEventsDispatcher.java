package com.symphony.bdk.spring.events;

import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4ConnectionAccepted;
import com.symphony.bdk.gen.api.model.V4ConnectionRequested;
import com.symphony.bdk.gen.api.model.V4GenericSystemEvent;
import com.symphony.bdk.gen.api.model.V4Initiator;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * This component publishes Real Time Events as Spring {@link org.springframework.context.ApplicationEvent}. The event
 * payload and {@link V4Initiator} are wrapped into generic {@link RealTimeEvent}.
 * <p>
 * Events are dispatched asynchronously to avoid blocking the {@link DatafeedLoop}
 * thread.
 * </p>
 * <p>
 * To consume or subscribe to a specific event type, simply annotate your handle method by
 * {@link org.springframework.context.event.EventListener} and type your {@link RealTimeEvent} parameter with one of
 * those classes:
 * <ul>
 *   <li>{@link V4MessageSent}</li>
 *   <li>{@link V4SharedPost}</li>
 *   <li>{@link V4InstantMessageCreated}</li>
 *   <li>{@link V4RoomCreated}</li>
 *   <li>{@link V4RoomUpdated}</li>
 *   <li>{@link V4RoomDeactivated}</li>
 *   <li>{@link V4RoomReactivated}</li>
 *   <li>{@link V4UserRequestedToJoinRoom}</li>
 *   <li>{@link V4UserJoinedRoom}</li>
 *   <li>{@link V4UserLeftRoom}</li>
 *   <li>{@link V4RoomMemberPromotedToOwner}</li>
 *   <li>{@link V4RoomMemberDemotedFromOwner}</li>
 *   <li>{@link V4ConnectionRequested}</li>
 *   <li>{@link V4ConnectionAccepted}</li>
 *   <li>{@link V4MessageSuppressed}</li>
 *   <li>{@link V4SymphonyElementsAction}</li>
 * </ul>
 * </p>
 *
 * <p>
 * Example:
 * <pre>
 * &#64;EventListener
 * public void onMessage(RealTimeEvent&#60;V4MessageSent&#62; event) {
 *   // process event
 * }
 * </pre>
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
public class RealTimeEventsDispatcher implements RealTimeEventListener {

  private final ApplicationEventPublisher publisher;

  @Override
  public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onSharedPost(V4Initiator initiator, V4SharedPost event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onInstantMessageCreated(V4Initiator initiator, V4InstantMessageCreated event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onRoomCreated(V4Initiator initiator, V4RoomCreated event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onRoomUpdated(V4Initiator initiator, V4RoomUpdated event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onRoomDeactivated(V4Initiator initiator, V4RoomDeactivated event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onRoomReactivated(V4Initiator initiator, V4RoomReactivated event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onUserRequestedToJoinRoom(V4Initiator initiator, V4UserRequestedToJoinRoom event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onUserJoinedRoom(V4Initiator initiator, V4UserJoinedRoom event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onUserLeftRoom(V4Initiator initiator, V4UserLeftRoom event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onRoomMemberPromotedToOwner(V4Initiator initiator, V4RoomMemberPromotedToOwner event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onRoomMemberDemotedFromOwner(V4Initiator initiator, V4RoomMemberDemotedFromOwner event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onConnectionRequested(V4Initiator initiator, V4ConnectionRequested event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onConnectionAccepted(V4Initiator initiator, V4ConnectionAccepted event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onMessageSuppressed(V4Initiator initiator, V4MessageSuppressed event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onSymphonyElementsAction(V4Initiator initiator, V4SymphonyElementsAction event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

  @Override
  public void onGenericSystemEvent(V4Initiator initiator, V4GenericSystemEvent event) {
    this.publisher.publishEvent(new RealTimeEvent<>(initiator, event));
  }

}
