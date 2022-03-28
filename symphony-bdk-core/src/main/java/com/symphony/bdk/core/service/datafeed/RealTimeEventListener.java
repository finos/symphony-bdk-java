package com.symphony.bdk.core.service.datafeed;

import com.symphony.bdk.gen.api.model.*;

import org.apiguardian.api.API;

/**
 * Interface definition for a callback to be invoked when a real-time event is received from the datafeed.
 *
 * @see <a href="https://docs.developers.symphony.com/building-bots-on-symphony/datafeed/real-time-events">Real-Time Events</a>
 */
@API(status = API.Status.STABLE)
public interface RealTimeEventListener {

  /**
   * Check if the event is accepted to be handled.
   * By default, all the event that is created by the bot itself will not be accepted to be handled by the listener.
   * If you want to handle the self-created events or you want to apply your own filters for the events, you should override this method.
   *
   * @param event    Event to be verified.
   * @param botInfo  General bot info object.
   * @return The event is accepted or not.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  @API(status = API.Status.EXPERIMENTAL)
  default boolean isAcceptingEvent(V4Event event, UserV2 botInfo) throws EventException {
    return event.getInitiator() != null && event.getInitiator().getUser() != null
        && event.getInitiator().getUser().getUserId() != null
        && !event.getInitiator().getUser().getUserId().equals(botInfo.getId());
  }

  /**
   * Called when a MESSAGESENT event is received.
   *
   * @param initiator Event initiator.
   * @param event     Message sent payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onMessageSent(V4Initiator initiator, V4MessageSent event) throws EventException {
  }

  /**
   * Called when a SHAREDPOST event is received.
   *
   * @param initiator Event initiator.
   * @param event     Shared post payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onSharedPost(V4Initiator initiator, V4SharedPost event) throws EventException {
  }

  /**
   * Called when an INSTANTMESSAGECREATED event is received.
   *
   * @param initiator Event initiator.
   * @param event     Instant Message Created payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onInstantMessageCreated(V4Initiator initiator, V4InstantMessageCreated event) throws EventException {
  }

  /**
   * Called when a ROOMCREATED event is received.
   *
   * @param initiator Event initiator.
   * @param event     Room Created payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onRoomCreated(V4Initiator initiator, V4RoomCreated event) throws EventException {
  }

  /**
   * Called when a ROOMUPDATED event is received.
   *
   * @param initiator Event initiator.
   * @param event     Room Updated payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onRoomUpdated(V4Initiator initiator, V4RoomUpdated event) throws EventException {
  }

  /**
   * Called when a ROOMDEACTIVATED event is received.
   *
   * @param initiator Event initiator.
   * @param event     Room Deactivated payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onRoomDeactivated(V4Initiator initiator, V4RoomDeactivated event) throws EventException {
  }

  /**
   * Called when a ROOMREACTIVATED event is received.
   *
   * @param initiator Event initiator.
   * @param event     Room Reactivated payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onRoomReactivated(V4Initiator initiator, V4RoomReactivated event) throws EventException {
  }

  /**
   * Called when an USERREQUESTEDTOJOINROOM event is received.
   *
   * @param initiator Event initiator.
   * @param event     User Requested To Join Room payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onUserRequestedToJoinRoom(V4Initiator initiator, V4UserRequestedToJoinRoom event) throws EventException {
  }

  /**
   * Called when an USERJOINEDROOM event is received.
   *
   * @param initiator Event initiator.
   * @param event     User Joined Room payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onUserJoinedRoom(V4Initiator initiator, V4UserJoinedRoom event) throws EventException {
  }

  /**
   * Called when an USERLEFTROOM event is received.
   *
   * @param initiator Event initiator.
   * @param event     User Left Room payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onUserLeftRoom(V4Initiator initiator, V4UserLeftRoom event) throws EventException {
  }

  /**
   * Called when a ROOMMEMBERPROMOTEDTOOWNER event is received.
   *
   * @param initiator Event initiator.
   * @param event     Room Member Promoted To Owner payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onRoomMemberPromotedToOwner(V4Initiator initiator, V4RoomMemberPromotedToOwner event)
      throws EventException {
  }

  /**
   * Called when a ROOMMEMBERDEMOTEDFROMOWNER event is received.
   *
   * @param initiator Event initiator.
   * @param event     Room Member Demoted From Owner payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onRoomMemberDemotedFromOwner(V4Initiator initiator, V4RoomMemberDemotedFromOwner event)
      throws EventException {
  }

  /**
   * Called when a CONNECTIONREQUESTED event is received.
   *
   * @param initiator Event initiator.
   * @param event     Connection Requested payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onConnectionRequested(V4Initiator initiator, V4ConnectionRequested event) throws EventException {
  }

  /**
   * Called when a CONNECTIONACCEPTED event is received.
   *
   * @param initiator Event initiator.
   * @param event     Connection Accepted payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onConnectionAccepted(V4Initiator initiator, V4ConnectionAccepted event) throws EventException {
  }

  /**
   * Called when a MESSAGESUPPRESSED event is received.
   *
   * @param initiator Event initiator.
   * @param event     Message Suppressed payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onMessageSuppressed(V4Initiator initiator, V4MessageSuppressed event) throws EventException {
  }

  /**
   * Called when a SYMPHONYELEMENTSACTION event is received.
   *
   * @param initiator Event initiator.
   * @param event     Symphony Elements Action payload.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  default void onSymphonyElementsAction(V4Initiator initiator, V4SymphonyElementsAction event) throws EventException {
  }

}
