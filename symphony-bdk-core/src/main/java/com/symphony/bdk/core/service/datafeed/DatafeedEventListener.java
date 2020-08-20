package com.symphony.bdk.core.service.datafeed;

import com.symphony.bdk.gen.api.model.*;

public interface DatafeedEventListener {

    default void onMessageSent(V4Event event) {};

    default void onSharedPost(V4Event event) {};

    default void onInstantMessageCreated(V4Event event) {};

    default void onRoomCreated(V4Event event) {};

    default void onRoomUpdated(V4Event event) {};

    default void onRoomDeactivated(V4Event event) {};

    default void onRoomReactivated(V4Event event) {};

    default void onUserRequestedToJoinRoom(V4Event event) {};

    default void onUserJoinedRoom(V4Event event) {};

    default void onUserLeftRoom(V4Event event) {};

    default void onRoomMemberPromotedToOwner(V4Event event) {};

    default void onRoomMemberDemotedFromOwner(V4Event event) {};

    default void onConnectionRequested(V4Event event) {};

    default void onConnectionAccepted(V4Event event) {};

    default void onMessageSuppressed(V4Event event) {};

    default void onSymphonyElementsAction(V4Event event) {};

}
