package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V4Payload;
import io.vavr.Function3;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Enumeration of possible types of Real Time Events that can be retrieved from the DataFeed.
 * More information : https://rest-api.symphony.com/reference#real-time-events-v4
 */
enum RealTimeEventType {

    MESSAGESENT((listener, event, innerEvent) -> {
        listener.onMessageSent(event);
        listener.onMessageSent(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getMessageSent),
    MESSAGESUPPRESSED((listener, event, innerEvent) -> {
        listener.onMessageSuppressed(event);
        listener.onMessageSuppressed(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getMessageSuppressed),
    SYMPHONYELEMENTSACTION((listener, event, innerEvent) -> {
        listener.onSymphonyElementsAction(event);
        listener.onSymphonyElementsAction(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getSymphonyElementsAction),
    SHAREDPOST((listener, event, innerEvent) -> {
        listener.onSharedPost(event);
        listener.onSharedPost(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getSharedPost),
    INSTANTMESSAGECREATED((listener, event, innerEvent) -> {
        listener.onInstantMessageCreated(event);
        listener.onInstantMessageCreated(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getInstantMessageCreated),
    ROOMCREATED((listener, event, innerEvent) -> {
        listener.onRoomCreated(event);
        listener.onRoomCreated(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getRoomCreated),
    ROOMUPDATED((listener, event, innerEvent) -> {
        listener.onRoomUpdated(event);
        listener.onRoomUpdated(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getRoomUpdated),
    ROOMDEACTIVATED((listener, event, innerEvent) -> {
        listener.onRoomDeactivated(event);
        listener.onRoomDeactivated(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getRoomDeactivated),
    ROOMREACTIVATED((listener, event, innerEvent) -> {
        listener.onRoomReactivated(event);
        listener.onRoomReactivated(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getRoomReactivated),
    USERJOINEDROOM((listener, event, innerEvent) -> {
        listener.onUserJoinedRoom(event);
        listener.onUserJoinedRoom(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getUserJoinedRoom),
    USERLEFTROOM((listener, event, innerEvent) -> {
        listener.onUserLeftRoom(event);
        listener.onUserLeftRoom(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getUserLeftRoom),
    USERREQUESTEDTOJOINROOM((listener, event, innerEvent) -> {
        listener.onUserRequestedToJoinRoom(event);
        listener.onUserRequestedToJoinRoom(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getUserRequestedToJoinRoom),
    ROOMMEMBERPROMOTEDTOOWNER((listener, event, innerEvent) -> {
        listener.onRoomMemberPromotedToOwner(event);
        listener.onRoomMemberPromotedToOwner(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getRoomMemberPromotedToOwner),
    ROOMMEMBERDEMOTEDFROMOWNER((listener, event, innerEvent) -> {
        listener.onRoomMemberDemotedFromOwner(event);
        listener.onRoomMemberDemotedFromOwner(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getRoomMemberDemotedFromOwner),
    CONNECTIONACCEPTED((listener, event, innerEvent) -> {
        listener.onConnectionAccepted(event);
        listener.onConnectionAccepted(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getConnectionAccepted),
    CONNECTIONREQUESTED((listener, event, innerEvent) -> {
        listener.onConnectionRequested(event);
        listener.onConnectionRequested(event.getInitiator(), innerEvent);
        return null;
    }, V4Payload::getConnectionRequested);

    private final BiConsumer<RealTimeEventListener, V4Event> execConsumer;

    <T> RealTimeEventType(Function3<RealTimeEventListener, V4Event, T, Void> consumer, Function<V4Payload, T> payloadFunction) {
        this.execConsumer = ((listener, event) -> consumer.apply(listener, event, payloadFunction.apply(event.getPayload())));
    }

    public void dispatch(RealTimeEventListener listener, V4Event event) {
        this.execConsumer.accept(listener, event);
    }
}
