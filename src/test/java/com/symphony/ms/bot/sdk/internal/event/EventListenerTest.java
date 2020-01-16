package com.symphony.ms.bot.sdk.internal.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.symphony.ms.bot.sdk.internal.event.model.IMCreatedEvent;
import com.symphony.ms.bot.sdk.internal.event.model.MessageEvent;
import com.symphony.ms.bot.sdk.internal.event.model.RoomCreatedEvent;
import com.symphony.ms.bot.sdk.internal.event.model.RoomDeactivatedEvent;
import com.symphony.ms.bot.sdk.internal.event.model.RoomMemberDemotedFromOwnerEvent;
import com.symphony.ms.bot.sdk.internal.event.model.RoomMemberPromotedToOwnerEvent;
import com.symphony.ms.bot.sdk.internal.event.model.RoomReactivatedEvent;
import com.symphony.ms.bot.sdk.internal.event.model.RoomUpdatedEvent;
import com.symphony.ms.bot.sdk.internal.event.model.SymphonyElementsEvent;
import com.symphony.ms.bot.sdk.internal.event.model.UserJoinedRoomEvent;
import com.symphony.ms.bot.sdk.internal.event.model.UserLeftRoomEvent;
import com.symphony.ms.bot.sdk.internal.symphony.DatafeedClient;
import model.InboundMessage;
import model.RoomProperties;
import model.Stream;
import model.User;
import model.events.RoomCreated;
import model.events.RoomDeactivated;
import model.events.RoomMemberDemotedFromOwner;
import model.events.RoomMemberPromotedToOwner;
import model.events.RoomUpdated;
import model.events.SymphonyElementsAction;
import model.events.UserJoinedRoom;
import model.events.UserLeftRoom;

@ExtendWith(MockitoExtension.class)
public class EventListenerTest {

  @Mock
  private DatafeedClient datafeedClient;

  @Mock
  private InternalEventListenerImpl internalEventListener;

  @InjectMocks
  private EventListener eventListener;

  @Test
  public void onRoomMessageSuccessTest() {
    InboundMessage message = mock(InboundMessage.class);
    Stream stream = mock(Stream.class);
    when(stream.getStreamId()).thenReturn("1234");
    when(stream.getStreamType()).thenReturn("ROOM");
    User user = mock(User.class);
    when(user.getUserId()).thenReturn(1L);
    when(message.getStream()).thenReturn(stream);
    when(message.getUser()).thenReturn(user);
    when(message.getMessageText()).thenReturn("some message");

    eventListener.onRoomMessage(message);

    verify(internalEventListener, times(1)).onRoomMessage(any(MessageEvent.class));
  }

  @Test
  public void onRoomMessageErrorTest() {
    InboundMessage message = mock(InboundMessage.class);

    eventListener.onRoomMessage(message);

    verify(internalEventListener, never())
      .onRoomMessage(any(MessageEvent.class));
  }

  @Test
  public void onIMMessageSuccessTest() {
    InboundMessage message = mock(InboundMessage.class);
    Stream stream = mock(Stream.class);
    when(stream.getStreamId()).thenReturn("1234");
    when(stream.getStreamType()).thenReturn("ROOM");
    User user = mock(User.class);
    when(user.getUserId()).thenReturn(1L);
    when(message.getStream()).thenReturn(stream);
    when(message.getUser()).thenReturn(user);
    when(message.getMessageText()).thenReturn("some message");

    eventListener.onIMMessage(message);

    verify(internalEventListener, times(1))
      .onIMMessage(any(MessageEvent.class));
  }

  @Test
  public void onIMMessageErrorTest() {
    InboundMessage message = mock(InboundMessage.class);

    eventListener.onIMMessage(message);

    verify(internalEventListener, never()).onIMMessage(any(MessageEvent.class));
  }

  @Test
  public void onRoomCreatedSuccessTest() {
    RoomCreated event = mock(RoomCreated.class);
    Stream stream = mock(Stream.class);
    when(stream.getStreamId()).thenReturn("1234");
    when(stream.getStreamType()).thenReturn("ROOM");
    RoomProperties props = mock(RoomProperties.class);
    when(event.getStream()).thenReturn(stream);
    when(event.getRoomProperties()).thenReturn(props);

    eventListener.onRoomCreated(event);

    verify(internalEventListener, times(1))
      .onRoomCreated(any(RoomCreatedEvent.class));
  }

  @Test
  public void onRoomUpdatedSuccessTest() {
    RoomUpdated event = mock(RoomUpdated.class);
    Stream stream = mock(Stream.class);
    when(stream.getStreamId()).thenReturn("1234");
    when(stream.getStreamType()).thenReturn("ROOM");
    RoomProperties props = mock(RoomProperties.class);
    when(event.getStream()).thenReturn(stream);
    when(event.getNewRoomProperties()).thenReturn(props);

    eventListener.onRoomUpdated(event);

    verify(internalEventListener, times(1))
      .onRoomUpdated(any(RoomUpdatedEvent.class));
  }

