package com.symphony.bdk.spring.events;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4ConnectionAccepted;
import com.symphony.bdk.gen.api.model.V4ConnectionRequested;
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class RealTimeEventsDispatcherTest {

  @Autowired
  private RealTimeEventsDispatcher dispatcher;

  @MockitoSpyBean
  private RealTimeEventListener listener;

  @Test
  void shouldPublishOnMessageSent() {

    final V4Initiator initiator = createInitiator();
    final V4MessageSent payload = mock(V4MessageSent.class);

    this.dispatcher.onMessageSent(initiator, payload);

    verify(this.listener, only()).onMessageSent(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onMessageSent(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnSharedPost() {

    final V4Initiator initiator = createInitiator();
    final V4SharedPost payload = mock(V4SharedPost.class);

    this.dispatcher.onSharedPost(initiator, payload);

    verify(this.listener, only()).onSharedPost(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onSharedPost(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnInstantMessageCreated() {

    final V4Initiator initiator = createInitiator();
    final V4InstantMessageCreated payload = mock(V4InstantMessageCreated.class);

    this.dispatcher.onInstantMessageCreated(initiator, payload);

    verify(this.listener, only()).onInstantMessageCreated(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onInstantMessageCreated(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnRoomCreated() {

    final V4Initiator initiator = createInitiator();
    final V4RoomCreated payload = mock(V4RoomCreated.class);

    this.dispatcher.onRoomCreated(initiator, payload);

    verify(this.listener, only()).onRoomCreated(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onRoomCreated(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnRoomUpdated() {

    final V4Initiator initiator = createInitiator();
    final V4RoomUpdated payload = mock(V4RoomUpdated.class);

    this.dispatcher.onRoomUpdated(initiator, payload);

    verify(this.listener, only()).onRoomUpdated(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onRoomUpdated(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnRoomDeactivated() {

    final V4Initiator initiator = createInitiator();
    final V4RoomDeactivated payload = mock(V4RoomDeactivated.class);

    this.dispatcher.onRoomDeactivated(initiator, payload);

    verify(this.listener, only()).onRoomDeactivated(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onRoomDeactivated(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnRoomReactivated() {

    final V4Initiator initiator = createInitiator();
    final V4RoomReactivated payload = mock(V4RoomReactivated.class);

    this.dispatcher.onRoomReactivated(initiator, payload);

    verify(this.listener, only()).onRoomReactivated(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onRoomReactivated(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnUserRequestedToJoinRoom() {

    final V4Initiator initiator = createInitiator();
    final V4UserRequestedToJoinRoom payload = mock(V4UserRequestedToJoinRoom.class);

    this.dispatcher.onUserRequestedToJoinRoom(initiator, payload);

    verify(this.listener, only()).onUserRequestedToJoinRoom(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onUserRequestedToJoinRoom(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnUserJoinedRoom() {

    final V4Initiator initiator = createInitiator();
    final V4UserJoinedRoom payload = mock(V4UserJoinedRoom.class);

    this.dispatcher.onUserJoinedRoom(initiator, payload);

    verify(this.listener, only()).onUserJoinedRoom(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onUserJoinedRoom(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnUserLeftRoom() {

    final V4Initiator initiator = createInitiator();
    final V4UserLeftRoom payload = mock(V4UserLeftRoom.class);

    this.dispatcher.onUserLeftRoom(initiator, payload);

    verify(this.listener, only()).onUserLeftRoom(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onUserLeftRoom(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnRoomMemberPromotedToOwner() {

    final V4Initiator initiator = createInitiator();
    final V4RoomMemberPromotedToOwner payload = mock(V4RoomMemberPromotedToOwner.class);

    this.dispatcher.onRoomMemberPromotedToOwner(initiator, payload);

    verify(this.listener, only()).onRoomMemberPromotedToOwner(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onRoomMemberPromotedToOwner(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnRoomMemberDemotedFromOwner() {

    final V4Initiator initiator = createInitiator();
    final V4RoomMemberDemotedFromOwner payload = mock(V4RoomMemberDemotedFromOwner.class);

    this.dispatcher.onRoomMemberDemotedFromOwner(initiator, payload);

    verify(this.listener, only()).onRoomMemberDemotedFromOwner(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onRoomMemberDemotedFromOwner(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnConnectionRequested() {

    final V4Initiator initiator = createInitiator();
    final V4ConnectionRequested payload = mock(V4ConnectionRequested.class);

    this.dispatcher.onConnectionRequested(initiator, payload);

    verify(this.listener, only()).onConnectionRequested(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onConnectionRequested(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnConnectionAccepted() {

    final V4Initiator initiator = createInitiator();
    final V4ConnectionAccepted payload = mock(V4ConnectionAccepted.class);

    this.dispatcher.onConnectionAccepted(initiator, payload);

    verify(this.listener, only()).onConnectionAccepted(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onConnectionAccepted(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnMessageSuppressed() {

    final V4Initiator initiator = createInitiator();
    final V4MessageSuppressed payload = mock(V4MessageSuppressed.class);

    this.dispatcher.onMessageSuppressed(initiator, payload);

    verify(this.listener, only()).onMessageSuppressed(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onMessageSuppressed(eq(initiator), eq(payload));
  }

  @Test
  void shouldPublishOnSymphonyElementsAction() {

    final V4Initiator initiator = createInitiator();
    final V4SymphonyElementsAction payload = mock(V4SymphonyElementsAction.class);

    this.dispatcher.onSymphonyElementsAction(initiator, payload);

    verify(this.listener, only()).onSymphonyElementsAction(eq(initiator), eq(payload));
    verify(this.listener, times(1)).onSymphonyElementsAction(eq(initiator), eq(payload));
  }

  private static V4Initiator createInitiator() {
    return mock(V4Initiator.class);
  }
}
