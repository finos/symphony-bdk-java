package com.symphony.ms.songwriter.internal.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.symphony.ms.songwriter.internal.command.CommandFilter;
import com.symphony.ms.songwriter.internal.event.model.IMCreatedEvent;
import com.symphony.ms.songwriter.internal.event.model.MessageEvent;
import com.symphony.ms.songwriter.internal.event.model.RoomCreatedEvent;
import com.symphony.ms.songwriter.internal.event.model.RoomDeactivatedEvent;
import com.symphony.ms.songwriter.internal.event.model.RoomMemberDemotedFromOwnerEvent;
import com.symphony.ms.songwriter.internal.event.model.RoomMemberPromotedToOwnerEvent;
import com.symphony.ms.songwriter.internal.event.model.RoomReactivatedEvent;
import com.symphony.ms.songwriter.internal.event.model.RoomUpdatedEvent;
import com.symphony.ms.songwriter.internal.event.model.SymphonyElementsEvent;
import com.symphony.ms.songwriter.internal.event.model.UserJoinedRoomEvent;
import com.symphony.ms.songwriter.internal.event.model.UserLeftRoomEvent;

@Service
public class InternalEventListenerImpl implements InternalEventListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(InternalEventListenerImpl.class);

  private CommandFilter commandFilter;

  private EventDispatcher eventDispatcher;

  public InternalEventListenerImpl(CommandFilter commandFilter,
      EventDispatcher eventDispatcher) {
    this.commandFilter = commandFilter;
    this.eventDispatcher = eventDispatcher;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRoomMessage(MessageEvent message) {
    LOGGER.debug("Received message in room {}", message.getStreamId());
    commandFilter.filter(message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onIMMessage(MessageEvent message) {
    LOGGER.debug("Received message in IM {}", message.getStreamId());
    commandFilter.filter(message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRoomCreated(RoomCreatedEvent event) {
    LOGGER.debug("Room {} has been created", event.getStreamId());
    eventDispatcher.push(event.getClass().getCanonicalName(), event);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRoomReactivated(RoomReactivatedEvent event) {
    LOGGER.debug("Room {} has been reactivated", event.getStreamId());
    eventDispatcher.push(event.getClass().getCanonicalName(), event);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRoomDeactivated(RoomDeactivatedEvent event) {
    LOGGER.debug("Room {} has been deactivated", event.getStreamId());
    eventDispatcher.push(event.getClass().getCanonicalName(), event);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRoomUpdated(RoomUpdatedEvent event) {
    LOGGER.debug("Room {} has been updated", event.getStreamId());
    eventDispatcher.push(event.getClass().getCanonicalName(), event);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onIMCreated(IMCreatedEvent event) {
    LOGGER.debug("IM {} has been created", event.getStreamId());
    eventDispatcher.push(event.getClass().getCanonicalName(), event);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRoomMemberDemotedFromOwner(
      RoomMemberDemotedFromOwnerEvent event) {
    LOGGER.debug("Room member {} has been demoted from owner in room {}",
        event.getUserId(), event.getStreamId());
    eventDispatcher.push(event.getClass().getCanonicalName(), event);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRoomMemberPromotedToOwner(
      RoomMemberPromotedToOwnerEvent event) {
    LOGGER.debug("Room member {} has been promoted to owner in room {}",
        event.getUserId(), event.getStreamId());
    eventDispatcher.push(event.getClass().getCanonicalName(), event);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onUserJoinedRoom(UserJoinedRoomEvent event) {
    LOGGER.debug("User {} joined room {}",
        event.getUserId(), event.getStreamId());
    eventDispatcher.push(event.getClass().getCanonicalName(), event);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onUserLeftRoom(UserLeftRoomEvent event) {
    LOGGER.debug("User {} left room {}",
        event.getUserId(), event.getStreamId());
    eventDispatcher.push(event.getClass().getCanonicalName(), event);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onElementsAction(SymphonyElementsEvent event) {
    LOGGER.debug("User {} triggered elements form {} in room {}",
        event.getUserId(), event.getFormId(), event.getStreamId());
    eventDispatcher.push(event.getFormId(), event);
  }

}
