package com.symphony.bdk.test;

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
        .data(new V4MessageDataBuilder().apply(botInfo))
        .timestamp(Instant.now().toEpochMilli()));
  }

  public static void pushMessageToDF(V4User initiator, V4Message message) {
    pushEventToDataFeed(new V4Event().initiator(new V4Initiator().user(initiator))
        .payload(new V4Payload().messageSent(new V4MessageSent().message(message)))
        .type(V4EventType.MESSAGESENT.name()));
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

    if (!eventType.isPresent()) {
      return;
    }
    for (RealTimeEventListener listener : listeners) {
      eventType.get().dispatch(listener, event);
    }
  }

  public enum V4EventType {

    MESSAGESENT((listener, event) -> {
      assert event.getPayload() != null;
      listener.onMessageSent(event.getInitiator(), event.getPayload().getMessageSent());
    }),
    MESSAGESUPPRESSED((listener, event) -> {
      assert event.getPayload() != null;
      listener.onMessageSuppressed(event.getInitiator(), event.getPayload().getMessageSuppressed());
    }),
    SYMPHONYELEMENTSACTION((listener, event) -> {
      assert event.getPayload() != null;
      listener.onSymphonyElementsAction(event.getInitiator(), event.getPayload().getSymphonyElementsAction());
    }),
    SHAREDPOST((listener, event) -> {
      assert event.getPayload() != null;
      listener.onSharedPost(event.getInitiator(), event.getPayload().getSharedPost());
    }),
    INSTANTMESSAGECREATED((listener, event) -> {
      assert event.getPayload() != null;
      listener.onInstantMessageCreated(event.getInitiator(), event.getPayload().getInstantMessageCreated());
    }),
    ROOMCREATED((listener, event) -> {
      assert event.getPayload() != null;
      listener.onRoomCreated(event.getInitiator(), event.getPayload().getRoomCreated());
    }),
    ROOMUPDATED((listener, event) -> {
      assert event.getPayload() != null;
      listener.onRoomUpdated(event.getInitiator(), event.getPayload().getRoomUpdated());
    }),
    ROOMDEACTIVATED((listener, event) -> {
      assert event.getPayload() != null;
      listener.onRoomDeactivated(event.getInitiator(), event.getPayload().getRoomDeactivated());
    }),
    ROOMREACTIVATED((listener, event) -> {
      assert event.getPayload() != null;
      listener.onRoomReactivated(event.getInitiator(), event.getPayload().getRoomReactivated());
    }),
    USERJOINEDROOM((listener, event) -> {
      assert event.getPayload() != null;
      listener.onUserJoinedRoom(event.getInitiator(), event.getPayload().getUserJoinedRoom());
    }),
    USERLEFTROOM((listener, event) -> {
      assert event.getPayload() != null;
      listener.onUserLeftRoom(event.getInitiator(), event.getPayload().getUserLeftRoom());
    }),
    USERREQUESTEDTOJOINROOM((listener, event) -> {
      assert event.getPayload() != null;
      listener.onUserRequestedToJoinRoom(event.getInitiator(), event.getPayload().getUserRequestedToJoinRoom());
    }),
    ROOMMEMBERPROMOTEDTOOWNER((listener, event) -> {
      assert event.getPayload() != null;
      listener.onRoomMemberPromotedToOwner(event.getInitiator(), event.getPayload().getRoomMemberPromotedToOwner());
    }),
    ROOMMEMBERDEMOTEDFROMOWNER((listener, event) -> {
      assert event.getPayload() != null;
      listener.onRoomMemberDemotedFromOwner(event.getInitiator(), event.getPayload().getRoomMemberDemotedFromOwner());
    }),
    CONNECTIONACCEPTED((listener, event) -> {
      assert event.getPayload() != null;
      listener.onConnectionAccepted(event.getInitiator(), event.getPayload().getConnectionAccepted());
    }),
    CONNECTIONREQUESTED((listener, event) -> {
      assert event.getPayload() != null;
      listener.onConnectionRequested(event.getInitiator(), event.getPayload().getConnectionRequested());
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
