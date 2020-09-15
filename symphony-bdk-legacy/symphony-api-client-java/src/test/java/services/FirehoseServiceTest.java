package services;

import clients.SymBotClient;
import clients.symphony.api.FirehoseClient;
import listeners.FirehoseListener;
import lombok.SneakyThrows;
import model.DatafeedEvent;
import model.EventPayload;
import model.InboundMessage;
import model.Initiator;
import model.Stream;
import model.User;
import model.events.ConnectionAccepted;
import model.events.ConnectionRequested;
import model.events.IMCreated;
import model.events.MessageSent;
import model.events.RoomCreated;
import model.events.RoomDeactivated;
import model.events.RoomMemberDemotedFromOwner;
import model.events.RoomMemberPromotedToOwner;
import model.events.RoomReactivated;
import model.events.RoomUpdated;
import model.events.UserJoinedRoom;
import model.events.UserLeftRoom;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class FirehoseServiceTest {
  @Mock private SymBotClient symBotClient;
  @Mock private FirehoseClient firehoseClient;
  @Mock private DatafeedEvent datafeedEvent;
  @Mock private Initiator initiator;
  @Mock private EventPayload eventPayload;
  @Mock private User user;
  @Mock private MessageSent messageSent;
  @Mock private InboundMessage message;
  @Mock private Stream stream;

  @Before
  public void initClient() {
    when(firehoseClient.createFirehose()).thenReturn("123");
    when(symBotClient.getFirehoseClient()).thenReturn(firehoseClient);
    when(symBotClient.getBotUserId()).thenReturn(1234567890L);
    when(user.getUserId()).thenReturn(12345L);
  }

  @SneakyThrows
  @Test
  public void readFirehoseTest() {
    FirehoseService firehoseService = new FirehoseService(symBotClient);
    Thread.sleep(500);
    firehoseService.stopDatafeedService();
    verify(firehoseClient, times(1)).createFirehose();
    verify(firehoseClient, atLeastOnce()).readFirehose("123");
  }

  @SneakyThrows
  @Test
  public void restartDatafeedTest() {
    FirehoseService firehoseService = new FirehoseService(symBotClient, "123456");
    Thread.sleep(500);
    firehoseService.stopDatafeedService();
    verify(firehoseClient, atLeastOnce()).readFirehose("123456");

    firehoseService.restartDatafeedService();
    Thread.sleep(500);
    firehoseService.stopDatafeedService();
    verify(firehoseClient, times(1)).createFirehose();
    verify(firehoseClient, atLeastOnce()).readFirehose("123");
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

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123");
    FirehoseListener listener = createFirehoseListener();
    firehoseService.addListener(listener);
    Thread.sleep(150);

    verify(listener, atLeastOnce()).onRoomMessage(message);
    verify(listener, never()).onIMMessage(message);
  }

  @SneakyThrows
  @Test
  public void handleEventsMessageSentIMTest() {
    configEventType("MESSAGESENT");
    when(eventPayload.getMessageSent()).thenReturn(messageSent);
    when(messageSent.getMessage()).thenReturn(message);
    when(message.getStream()).thenReturn(stream);
    when(stream.getStreamType()).thenReturn("IMTest");

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123");
    FirehoseListener listener = createFirehoseListener();
    firehoseService.addListener(listener);
    Thread.sleep(150);

    verify(listener, atLeastOnce()).onIMMessage(message);
    verify(listener, never()).onRoomMessage(message);
  }

  @SneakyThrows
  @Test
  public void handleEventsInstantMessageCreatedTest() {
    configEventType("INSTANTMESSAGECREATED");
    IMCreated imCreated = mock(IMCreated.class);
    when(eventPayload.getInstantMessageCreated()).thenReturn(imCreated);
    when(imCreated.getStream()).thenReturn(stream);

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123");
    FirehoseListener listener = createFirehoseListener();
    firehoseService.addListener(listener);
    Thread.sleep(150);

    verify(listener, atLeastOnce()).onIMCreated(stream);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomCreatedTest() {
    configEventType("ROOMCREATED");
    RoomCreated roomCreated = mock(RoomCreated.class);
    when(eventPayload.getRoomCreated()).thenReturn(roomCreated);

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123");
    FirehoseListener listener = createFirehoseListener();
    firehoseService.addListener(listener);
    Thread.sleep(150);

    verify(listener, atLeastOnce()).onRoomCreated(roomCreated);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomUpdatedTest() {
    configEventType("ROOMUPDATED");
    RoomUpdated roomUpdated = mock(RoomUpdated.class);
    when(eventPayload.getRoomUpdated()).thenReturn(roomUpdated);

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123");
    FirehoseListener listener = createFirehoseListener();
    firehoseService.addListener(listener);
    Thread.sleep(150);

    verify(listener, atLeastOnce()).onRoomUpdated(roomUpdated);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomDeactivatedTest() {
    configEventType("ROOMDEACTIVATED");
    RoomDeactivated roomDeactivated = mock(RoomDeactivated.class);
    when(eventPayload.getRoomDeactivated()).thenReturn(roomDeactivated);

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123");
    FirehoseListener listener = createFirehoseListener();
    firehoseService.addListener(listener);
    Thread.sleep(150);

    verify(listener, atLeastOnce()).onRoomDeactivated(roomDeactivated);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomReactivatedTest() {
    configEventType("ROOMREACTIVATED");
    RoomReactivated roomReactivated = mock(RoomReactivated.class);
    when(eventPayload.getRoomReactivated()).thenReturn(roomReactivated);
    when(roomReactivated.getStream()).thenReturn(stream);

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123");
    FirehoseListener listener = createFirehoseListener();
    firehoseService.addListener(listener);
    Thread.sleep(150);

    verify(listener, atLeastOnce()).onRoomReactivated(stream);
  }

  @SneakyThrows
  @Test
  public void handleEventsUserJoinedRoomTest() {
    configEventType("USERJOINEDROOM");
    UserJoinedRoom userJoinedRoom = mock(UserJoinedRoom.class);
    when(eventPayload.getUserJoinedRoom()).thenReturn(userJoinedRoom);

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123");
    FirehoseListener listener = createFirehoseListener();
    firehoseService.addListener(listener);
    Thread.sleep(150);

    verify(listener, atLeastOnce()).onUserJoinedRoom(userJoinedRoom);
  }

  @SneakyThrows
  @Test
  public void handleEventsUserLeftRoomTest() {
    configEventType("USERLEFTROOM");
    UserLeftRoom userLeftRoom = mock(UserLeftRoom.class);
    when(eventPayload.getUserLeftRoom()).thenReturn(userLeftRoom);

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123");
    FirehoseListener listener = createFirehoseListener();
    firehoseService.addListener(listener);
    Thread.sleep(150);

    verify(listener, atLeastOnce()).onUserLeftRoom(userLeftRoom);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomMemberPromotedToOwnerTest() {
    configEventType("ROOMMEMBERPROMOTEDTOOWNER");
    RoomMemberPromotedToOwner roomMemberPromotedToOwner = mock(RoomMemberPromotedToOwner.class);
    when(eventPayload.getRoomMemberPromotedToOwner()).thenReturn(roomMemberPromotedToOwner);

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123");
    FirehoseListener listener = createFirehoseListener();
    firehoseService.addListener(listener);
    Thread.sleep(150);

    verify(listener, atLeastOnce()).onRoomMemberPromotedToOwner(roomMemberPromotedToOwner);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomMemberDemotedFromOwnerTest() {
    configEventType("ROOMMEMBERDEMOTEDFROMOWNER");
    RoomMemberDemotedFromOwner roomMemberDemotedFromOwner = mock(RoomMemberDemotedFromOwner.class);
    when(eventPayload.getRoomMemberDemotedFromOwner()).thenReturn(roomMemberDemotedFromOwner);

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123");
    FirehoseListener listener = createFirehoseListener();
    firehoseService.addListener(listener);
    Thread.sleep(150);

    verify(listener, atLeastOnce()).onRoomMemberDemotedFromOwner(roomMemberDemotedFromOwner);
  }

  @SneakyThrows
  @Test
  public void handleEventsConnectionAcceptedTest() {
    configEventType("CONNECTIONACCEPTED");
    ConnectionAccepted connectionAccepted = mock(ConnectionAccepted.class);
    when(connectionAccepted.getFromUser()).thenReturn(user);
    when(eventPayload.getConnectionAccepted()).thenReturn(connectionAccepted);

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123");
    FirehoseListener listener = createFirehoseListener();
    firehoseService.addListener(listener);
    Thread.sleep(150);

    verify(listener, atLeastOnce()).onConnectionAccepted(user);
  }

  @SneakyThrows
  @Test
  public void handleEventsConnectionRequestedTest() {
    configEventType("CONNECTIONREQUESTED");
    ConnectionRequested connectionRequested = mock(ConnectionRequested.class);
    when(connectionRequested.getToUser()).thenReturn(user);
    when(eventPayload.getConnectionRequested()).thenReturn(connectionRequested);

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123");
    FirehoseListener listener = createFirehoseListener();
    firehoseService.addListener(listener);
    Thread.sleep(150);

    verify(listener, atLeastOnce()).onConnectionRequested(user);
  }

  private void configEventType(String datafeedEventType) {
    ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);

    when(initiator.getUser()).thenReturn(user);
    when(datafeedEvent.getInitiator()).thenReturn(initiator);
    when(datafeedEvent.getPayload()).thenReturn(eventPayload);
    when(datafeedEvent.getType()).thenReturn(datafeedEventType);
    when(firehoseClient.readFirehose("123")).thenReturn(datafeedEventList);
  }
  //endregion

  private FirehoseListener createFirehoseListener() { return mock(FirehoseListener.class); }
}
