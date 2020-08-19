package com.symphony.bdk.core.service.datafeed;

import com.symphony.bdk.gen.api.model.*;

public interface DatafeedEventListener {

    void onMessageSent(V4Event event);

    void onSharedPost(V4Event event);

    void onInstantMessageCreated(V4Event event);

    void onRoomCreated(V4Event event);

    void onRoomUpdated(V4Event event);

    void onRoomDeactivated(V4Event event);

    void onRoomReactivated(V4Event event);

    void onUserRequestedToJoinRoom(V4Event event);

    void onUserJoinedRoom(V4Event event);

    void onUserLeftRoom(V4Event event);

    void onRoomMemberPromotedToOwner(V4Event event);

    void onRoomMemberDemotedFromOwner(V4Event event);

    void onConnectionRequested(V4Event event);

    void onConnectionAccepted(V4Event event);

    void onMessageSuppressed(V4Event event);

    void onSymphonyElementsAction(V4Event event);

}