  @Test
  public void onRoomReactivatedSuccessTest() {
    Stream stream = mock(Stream.class);
    when(stream.getStreamId()).thenReturn("1234");
    when(stream.getStreamType()).thenReturn("ROOM");

    eventListener.onRoomReactivated(stream);

    verify(internalEventListener, times(1))
      .onRoomReactivated(any(RoomReactivatedEvent.class));
  }

  @Test
  public void onRoomDeactivatedSuccessTest() {
    RoomDeactivated event = mock(RoomDeactivated.class);
    Stream stream = mock(Stream.class);
    when(stream.getStreamId()).thenReturn("1234");
    when(stream.getStreamType()).thenReturn("ROOM");
    when(event.getStream()).thenReturn(stream);

    eventListener.onRoomDeactivated(event);

    verify(internalEventListener, times(1))
      .onRoomDeactivated(any(RoomDeactivatedEvent.class));
  }

  @Test
  public void onIMCreatedSuccess() {
    Stream stream = mock(Stream.class);
    when(stream.getStreamId()).thenReturn("1234");
    when(stream.getStreamType()).thenReturn("ROOM");

    eventListener.onIMCreated(stream);

    verify(internalEventListener, times(1))
      .onIMCreated(any(IMCreatedEvent.class));
  }

  @Test
  public void onRoomMemberDemotedFromOwnerSuccessTest() {
    RoomMemberDemotedFromOwner event = mock(RoomMemberDemotedFromOwner.class);

    Stream stream = mock(Stream.class);
    when(stream.getStreamId()).thenReturn("1234");
    when(stream.getStreamType()).thenReturn("ROOM");
    when(event.getStream()).thenReturn(stream);

    User user = mock(User.class);
    when(user.getUserId()).thenReturn(1L);
    when(event.getStream()).thenReturn(stream);
    when(event.getAffectedUser()).thenReturn(user);

    eventListener.onRoomMemberDemotedFromOwner(event);

    verify(internalEventListener, times(1))
      .onRoomMemberDemotedFromOwner(any(RoomMemberDemotedFromOwnerEvent.class));
  }

  @Test
  public void onRoomMemberPromotedToOwnerSuccessTest() {
    RoomMemberPromotedToOwner event = mock(RoomMemberPromotedToOwner.class);

    Stream stream = mock(Stream.class);
    when(stream.getStreamId()).thenReturn("1234");
    when(stream.getStreamType()).thenReturn("ROOM");
    when(event.getStream()).thenReturn(stream);

    User user = mock(User.class);
    when(user.getUserId()).thenReturn(1L);
    when(event.getStream()).thenReturn(stream);
    when(event.getAffectedUser()).thenReturn(user);

    eventListener.onRoomMemberPromotedToOwner(event);

    verify(internalEventListener, times(1))
      .onRoomMemberPromotedToOwner(any(RoomMemberPromotedToOwnerEvent.class));
  }

  @Test
  public void onUserJoinedRoomSuccessTest() {
    UserJoinedRoom event = mock(UserJoinedRoom.class);

    Stream stream = mock(Stream.class);
    when(stream.getStreamId()).thenReturn("1234");
    when(stream.getStreamType()).thenReturn("ROOM");
    when(event.getStream()).thenReturn(stream);

    User user = mock(User.class);
    when(user.getUserId()).thenReturn(1L);
    when(event.getAffectedUser()).thenReturn(user);


    eventListener.onUserJoinedRoom(event);

    verify(internalEventListener, times(1))
      .onUserJoinedRoom(any(UserJoinedRoomEvent.class));
  }


  @Test
  public void onUserLeftRoomSuccessTest() {
    UserLeftRoom event = mock(UserLeftRoom.class);

    Stream stream = mock(Stream.class);
    when(stream.getStreamId()).thenReturn("1234");
    when(stream.getStreamType()).thenReturn("ROOM");
    when(event.getStream()).thenReturn(stream);

    User user = mock(User.class);
    when(user.getUserId()).thenReturn(1L);
    when(event.getAffectedUser()).thenReturn(user);


    eventListener.onUserLeftRoom(event);

    verify(internalEventListener, times(1))
      .onUserLeftRoom(any(UserLeftRoomEvent.class));
  }

  @Test
  public void onElementsActionTest() {
    SymphonyElementsAction action = mock(SymphonyElementsAction.class);
    when(action.getStreamId()).thenReturn("1234");
    when(action.getStreamType()).thenReturn("ROOM");
    when(action.getFormId()).thenReturn("987");

    User user = mock(User.class);
    when(user.getUserId()).thenReturn(1L);


    eventListener.onElementsAction(user, action);

    verify(internalEventListener, times(1))
      .onElementsAction(any(SymphonyElementsEvent.class));
  }

}
