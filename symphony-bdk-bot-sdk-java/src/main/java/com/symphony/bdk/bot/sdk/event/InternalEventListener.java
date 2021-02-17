package com.symphony.bdk.bot.sdk.event;

import com.symphony.bdk.bot.sdk.event.model.IMCreatedEvent;
import com.symphony.bdk.bot.sdk.event.model.MessageEvent;
import com.symphony.bdk.bot.sdk.event.model.RoomCreatedEvent;
import com.symphony.bdk.bot.sdk.event.model.RoomDeactivatedEvent;
import com.symphony.bdk.bot.sdk.event.model.RoomMemberDemotedFromOwnerEvent;
import com.symphony.bdk.bot.sdk.event.model.RoomMemberPromotedToOwnerEvent;
import com.symphony.bdk.bot.sdk.event.model.RoomReactivatedEvent;
import com.symphony.bdk.bot.sdk.event.model.RoomUpdatedEvent;
import com.symphony.bdk.bot.sdk.event.model.SymphonyElementsEvent;
import com.symphony.bdk.bot.sdk.event.model.UserJoinedRoomEvent;
import com.symphony.bdk.bot.sdk.event.model.UserLeftRoomEvent;
import com.symphony.bdk.bot.sdk.event.model.ConnectionAcceptedEvent;
import com.symphony.bdk.bot.sdk.event.model.ConnectionRequestedEvent;

/**
 * Internal Symphony events listener
 *
 * @author Marcus Secato
 *
 */
public interface InternalEventListener {

  /**
   * Room message event received. Pushes event to CommandFilter for command
   * filtering.
   *
   * @param message the message received in Symphony chat room
   */
  void onRoomMessage(MessageEvent message);

  /**
   * IM message event received. Pushes event to CommandFilter for command
   * filtering.
   *
   * @param message the message received in Symphony IM
   */
  void onIMMessage(MessageEvent message);

  /**
   * Room created event received. Pushes event to EventDispatcher.
   *
   * @param event the event
   */
  void onRoomCreated(RoomCreatedEvent event);

  /**
   * Room reactivated event received. Pushes event to EventDispatcher.
   *
   * @param event the event
   */
  void onRoomReactivated(RoomReactivatedEvent event);

  /**
   * Room deactivated event received. Pushes event to EventDispatcher.
   *
   * @param event the event
   */
  void onRoomDeactivated(RoomDeactivatedEvent event);

  /**
   * Room updated event received. Pushes event to EventDispatcher.
   *
   * @param event the event
   */
  void onRoomUpdated(RoomUpdatedEvent event);

  /**
   * IM created event received. Pushes event to EventDispatcher.
   *
   * @param event the event
   */
  void onIMCreated(IMCreatedEvent event);

  /**
   * Room member demoted event received. Pushes event to EventDispatcher.
   *
   * @param event the event
   */
  void onRoomMemberDemotedFromOwner(RoomMemberDemotedFromOwnerEvent event);

  /**
   * Room member promoted event received. Pushes event to EventDispatcher.
   *
   * @param event the event
   */
  void onRoomMemberPromotedToOwner(RoomMemberPromotedToOwnerEvent event);

  /**
   * User joined room event received. Pushes event to EventDispatcher.
   *
   * @param event the event
   */
  void onUserJoinedRoom(UserJoinedRoomEvent event);

  /**
   * User left room event received. Pushes event to EventDispatcher.
   *
   * @param event the event
   */
  void onUserLeftRoom(UserLeftRoomEvent event);

  /**
   * Symphony elements event received. Pushes event to EventDispatcher.
   *
   * @param event the event
   */
  void onElementsAction(SymphonyElementsEvent event);

  /**
   * Connection request received. Pushes event to EventDispatcher.
   *
   * @param event
   */
  void onConnectionRequested(ConnectionRequestedEvent connectionRequestedEvent);
  
  /**
   * Accepted connection request event received. Pushes event to EventDispatcher.
   *
   * @param event
   */
  void onConnectionAccepted(ConnectionAcceptedEvent connectionRequestedEvent);
  
}
