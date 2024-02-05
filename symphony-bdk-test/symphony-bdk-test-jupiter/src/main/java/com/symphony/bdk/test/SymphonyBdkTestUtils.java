package com.symphony.bdk.test;

import static net.bytebuddy.matcher.ElementMatchers.isPublic;

import com.symphony.bdk.core.service.datafeed.EventPayload;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4Payload;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;
import com.symphony.bdk.gen.api.model.V4User;
import com.symphony.bdk.gen.api.model.V4UserJoinedRoom;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SymphonyBdkTestUtils {
  private static final String PREFIX_MESSAGEML_TAG = "<div data-format=\"PresentationML\" data-version=\"2.0\"><p>\n";
  private static final String PREFIX_MENTION_TAG = "<span class=\"entity\" data-entity-id=\"0\">@";
  private static final String SUFFIX_MENTION_TAG = "</span>";
  private static final String SUFFIX_MESSAGEML_TAG = "</p>\n</div>";
  private static final String PREFIX_MENTION_DATA =
      "{\"0\":{\"id\": [{\"type\":\"com.symphony.user.userId\",\"value\":\"";
  private static final String SUFFIX_MENTION_DATA = "\"}],\"type\":\"com.symphony.user.mention\"}}";
  private static final List<RealTimeEventListener> listeners = new ArrayList<>();

  public static void addListener(RealTimeEventListener listener) {
    listeners.add(listener);
  }

  public static void removeListener(RealTimeEventListener listener) {
    listeners.remove(listener);
  }

  public static void pushMessageToDF(V4User initiator, V4Stream stream, String message) {
    pushMessageToDF(initiator, stream, message, null);
  }

  public static void pushMessageToDF(V4User initiator, V4Stream stream, String message, UserV2 botInfo) {
    pushMessageToDF(initiator, new V4Message().stream(stream)
        .message(new V4MessageContentBuilder().apply(message, botInfo))
        .messageId("message-id")
        .user(initiator)
        .data(new V4MessageDataBuilder().apply(botInfo)));
  }

  public static void pushMessageToDF(V4User initiator, V4Message message) {
    pushEventToDataFeed(new V4Event().initiator(new V4Initiator().user(initiator))
        .payload(new V4Payload().messageSent(new V4MessageSent().message(message)))
        .type(V4EventType.MESSAGESENT.name())
        .id("event-id")
        .timestamp(Instant.now().toEpochMilli()));
  }

  public static void pushElementActionToDF(V4User initiator, V4SymphonyElementsAction elementAction) {
    pushEventToDataFeed(new V4Event().initiator(new V4Initiator().user(initiator))
        .payload(new V4Payload().symphonyElementsAction(elementAction))
        .type(V4EventType.SYMPHONYELEMENTSACTION.name()));
  }

  public static void pushUserJoinedEventToDF(V4User initiator, V4UserJoinedRoom userJoinedRoom) {
    pushEventToDataFeed(new V4Event().initiator(new V4Initiator().user(initiator))
        .payload(new V4Payload().userJoinedRoom(userJoinedRoom))
        .type(V4EventType.USERJOINEDROOM.name()));
  }

  public static void pushEventToDataFeed(V4Event event) {
    final Optional<V4EventType> eventType = V4EventType.fromV4Event(event);

    if (eventType.isEmpty()) {
      return;
    }
    for (RealTimeEventListener listener : listeners) {
      eventType.get().dispatch(listener, event);
    }
  }

  private static <T> T proxy(T event, V4Event realEvent) {
    try {
      T proxyEvent = (T) new ByteBuddy(ClassFileVersion.JAVA_V8)
          .subclass(event.getClass())
          .method(ElementMatchers.any().and(isPublic()))
          .intercept(MethodCall.invokeSelf().on(event).withAllArguments())
          .defineField("eventId", String.class, Visibility.PRIVATE)
          .defineField("eventTimestamp", Long.class, Visibility.PRIVATE)
          .implement(Setter.class).intercept(FieldAccessor.ofBeanProperty())
          .implement(EventPayload.class).intercept(FieldAccessor.ofBeanProperty())
          .make()
          .load(event.getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
          .getLoaded().getDeclaredConstructor().newInstance();
      ((Setter) proxyEvent).setEventId(realEvent.getId());
      ((Setter) proxyEvent).setEventTimestamp(realEvent.getTimestamp());
      return proxyEvent;
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public interface Setter {
    void setEventId(String eventId);

    void setEventTimestamp(Long eventTimestamp);
  }


  public enum V4EventType {

    MESSAGESENT((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getMessageSent() != null : "MessageSent event must not null";
      listener.onMessageSent(event.getInitiator(), proxy(event.getPayload().getMessageSent(), event));
    }),
    MESSAGESUPPRESSED((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getMessageSuppressed() != null : "MessageSuppressed event must not null";
      listener.onMessageSuppressed(event.getInitiator(), proxy(event.getPayload().getMessageSuppressed(), event));
    }),
    SYMPHONYELEMENTSACTION((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getSymphonyElementsAction() != null : "ElementsAction event must not null";
      listener.onSymphonyElementsAction(event.getInitiator(), proxy(event.getPayload().getSymphonyElementsAction(), event));
    }),
    SHAREDPOST((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getSharedPost() != null : "SharedPost event must not null";
      listener.onSharedPost(event.getInitiator(), proxy(event.getPayload().getSharedPost(), event));
    }),
    INSTANTMESSAGECREATED((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getInstantMessageCreated() != null : "InstantMessageCreated event must not null";
      listener.onInstantMessageCreated(event.getInitiator(), proxy(event.getPayload().getInstantMessageCreated(), event));
    }),
    ROOMCREATED((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getRoomCreated() != null : "RoomCreated event must not null";
      listener.onRoomCreated(event.getInitiator(), proxy(event.getPayload().getRoomCreated(), event));
    }),
    ROOMUPDATED((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getRoomUpdated() != null : "RoomUpdated event must not null";
      listener.onRoomUpdated(event.getInitiator(), proxy(event.getPayload().getRoomUpdated(), event));
    }),
    ROOMDEACTIVATED((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getRoomDeactivated() != null : "RoomDeactivated event must not null";
      listener.onRoomDeactivated(event.getInitiator(), proxy(event.getPayload().getRoomDeactivated(), event));
    }),
    ROOMREACTIVATED((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getRoomReactivated() != null : "RoomReactivated event must not null";
      listener.onRoomReactivated(event.getInitiator(), proxy(event.getPayload().getRoomReactivated(), event));
    }),
    USERJOINEDROOM((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getUserJoinedRoom() != null : "UserJoinedRoom event must not null";
      listener.onUserJoinedRoom(event.getInitiator(), proxy(event.getPayload().getUserJoinedRoom(), event));
    }),
    USERLEFTROOM((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getUserLeftRoom() != null : "UserLeftRoom event must not null";
      listener.onUserLeftRoom(event.getInitiator(), proxy(event.getPayload().getUserLeftRoom(), event));
    }),
    USERREQUESTEDTOJOINROOM((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getUserRequestedToJoinRoom() != null : "UserRequestedToJoinRoom event must not null";
      listener.onUserRequestedToJoinRoom(event.getInitiator(), proxy(event.getPayload().getUserRequestedToJoinRoom(), event));
    }),
    ROOMMEMBERPROMOTEDTOOWNER((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getRoomMemberPromotedToOwner() != null : "RoomMemberPromotedToOwner event must not null";
      listener.onRoomMemberPromotedToOwner(event.getInitiator(), proxy(event.getPayload().getRoomMemberPromotedToOwner(), event));
    }),
    ROOMMEMBERDEMOTEDFROMOWNER((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getRoomMemberDemotedFromOwner() != null : "RoomMemberDemotedFromOwner event must not null";
      listener.onRoomMemberDemotedFromOwner(event.getInitiator(), proxy(event.getPayload().getRoomMemberDemotedFromOwner(), event));
    }),
    CONNECTIONACCEPTED((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getConnectionAccepted() != null : "ConnectionAccepted event must not null";
      listener.onConnectionAccepted(event.getInitiator(), proxy(event.getPayload().getConnectionAccepted(), event));
    }),
    CONNECTIONREQUESTED((listener, event) -> {
      assert event.getPayload() != null;
      assert event.getPayload().getConnectionRequested() != null : "ConnectionRequested event must not null";
      listener.onConnectionRequested(event.getInitiator(), proxy(event.getPayload().getConnectionRequested(), event));
    });

    private final BiConsumer<RealTimeEventListener, V4Event> execConsumer;

    V4EventType(BiConsumer<RealTimeEventListener, V4Event> consumer) {
      this.execConsumer = consumer;
    }

    private static Optional<V4EventType> fromV4Event(V4Event event) {

      if (event == null || event.getType() == null) {
        return Optional.empty();
      }

      try {
        return Optional.of(V4EventType.valueOf(event.getType()));
      } catch (IllegalArgumentException e) {
        return Optional.empty();
      }
    }

    private void dispatch(RealTimeEventListener listener, V4Event event) {
      this.execConsumer.accept(listener, event);
    }

  }


  private static class V4MessageContentBuilder implements BiFunction<String, UserV2, String> {

    @Override
    public String apply(String message, UserV2 botInfo) {
      StringBuilder messageMl = new StringBuilder(PREFIX_MESSAGEML_TAG);
      if (Optional.ofNullable(botInfo).isPresent()) {
        messageMl.append(PREFIX_MENTION_TAG);
        messageMl.append(botInfo.getDisplayName());
        messageMl.append(SUFFIX_MENTION_TAG);
        messageMl.append(" ");
      }
      messageMl.append(message);
      messageMl.append(SUFFIX_MESSAGEML_TAG);
      return messageMl.toString();
    }
  }


  private static class V4MessageDataBuilder implements Function<UserV2, String> {

    @Override
    public String apply(UserV2 botInfo) {
      String data = null;
      if (Optional.ofNullable(botInfo).isPresent()) {
        data = PREFIX_MENTION_DATA;
        data += botInfo.getId();
        data += SUFFIX_MENTION_DATA;
      }
      return data;
    }
  }
}
