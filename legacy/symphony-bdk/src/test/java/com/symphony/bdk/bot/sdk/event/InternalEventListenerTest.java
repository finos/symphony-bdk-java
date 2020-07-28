package com.symphony.bdk.bot.sdk.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.symphony.bdk.bot.sdk.command.CommandFilter;
import com.symphony.bdk.bot.sdk.event.EventDispatcher;
import com.symphony.bdk.bot.sdk.event.InternalEventListenerImpl;
import com.symphony.bdk.bot.sdk.event.model.IMCreatedEvent;
import com.symphony.bdk.bot.sdk.event.model.MessageEvent;
import com.symphony.bdk.bot.sdk.event.model.RoomCreatedEvent;
import com.symphony.bdk.bot.sdk.event.model.RoomDeactivatedEvent;
import com.symphony.bdk.bot.sdk.event.model.RoomMemberDemotedFromOwnerEvent;
import com.symphony.bdk.bot.sdk.event.model.RoomMemberPromotedToOwnerEvent;
import com.symphony.bdk.bot.sdk.event.model.RoomReactivatedEvent;
import com.symphony.bdk.bot.sdk.event.model.RoomUpdatedEvent;
import com.symphony.bdk.bot.sdk.event.model.SymphonyElementsEvent;
import com.symphony.bdk.bot.sdk.event.model.UserJoinedRoomEvent;
import com.symphony.bdk.bot.sdk.event.model.UserLeftRoomEvent;

@ExtendWith(MockitoExtension.class)
public class InternalEventListenerTest {

  @Mock
  private CommandFilter commandFilter;

  @Mock
  private EventDispatcher eventDispatcher;

  @InjectMocks
  private InternalEventListenerImpl internalEventListener;

  @Test
  public void onRooMessageTest() {
    MessageEvent event = mock(MessageEvent.class);
    when(event.getStreamId()).thenReturn("12345");

    internalEventListener.onRoomMessage(event);

    verify(commandFilter, times(1)).filter(event);
  }

  @Test
  public void onIMMessageTest() {
    MessageEvent event = mock(MessageEvent.class);
    when(event.getStreamId()).thenReturn("12345");

    internalEventListener.onIMMessage(event);

    verify(commandFilter, times(1)).filter(event);
  }

  @Test
  public void onRoomCreatedTest() {
    RoomCreatedEvent event = mock(RoomCreatedEvent.class);
    when(event.getStreamId()).thenReturn("12345");

    internalEventListener.onRoomCreated(event);

    verify(eventDispatcher, times(1))
      .push(event.getClass().getCanonicalName(), event);
  }

  @Test
  public void onRoomReactivatedTest() {
    RoomReactivatedEvent event = mock(RoomReactivatedEvent.class);
    when(event.getStreamId()).thenReturn("12345");

    internalEventListener.onRoomReactivated(event);

    verify(eventDispatcher, times(1))
      .push(event.getClass().getCanonicalName(), event);
  }

  @Test
  public void onRoomDeactivatedTest() {
    RoomDeactivatedEvent event = mock(RoomDeactivatedEvent.class);
    when(event.getStreamId()).thenReturn("12345");

    internalEventListener.onRoomDeactivated(event);

    verify(eventDispatcher, times(1))
      .push(event.getClass().getCanonicalName(), event);
  }

  @Test
  public void onRoomUpdatedTest() {
    RoomUpdatedEvent event = mock(RoomUpdatedEvent.class);
    when(event.getStreamId()).thenReturn("12345");

    internalEventListener.onRoomUpdated(event);

    verify(eventDispatcher, times(1))
      .push(event.getClass().getCanonicalName(), event);
  }

  @Test
  public void onIMCreatedTest() {
    IMCreatedEvent event = mock(IMCreatedEvent.class);
    when(event.getStreamId()).thenReturn("12345");

    internalEventListener.onIMCreated(event);

    verify(eventDispatcher, times(1))
      .push(event.getClass().getCanonicalName(), event);
  }

  @Test
  public void onRoomMemberDemotedFromOwnerTest() {
    RoomMemberDemotedFromOwnerEvent event = mock(
        RoomMemberDemotedFromOwnerEvent.class);
    when(event.getStreamId()).thenReturn("12345");

    internalEventListener.onRoomMemberDemotedFromOwner(event);

    verify(eventDispatcher, times(1))
      .push(event.getClass().getCanonicalName(), event);
  }

  @Test
  public void onRoomMemberPromotedToOwnerTest() {
    RoomMemberPromotedToOwnerEvent event = mock(
        RoomMemberPromotedToOwnerEvent.class);
    when(event.getStreamId()).thenReturn("12345");

    internalEventListener.onRoomMemberPromotedToOwner(event);

    verify(eventDispatcher, times(1))
      .push(event.getClass().getCanonicalName(), event);
  }

  @Test
  public void onUserJoinedRoomTest() {
    UserJoinedRoomEvent event = mock(UserJoinedRoomEvent.class);
    when(event.getStreamId()).thenReturn("12345");

    internalEventListener.onUserJoinedRoom(event);

    verify(eventDispatcher, times(1))
      .push(event.getClass().getCanonicalName(), event);
  }

  @Test
  public void onUserLeftRoomTest() {
    UserLeftRoomEvent event = mock(UserLeftRoomEvent.class);
    when(event.getStreamId()).thenReturn("12345");

    internalEventListener.onUserLeftRoom(event);

    verify(eventDispatcher, times(1))
      .push(event.getClass().getCanonicalName(), event);
  }

  @Test
  public void onElementsActionTest() {
    SymphonyElementsEvent event = mock(SymphonyElementsEvent.class);
    when(event.getFormId()).thenReturn("12");
    when(event.getUserId()).thenReturn(123L);
    when(event.getStreamId()).thenReturn("1234");

    internalEventListener.onElementsAction(event);

    verify(eventDispatcher, times(1)).push(event.getFormId(), event);
  }

}
