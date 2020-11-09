package services;

import clients.SymBotClient;
import clients.symphony.api.FirehoseClient;
import exceptions.SymClientException;
import listeners.FirehoseListener;
import lombok.SneakyThrows;
import model.*;
import model.events.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActionFirehoseImplTest {
  @Mock private SymBotClient symBotClient;
  @Mock private FirehoseClient firehoseClient;
  @Mock private DatafeedEvent datafeedEvent;
  @Mock private Initiator initiator;
  @Mock private EventPayload eventPayload;
  @Mock private User user;
  @Mock private MessageSent messageSent;
  @Mock private InboundMessage message;
  @Mock private Stream stream;
  private List<FirehoseListener> listeners;

  @Before
  public void initClient() {
    when(symBotClient.getBotUserId()).thenReturn(1234567890L);
    when(user.getUserId()).thenReturn(12345L);
    listeners = createFirehoseListeners();
  }

  @Test
  public void actionReadFirehoseTest() {
    ArrayList<DatafeedEvent> datafeedEvents = new ArrayList<>();
    datafeedEvents.add(datafeedEvent);
    datafeedEvents.add(datafeedEvent);
    datafeedEvents.add(datafeedEvent);
    when(firehoseClient.readFirehose("123")).thenReturn(datafeedEvents);
    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> receivedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");

    assertEquals(receivedEvents.size(), 3);
  }

  @Test(expected = RuntimeException.class)
  public void actionReadFirehoseFailedTest() {
    when(firehoseClient.readFirehose("123")).thenThrow(SymClientException.class);
    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
  }

  @Test
  public void actionHandleEventsEmptyTest() {
    ArrayList<DatafeedEvent> datafeedEvents = new ArrayList<>();
    ArrayList<FirehoseListener> firehoseListeners = new ArrayList<>();
    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    CompletableFuture<List<DatafeedEvent>> ac = actionFirehoseImpl.actionHandleEvents(datafeedEvents, firehoseListeners);
    assertNull(ac);
  }

  //region Events Handler Test
  @SneakyThrows
  @Test
  public void handleEventsMessageSentRoomTest() {
    configEventType("MESSAGESENT");
    when(eventPayload.getMessageSent()).thenReturn(messageSent);
    when(messageSent.getMessage()).thenReturn(message);
    when(message.getStream()).thenReturn(stream);
    when(stream.getStreamType()).thenReturn("ROOM");

    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> datafeedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
    actionFirehoseImpl.actionHandleEvents(datafeedEvents, listeners);

    verify(listeners.get(0), atLeastOnce()).onRoomMessage(message);
    verify(listeners.get(0), never()).onIMMessage(message);
  }

  @SneakyThrows
  @Test
  public void handleEventsMessageSentIMTest() {
    configEventType("MESSAGESENT");
    when(eventPayload.getMessageSent()).thenReturn(messageSent);
    when(messageSent.getMessage()).thenReturn(message);
    when(message.getStream()).thenReturn(stream);
    when(stream.getStreamType()).thenReturn("IMTest");

    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> datafeedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
    actionFirehoseImpl.actionHandleEvents(datafeedEvents, listeners);

    verify(listeners.get(0), atLeastOnce()).onIMMessage(message);
    verify(listeners.get(0), never()).onRoomMessage(message);
  }

  @SneakyThrows
  @Test
  public void handleEventsInstantMessageCreatedTest() {
    configEventType("INSTANTMESSAGECREATED");
    IMCreated imCreated = mock(IMCreated.class);
    when(eventPayload.getInstantMessageCreated()).thenReturn(imCreated);
    when(imCreated.getStream()).thenReturn(stream);

    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> datafeedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
    actionFirehoseImpl.actionHandleEvents(datafeedEvents, listeners);

    verify(listeners.get(0), atLeastOnce()).onIMCreated(stream);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomCreatedTest() {
    configEventType("ROOMCREATED");
    RoomCreated roomCreated = mock(RoomCreated.class);
    when(eventPayload.getRoomCreated()).thenReturn(roomCreated);

    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> datafeedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
    actionFirehoseImpl.actionHandleEvents(datafeedEvents, listeners);

    verify(listeners.get(0), atLeastOnce()).onRoomCreated(roomCreated);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomUpdatedTest() {
    configEventType("ROOMUPDATED");
    RoomUpdated roomUpdated = mock(RoomUpdated.class);
    when(eventPayload.getRoomUpdated()).thenReturn(roomUpdated);

    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> datafeedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
    actionFirehoseImpl.actionHandleEvents(datafeedEvents, listeners);

    verify(listeners.get(0), atLeastOnce()).onRoomUpdated(roomUpdated);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomDeactivatedTest() {
    configEventType("ROOMDEACTIVATED");
    RoomDeactivated roomDeactivated = mock(RoomDeactivated.class);
    when(eventPayload.getRoomDeactivated()).thenReturn(roomDeactivated);

    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> datafeedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
    actionFirehoseImpl.actionHandleEvents(datafeedEvents, listeners);

    verify(listeners.get(0), atLeastOnce()).onRoomDeactivated(roomDeactivated);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomReactivatedTest() {
    configEventType("ROOMREACTIVATED");
    RoomReactivated roomReactivated = mock(RoomReactivated.class);
    when(eventPayload.getRoomReactivated()).thenReturn(roomReactivated);
    when(roomReactivated.getStream()).thenReturn(stream);

    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> datafeedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
    actionFirehoseImpl.actionHandleEvents(datafeedEvents, listeners);

    verify(listeners.get(0), atLeastOnce()).onRoomReactivated(stream);
  }

  @SneakyThrows
  @Test
  public void handleEventsUserJoinedRoomTest() {
    configEventType("USERJOINEDROOM");
    UserJoinedRoom userJoinedRoom = mock(UserJoinedRoom.class);
    when(eventPayload.getUserJoinedRoom()).thenReturn(userJoinedRoom);

    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> datafeedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
    actionFirehoseImpl.actionHandleEvents(datafeedEvents, listeners);

    verify(listeners.get(0), atLeastOnce()).onUserJoinedRoom(userJoinedRoom);
  }

  @SneakyThrows
  @Test
  public void handleEventsUserLeftRoomTest() {
    configEventType("USERLEFTROOM");
    UserLeftRoom userLeftRoom = mock(UserLeftRoom.class);
    when(eventPayload.getUserLeftRoom()).thenReturn(userLeftRoom);

    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> datafeedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
    actionFirehoseImpl.actionHandleEvents(datafeedEvents, listeners);

    verify(listeners.get(0), atLeastOnce()).onUserLeftRoom(userLeftRoom);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomMemberPromotedToOwnerTest() {
    configEventType("ROOMMEMBERPROMOTEDTOOWNER");
    RoomMemberPromotedToOwner roomMemberPromotedToOwner = mock(RoomMemberPromotedToOwner.class);
    when(eventPayload.getRoomMemberPromotedToOwner()).thenReturn(roomMemberPromotedToOwner);

    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> datafeedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
    actionFirehoseImpl.actionHandleEvents(datafeedEvents, listeners);

    verify(listeners.get(0), atLeastOnce()).onRoomMemberPromotedToOwner(roomMemberPromotedToOwner);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomMemberDemotedFromOwnerTest() {
    configEventType("ROOMMEMBERDEMOTEDFROMOWNER");
    RoomMemberDemotedFromOwner roomMemberDemotedFromOwner = mock(RoomMemberDemotedFromOwner.class);
    when(eventPayload.getRoomMemberDemotedFromOwner()).thenReturn(roomMemberDemotedFromOwner);

    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> datafeedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
    actionFirehoseImpl.actionHandleEvents(datafeedEvents, listeners);

    verify(listeners.get(0), atLeastOnce()).onRoomMemberDemotedFromOwner(roomMemberDemotedFromOwner);
  }

  @SneakyThrows
  @Test
  public void handleEventsConnectionAcceptedTest() {
    configEventType("CONNECTIONACCEPTED");
    ConnectionAccepted connectionAccepted = mock(ConnectionAccepted.class);
    when(connectionAccepted.getFromUser()).thenReturn(user);
    when(eventPayload.getConnectionAccepted()).thenReturn(connectionAccepted);

    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> datafeedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
    actionFirehoseImpl.actionHandleEvents(datafeedEvents, listeners);

    verify(listeners.get(0), atLeastOnce()).onConnectionAccepted(user);
  }

  @SneakyThrows
  @Test
  public void handleEventsConnectionRequestedTest() {
    configEventType("CONNECTIONREQUESTED");
    ConnectionRequested connectionRequested = mock(ConnectionRequested.class);
    when(connectionRequested.getToUser()).thenReturn(user);
    when(eventPayload.getConnectionRequested()).thenReturn(connectionRequested);

    ActionFirehoseImpl actionFirehoseImpl = new ActionFirehoseImpl(symBotClient);
    List<DatafeedEvent> datafeedEvents = actionFirehoseImpl.actionReadFirehose(firehoseClient, "123");
    actionFirehoseImpl.actionHandleEvents(datafeedEvents, listeners);

    verify(listeners.get(0), atLeastOnce()).onConnectionRequested(user);
  }
  //endregion

  private void configEventType(String datafeedEventType) {
    ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<>();
    datafeedEventList.add(datafeedEvent);

    when(initiator.getUser()).thenReturn(user);
    when(datafeedEvent.getInitiator()).thenReturn(initiator);
    when(datafeedEvent.getPayload()).thenReturn(eventPayload);
    when(datafeedEvent.getType()).thenReturn(datafeedEventType);
    when(firehoseClient.readFirehose("123")).thenReturn(datafeedEventList);
  }

  private List<FirehoseListener> createFirehoseListeners() {
    return new ArrayList<FirehoseListener>(){{
      add(mock(FirehoseListener.class));
    }};
  }
}
