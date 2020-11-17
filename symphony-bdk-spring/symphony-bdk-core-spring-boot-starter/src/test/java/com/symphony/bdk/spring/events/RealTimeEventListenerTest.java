package com.symphony.bdk.spring.events;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import com.symphony.bdk.gen.api.model.V4ConnectionAccepted;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4InstantMessageCreated;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4MessageSuppressed;
import com.symphony.bdk.gen.api.model.V4RoomCreated;
import com.symphony.bdk.gen.api.model.V4RoomDeactivated;
import com.symphony.bdk.gen.api.model.V4RoomMemberDemotedFromOwner;
import com.symphony.bdk.gen.api.model.V4RoomMemberPromotedToOwner;
import com.symphony.bdk.gen.api.model.V4RoomReactivated;
import com.symphony.bdk.gen.api.model.V4RoomUpdated;
import com.symphony.bdk.gen.api.model.V4SharedPost;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;
import com.symphony.bdk.gen.api.model.V4UserJoinedRoom;
import com.symphony.bdk.gen.api.model.V4UserLeftRoom;
import com.symphony.bdk.gen.api.model.V4UserRequestedToJoinRoom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = RealTimeEventsTestListener.class)
@ExtendWith(SpringExtension.class)
public class RealTimeEventListenerTest {

  @Autowired
  private ApplicationEventPublisher publisher;

  @SpyBean
  private RealTimeEventsTestListener eventListener;

  private RealTimeEventsDispatcher dispatcher;

  private V4Initiator initiator;

  @BeforeEach
  public void setUp() {
    dispatcher = new RealTimeEventsDispatcher(publisher);
    initiator = new V4Initiator();
  }

  @Test
  void testOnMessageSent() {
    final V4MessageSent payload = new V4MessageSent();
    dispatcher.onMessageSent(initiator, payload);

    verify(eventListener, only()).onMessageSent(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnSharedPost() {
    final V4SharedPost payload = new V4SharedPost();
    dispatcher.onSharedPost(initiator, payload);

    verify(eventListener, only()).onSharedPost(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnInstantMessageCreated() {
    final V4InstantMessageCreated payload = new V4InstantMessageCreated();
    dispatcher.onInstantMessageCreated(initiator, payload);

    verify(eventListener, only()).onInstantMessageCreated(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnRoomCreated() {
    final V4RoomCreated payload = new V4RoomCreated();
    dispatcher.onRoomCreated(initiator, payload);

    verify(eventListener, only()).onRoomCreated(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnRoomUpdated() {
    final V4RoomUpdated payload = new V4RoomUpdated();
    dispatcher.onRoomUpdated(initiator, payload);

    verify(eventListener, only()).onRoomUpdated(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnRoomDeactivated() {
    final V4RoomDeactivated payload = new V4RoomDeactivated();

    dispatcher.onRoomDeactivated(initiator, payload);
    verify(eventListener, only()).onRoomDeactivated(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnRoomReactivated() {
    final V4RoomReactivated payload = new V4RoomReactivated();
    dispatcher.onRoomReactivated(initiator, payload);

    verify(eventListener, only()).onRoomReactivated(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnUserRequestedToJoinRoom() {
    final V4UserRequestedToJoinRoom payload = new V4UserRequestedToJoinRoom();
    dispatcher.onUserRequestedToJoinRoom(initiator, payload);

    verify(eventListener, only()).onUserRequestedToJoinRoom(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnUserJoinedRoom() {
    final V4UserJoinedRoom payload = new V4UserJoinedRoom();
    dispatcher.onUserJoinedRoom(initiator, payload);

    verify(eventListener, only()).onUserJoinedRoom(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnUserLeftRoom() {
    final V4UserLeftRoom payload = new V4UserLeftRoom();
    dispatcher.onUserLeftRoom(initiator, payload);

    verify(eventListener, only()).onUserLeftRoom(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnRoomMemberPromotedToOwner() {
    final V4RoomMemberPromotedToOwner payload = new V4RoomMemberPromotedToOwner();
    dispatcher.onRoomMemberPromotedToOwner(initiator, payload);

    verify(eventListener, only()).onRoomMemberPromotedToOwner(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnRoomMemberDemotedFromOwner() {
    final V4RoomMemberDemotedFromOwner payload = new V4RoomMemberDemotedFromOwner();
    dispatcher.onRoomMemberDemotedFromOwner(initiator, payload);

    verify(eventListener, only()).onRoomMemberDemotedFromOwner(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnConnectionAccepted() {
    final V4ConnectionAccepted payload = new V4ConnectionAccepted();
    dispatcher.onConnectionAccepted(initiator, payload);

    verify(eventListener, only()).onConnectionAccepted(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnMessageSuppressed() {
    final V4MessageSuppressed payload = new V4MessageSuppressed();
    dispatcher.onMessageSuppressed(initiator, payload);

    verify(eventListener, only()).onMessageSuppressed(eq(new RealTimeEvent<>(initiator, payload)));
  }

  @Test
  void testOnSymphonyElementsAction() {
    final V4SymphonyElementsAction payload = new V4SymphonyElementsAction();
    dispatcher.onSymphonyElementsAction(initiator, payload);

    verify(eventListener, only()).onSymphonyElementsAction(eq(new RealTimeEvent<>(initiator, payload)));
  }
}
