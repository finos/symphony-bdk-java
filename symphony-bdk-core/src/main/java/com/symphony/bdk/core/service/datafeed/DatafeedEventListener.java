package com.symphony.bdk.core.service.datafeed;

import com.symphony.bdk.gen.api.model.*;

/**
 * Interface definition for a callback to be invoked when a datafeed event is received.
 */
public interface DatafeedEventListener {

    /**
     * Call when a MESSAGESENT event is received.
     * @param event Message sent event.
     */
    default void onMessageSent(V4Event event) {};

    /**
     * Call when a SHAREDPOST event is received.
     * @param event Shared post event.
     */
    default void onSharedPost(V4Event event) {};

    /**
     * Call when an INSTANTMESSAGECREATED event is received.
     * @param event Instant Message Created event.
     */
    default void onInstantMessageCreated(V4Event event) {};

    /**
     * Call when a ROOMCREATED event is received.
     * @param event Room Created event.
     */
    default void onRoomCreated(V4Event event) {};

    /**
     * Call when a ROOMUPDATED event is received.
     * @param event Room Updated event.
     */
    default void onRoomUpdated(V4Event event) {};

    /**
     * Call when a ROOMDEACTIVATED event is received.
     * @param event Room Deactivated event.
     */
    default void onRoomDeactivated(V4Event event) {};

    /**
     * Call when a ROOMREACTIVATED event is received.
     * @param event Room Reactivated event.
     */
    default void onRoomReactivated(V4Event event) {};

    /**
     * Call when an USERREQUESTEDTOJOINROOM event is received.
     * @param event User Requested To Join Room event.
     */
    default void onUserRequestedToJoinRoom(V4Event event) {};

    /**
     * Call when an USERJOINEDROOM event is received.
     * @param event User Joined Room event.
     */
    default void onUserJoinedRoom(V4Event event) {};

    /**
     * Call when an USERLEFTROOM event is received.
     * @param event User Left Room event.
     */
    default void onUserLeftRoom(V4Event event) {};

    /**
     * Call when a ROOMMEMBERPROMOTEDTOOWNER event is received.
     * @param event Room Member Promoted To Owner event.
     */
    default void onRoomMemberPromotedToOwner(V4Event event) {};

    /**
     * Call when a ROOMMEMBERDEMOTEDFROMOWNER event is received.
     * @param event Room Member Demoted From Owner event.
     */
    default void onRoomMemberDemotedFromOwner(V4Event event) {};

    /**
     * Call when a CONNECTIONREQUESTED event is received.
     * @param event Connection Requested event.
     */
    default void onConnectionRequested(V4Event event) {};

    /**
     * Call when a CONNECTIONACCEPTED event is received.
     * @param event Connection Accepted event.
     */
    default void onConnectionAccepted(V4Event event) {};

    /**
     * Call when a MESSAGESUPPRESSED event is received.
     * @param event Message Suppressed event.
     */
    default void onMessageSuppressed(V4Event event) {};

    /**
     * Call when a SYMPHONYELEMENTSACTION event is received.
     * @param event Symphony Elements Action event.
     */
    default void onSymphonyElementsAction(V4Event event) {};

}
