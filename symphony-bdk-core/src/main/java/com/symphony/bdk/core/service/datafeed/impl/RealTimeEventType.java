package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.service.datafeed.EventPayload;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Event;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.apiguardian.api.API;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.BiConsumer;

import static net.bytebuddy.matcher.ElementMatchers.isPublic;

/**
 * Enumeration of possible types of Real Time Events that can be retrieved from the DataFeed.
 * More information : https://docs.developers.symphony.com/building-bots-on-symphony/datafeed/real-time-events
 */
@API(status = API.Status.INTERNAL)
@Slf4j
enum RealTimeEventType {

  MESSAGESENT((listener, event) -> {
    listener.onMessageSent(event.getInitiator(), proxy(event.getPayload().getMessageSent(), event));
  }),
  MESSAGESUPPRESSED((listener, event) -> {
    listener.onMessageSuppressed(event.getInitiator(), proxy(event.getPayload().getMessageSuppressed(), event));
  }),
  SYMPHONYELEMENTSACTION((listener, event) -> {
    listener.onSymphonyElementsAction(event.getInitiator(),
        proxy(event.getPayload().getSymphonyElementsAction(), event));
  }),
  SHAREDPOST((listener, event) -> {
    listener.onSharedPost(event.getInitiator(), proxy(event.getPayload().getSharedPost(), event));
  }),
  INSTANTMESSAGECREATED((listener, event) -> {
    listener.onInstantMessageCreated(event.getInitiator(), proxy(event.getPayload().getInstantMessageCreated(), event));
  }),
  ROOMCREATED((listener, event) -> {
    listener.onRoomCreated(event.getInitiator(), proxy(event.getPayload().getRoomCreated(), event));
  }),
  ROOMUPDATED((listener, event) -> {
    listener.onRoomUpdated(event.getInitiator(), proxy(event.getPayload().getRoomUpdated(), event));
  }),
  ROOMDEACTIVATED((listener, event) -> {
    listener.onRoomDeactivated(event.getInitiator(), proxy(event.getPayload().getRoomDeactivated(), event));
  }),
  ROOMREACTIVATED((listener, event) -> {
    listener.onRoomReactivated(event.getInitiator(), proxy(event.getPayload().getRoomReactivated(), event));
  }),
  USERJOINEDROOM((listener, event) -> {
    listener.onUserJoinedRoom(event.getInitiator(), proxy(event.getPayload().getUserJoinedRoom(), event));
  }),
  USERLEFTROOM((listener, event) -> {
    listener.onUserLeftRoom(event.getInitiator(), proxy(event.getPayload().getUserLeftRoom(), event));
  }),
  USERREQUESTEDTOJOINROOM((listener, event) -> {
    listener.onUserRequestedToJoinRoom(event.getInitiator(),
        proxy(event.getPayload().getUserRequestedToJoinRoom(), event));
  }),
  ROOMMEMBERPROMOTEDTOOWNER((listener, event) -> {
    listener.onRoomMemberPromotedToOwner(event.getInitiator(),
        proxy(event.getPayload().getRoomMemberPromotedToOwner(), event));
  }),
  ROOMMEMBERDEMOTEDFROMOWNER((listener, event) -> {
    listener.onRoomMemberDemotedFromOwner(event.getInitiator(),
        proxy(event.getPayload().getRoomMemberDemotedFromOwner(), event));
  }),
  CONNECTIONACCEPTED((listener, event) -> {
    listener.onConnectionAccepted(event.getInitiator(), proxy(event.getPayload().getConnectionAccepted(), event));
  }),
  CONNECTIONREQUESTED((listener, event) -> {
    listener.onConnectionRequested(event.getInitiator(), proxy(event.getPayload().getConnectionRequested(), event));
  });

  private final BiConsumer<RealTimeEventListener, V4Event> execConsumer;

  RealTimeEventType(BiConsumer<RealTimeEventListener, V4Event> consumer) {
    this.execConsumer = consumer;
  }

  public static Optional<RealTimeEventType> fromV4Event(V4Event event) {

    if (event == null || event.getType() == null) {
      return Optional.empty();
    }

    try {
      return Optional.of(RealTimeEventType.valueOf(event.getType()));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public void dispatch(RealTimeEventListener listener, V4Event event) {
    this.execConsumer.accept(listener, event);
  }

  /**
   * Build a dynamic proxy on the received event, add two more fields to the new proxy class as a decorator pattern.
   * So that the event original timestamp, and event id are accessible from the new fields.
   * Other method calls will be delegated to the original event object instance.
   *
   * @param event     original event, the type will be used to build the proxy
   * @param realEvent the parent V4Event, from where the event id and timestamp can be read
   * @param <T>       event type
   * @return the new created event proxy instance
   */
  private static <T> T proxy(T event, V4Event realEvent) {
    try {
      T proxyEvent = (T) new ByteBuddy(ClassFileVersion.JAVA_V17)
          .subclass(event.getClass())
          .method(ElementMatchers.any().and(isPublic()))
          .intercept(MethodCall.invokeSelf().on(event).withAllArguments())
          .defineField("eventTimestamp", Long.class, Visibility.PRIVATE)
          .implement(EventPayload.class).intercept(FieldAccessor.ofBeanProperty())
          .make()
          .load(event.getClass().getClassLoader(), ClassLoadingStrategy.UsingLookup.of(MethodHandles
              .privateLookupIn(event.getClass(), MethodHandles.lookup())))
          .getLoaded().getDeclaredConstructor(new Class[] {}).newInstance();
      ((EventPayload) proxyEvent).setEventTimestamp(realEvent.getTimestamp());
      return proxyEvent;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      log.error("Cannot create real time event proxy class - {}", e.getMessage());
      log.debug("", e);
      throw new RuntimeException(e);
    }
  }

}
