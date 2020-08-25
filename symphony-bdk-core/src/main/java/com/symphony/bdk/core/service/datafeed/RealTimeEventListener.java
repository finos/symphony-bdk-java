package com.symphony.bdk.core.service.datafeed;

import com.symphony.bdk.gen.api.model.*;

/**
 * Interface definition for a callback to be invoked when a real-time event is received from the datafeed.
 * @see <a href="https://developers.symphony.com/restapi/docs/real-time-events">real-time-events</a>
 */
public interface RealTimeEventListener {

    /**
     * Called when a MESSAGESENT event is received.
     * @param event A Complete V4 event.
     */
    default void onMessageSent(V4Event event) {}

    /**
     * Called when a MESSAGESENT event is received.
     * @param initiator Event initiator.
     * @param event Message sent payload.
     */
    default void onMessageSent(V4Initiator initiator, V4MessageSent event) {}

    /**
     * Called when a SHAREDPOST event is received.
     * @param event A Complete V4 event.
     */
    default void onSharedPost(V4Event event) {}

    /**
     * Called when a SHAREDPOST event is received.
     * @param initiator Event initiator.
     * @param event Shared post payload.
     */
    default void onSharedPost(V4Initiator initiator, V4SharedPost event) {}

    /**
     * Called when an INSTANTMESSAGECREATED event is received.
     * @param event A Complete V4 event.
     */
    default void onInstantMessageCreated(V4Event event) {}

    /**
     * Called when an INSTANTMESSAGECREATED event is received.
     * @param initiator Event initiator.
     * @param event Instant Message Created payload.
     */
    default void onInstantMessageCreated(V4Initiator initiator, V4InstantMessageCreated event) {}

    /**
     * Called when a ROOMCREATED event is received.
     * @param event A Complete V4 event.
     */
    default void onRoomCreated(V4Event event) {}

    /**
     * Called when a ROOMCREATED event is received.
     * @param initiator Event initiator.
     * @param event Room Created payload.
     */
    default void onRoomCreated(V4Initiator initiator, V4RoomCreated event) {}

    /**
     * Called when a ROOMUPDATED event is received.
     * @param event A Complete V4 event.
     */
    default void onRoomUpdated(V4Event event) {}

    /**
     * Called when a ROOMUPDATED event is received.
     * @param initiator Event initiator.
     * @param event Room Updated payload.
     */
    default void onRoomUpdated(V4Initiator initiator, V4RoomUpdated event) {}

    /**
     * Called when a ROOMDEACTIVATED event is received.
     * @param event A Complete V4 event.
     */
    default void onRoomDeactivated(V4Event event) {}

    /**
     * Called when a ROOMDEACTIVATED event is received.
     * @param initiator Event initiator.
     * @param event Room Deactivated payload.
     */
    default void onRoomDeactivated(V4Initiator initiator, V4RoomDeactivated event) {}

    /**
     * Called when a ROOMREACTIVATED event is received.
     * @param event A Complete V4 event.
     */
    default void onRoomReactivated(V4Event event) {}

    /**
     * Called when a ROOMREACTIVATED event is received.
     * @param initiator Event initiator.
     * @param event Room Reactivated payload.
     */
    default void onRoomReactivated(V4Initiator initiator, V4RoomReactivated event) {}

    /**
     * Called when an USERREQUESTEDTOJOINROOM event is received.
     * @param event A Complete V4 event.
     */
    default void onUserRequestedToJoinRoom(V4Event event) {}

    /**
     * Called when an USERREQUESTEDTOJOINROOM event is received.
     * @param initiator Event initiator.
     * @param event User Requested To Join Room payload.
     */
    default void onUserRequestedToJoinRoom(V4Initiator initiator, V4UserRequestedToJoinRoom event) {}

    /**
     * Called when an USERJOINEDROOM event is received.
     * @param event A Complete V4 event.
     */
    default void onUserJoinedRoom(V4Event event) {}

    /**
     * Called when an USERJOINEDROOM event is received.
     * @param initiator Event initiator.
     * @param event User Joined Room payload.
     */
    default void onUserJoinedRoom(V4Initiator initiator, V4UserJoinedRoom event) {}

    /**
     * Called when an USERLEFTROOM event is received.
     * @param event A Complete V4 event.
     */
    default void onUserLeftRoom(V4Event event) {}

    /**
     * Called when an USERLEFTROOM event is received.
     * @param initiator Event initiator.
     * @param event User Left Room payload.
     */
    default void onUserLeftRoom(V4Initiator initiator, V4UserLeftRoom event) {}

    /**
     * Called when a ROOMMEMBERPROMOTEDTOOWNER event is received.
     * @param event A Complete V4 event.
     */
    default void onRoomMemberPromotedToOwner(V4Event event) {}

    /**
     * Called when a ROOMMEMBERPROMOTEDTOOWNER event is received.
     * @param initiator Event initiator.
     * @param event Room Member Promoted To Owner payload.
     */
    default void onRoomMemberPromotedToOwner(V4Initiator initiator, V4RoomMemberPromotedToOwner event) {}

    /**
     * Called when a ROOMMEMBERDEMOTEDFROMOWNER event is received.
     * @param event A Complete V4 event.
     */
    default void onRoomMemberDemotedFromOwner(V4Event event) {}

    /**
     * Called when a ROOMMEMBERDEMOTEDFROMOWNER event is received.
     * @param initiator Event initiator.
     * @param event Room Member Demoted From Owner payload.
     */
    default void onRoomMemberDemotedFromOwner(V4Initiator initiator, V4RoomMemberDemotedFromOwner event) {}

    /**
     * Called when a CONNECTIONREQUESTED event is received.
     * @param event A Complete V4 event.
     */
    default void onConnectionRequested(V4Event event) {}

    /**
     * Called when a CONNECTIONREQUESTED event is received.
     * @param initiator Event initiator.
     * @param event Connection Requested payload.
     */
    default void onConnectionRequested(V4Initiator initiator, V4ConnectionRequested event) {}

    /**
     * Called when a CONNECTIONACCEPTED event is received.
     * @param event A Complete V4 event.
     */
    default void onConnectionAccepted(V4Event event) {}

    /**
     * Called when a CONNECTIONACCEPTED event is received.
     * @param initiator Event initiator.
     * @param event Connection Accepted payload.
     */
    default void onConnectionAccepted(V4Initiator initiator, V4ConnectionAccepted event) {}

    /**
     * Called when a MESSAGESUPPRESSED event is received.
     * @param event A Complete V4 event.
     */
    default void onMessageSuppressed(V4Event event) {}

    /**
     * Called when a MESSAGESUPPRESSED event is received.
     * @param initiator Event initiator.
     * @param event Message Suppressed payload.
     */
    default void onMessageSuppressed(V4Initiator initiator, V4MessageSuppressed event) {}

    /**
     * Called when a SYMPHONYELEMENTSACTION event is received.
     * @param event A Complete V4 event.
     */
    default void onSymphonyElementsAction(V4Event event) {}

    /**
     * Called when a SYMPHONYELEMENTSACTION event is received.
     * @param initiator Event initiator.
     * @param event Symphony Elements Action payload.
     */
    default void onSymphonyElementsAction(V4Initiator initiator, V4SymphonyElementsAction event) {}

}
