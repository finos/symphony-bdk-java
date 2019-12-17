package com.symphony.ms.bot.sdk.internal.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.symphony.ms.bot.sdk.internal.event.model.IMCreatedEvent;
import com.symphony.ms.bot.sdk.internal.event.model.MessageEvent;
import com.symphony.ms.bot.sdk.internal.event.model.RoomCreatedEvent;
import com.symphony.ms.bot.sdk.internal.event.model.RoomDeactivatedEvent;
import com.symphony.ms.bot.sdk.internal.event.model.RoomMemberDemotedFromOwnerEvent;
import com.symphony.ms.bot.sdk.internal.event.model.RoomMemberPromotedToOwnerEvent;
import com.symphony.ms.bot.sdk.internal.event.model.RoomReactivatedEvent;
import com.symphony.ms.bot.sdk.internal.event.model.RoomUpdatedEvent;
import com.symphony.ms.bot.sdk.internal.event.model.SymphonyElementsEvent;
import com.symphony.ms.bot.sdk.internal.event.model.UserJoinedRoomEvent;
import com.symphony.ms.bot.sdk.internal.event.model.UserLeftRoomEvent;
import com.symphony.ms.bot.sdk.internal.symphony.SymphonyService;
import listeners.ElementsListener;
import listeners.IMListener;
import listeners.RoomListener;
import model.InboundMessage;
import model.Stream;
import model.User;
import model.events.RoomCreated;
import model.events.RoomDeactivated;
import model.events.RoomMemberDemotedFromOwner;
import model.events.RoomMemberPromotedToOwner;
import model.events.RoomUpdated;
import model.events.SymphonyElementsAction;
import model.events.UserJoinedRoom;
import model.events.UserLeftRoom;

/**
 * The Symphony listener
 * Listens to messages, events and Symphony elements actions. It also
 * standardizes events to internal representation.
 *
 * @author Marcus Secato
 *
 */
@Service
public class EventListener implements IMListener, RoomListener, ElementsListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventListener.class);

  private SymphonyService symphonyService;

  private InternalEventListener internalEventListener;

  public EventListener(SymphonyService symphonyService,
      InternalEventListenerImpl internalEventListener) {
    this.symphonyService = symphonyService;
    this.internalEventListener = internalEventListener;

    this.symphonyService.registerIMListener(this);
    this.symphonyService.registerRoomListener(this);
    this.symphonyService.registerElementsListener(this);
  }

  @Override
  public void onRoomMessage(InboundMessage message) {
    LOGGER.debug("onRoomMessage");
    try {
      internalEventListener.onRoomMessage(new MessageEvent(message));
    } catch (Exception e) {
      LOGGER.warn("Received invalid room message");
    }
  }

  @Override
  public void onIMMessage(InboundMessage message) {
    LOGGER.debug("onIMMessage");
    try {
      internalEventListener.onIMMessage(new MessageEvent(message));
    } catch (Exception e) {
      LOGGER.warn("Received invalid IM message");
    }
  }

  @Override
  public void onRoomCreated(RoomCreated roomCreatedEvent) {
    LOGGER.debug("onRoomCreated");
    internalEventListener.onRoomCreated(
        new RoomCreatedEvent(roomCreatedEvent));
  }

  @Override
  public void onRoomReactivated(Stream stream) {
    LOGGER.debug("onRoomReactivated");
    internalEventListener.onRoomReactivated(new RoomReactivatedEvent(stream));
  }

  @Override
  public void onRoomDeactivated(RoomDeactivated roomDeactivatedEvent) {
    LOGGER.debug("onRoomDeactivated");
    internalEventListener.onRoomDeactivated(
        new RoomDeactivatedEvent(roomDeactivatedEvent));
  }

  @Override
  public void onRoomUpdated(RoomUpdated roomUpdatedEvent) {
    LOGGER.debug("onRoomUpdated");
    internalEventListener.onRoomUpdated(
        new RoomUpdatedEvent(roomUpdatedEvent));
  }

  @Override
  public void onIMCreated(Stream stream) {
    LOGGER.debug("onIMCreated");
    internalEventListener.onIMCreated(new IMCreatedEvent(stream));
  }

  @Override
  public void onRoomMemberDemotedFromOwner(
      RoomMemberDemotedFromOwner roomMemberDemotedFromOwnerEvent) {
    LOGGER.debug("onRoomMemberDemotedFromOwner");
    internalEventListener.onRoomMemberDemotedFromOwner(
        new RoomMemberDemotedFromOwnerEvent(roomMemberDemotedFromOwnerEvent));
  }

  @Override
  public void onRoomMemberPromotedToOwner(
      RoomMemberPromotedToOwner roomMemberPromotedToOwnerEvent) {
    LOGGER.debug("onRoomMemberPromotedToOwner");
    internalEventListener.onRoomMemberPromotedToOwner(
        new RoomMemberPromotedToOwnerEvent(roomMemberPromotedToOwnerEvent));
  }

  @Override
  public void onUserJoinedRoom(UserJoinedRoom userJoinedRoomEvent) {
    LOGGER.debug("onUserJoinedRoom");
    internalEventListener.onUserJoinedRoom(
        new UserJoinedRoomEvent(userJoinedRoomEvent));
  }

  @Override
  public void onUserLeftRoom(UserLeftRoom userLeftRoomEvent) {
    LOGGER.debug("onUserLeftRoom");
    internalEventListener.onUserLeftRoom(
        new UserLeftRoomEvent(userLeftRoomEvent));
  }

  @Override
  public void onElementsAction(User initiator, SymphonyElementsAction action) {
    LOGGER.debug("onElementsAction");
    internalEventListener.onElementsAction(
        new SymphonyElementsEvent(initiator, action));
  }

}
