package services;

import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import configuration.SymConfig;
import listeners.ConnectionListener;
import listeners.ElementsListener;
import listeners.IMListener;
import listeners.RoomListener;
import lombok.SneakyThrows;
import model.DatafeedEvent;
import model.EventPayload;
import model.InboundMessage;
import model.Initiator;
import model.Stream;
import model.User;
import model.datafeed.DatafeedV2;
import model.events.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

@RunWith(MockitoJUnitRunner.class)
public class AbstractDatafeedEventsServiceTest {

  @Mock private SymBotClient symBotClient;
  @Mock private DatafeedClient datafeedClient;
  @Mock private DatafeedEvent datafeedEvent;
  @Mock private Initiator initiator;
  @Mock private EventPayload eventPayload;
  @Mock private User user;
  @Mock private MessageSent messageSent;
  @Mock private InboundMessage message;
  @Mock private Stream stream;

  @Before
  public void initClient() {
    SymConfig config = mock(SymConfig.class);
    when(symBotClient.getConfig()).thenReturn(config);
    when(symBotClient.getDatafeedClient()).thenReturn(datafeedClient);
    when(symBotClient.getBotUserId()).thenReturn(1234567890L);
    when(user.getUserId()).thenReturn(12345L);
    when(datafeedClient.listDatafeedId()).thenReturn(new ArrayList<DatafeedV2>());
    when(datafeedClient.createDatafeed()).thenReturn("datafeedId123");
    when(datafeedClient.getAckId()).thenReturn("ackId123");
  }

  @After
  public void desalocation() {
    Mockito.reset(symBotClient);
    Mockito.reset(datafeedClient);
    Mockito.reset(datafeedEvent);
    Mockito.reset(initiator);
    Mockito.reset(eventPayload);
    Mockito.reset(user);
    Mockito.reset(messageSent);
    Mockito.reset(message);
    Mockito.reset(stream);
  }

  //region Listeners Test
  @Test
  public void addRoomListenersTest() {
    final MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
    final int initialSize = service.getRoomListeners().size();
    int newSize;

    // Add first item
    service.addListeners(this.createRoomListener());
    newSize = service.getRoomListeners().size();
    assertEquals(initialSize + 1, newSize);

    // Add second item
    service.addListeners(this.createRoomListener());
    newSize = service.getRoomListeners().size();
    assertEquals(initialSize + 2, newSize);
  }

  @Test
  public void addConnectionListenersTest() {
    final MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
    final int initialSize = service.getConnectionListeners().size();
    int newSize;

    // Add first item
    service.addListeners(this.createConnectionListener());
    newSize = service.getConnectionListeners().size();
    assertEquals(initialSize + 1, newSize);

    // Add second item
    service.addListeners(this.createConnectionListener());
    newSize = service.getConnectionListeners().size();
    assertEquals(initialSize + 2, newSize);
  }

  @Test
  public void addElementsListenersTest() {
    final MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
    final int initialSize = service.getElementsListener().size();
    int newSize;

    // Add first item
    service.addListeners(this.createElementsListener());
    newSize = service.getElementsListener().size();
    assertEquals(initialSize + 1, newSize);

    // Add second item
    service.addListeners(this.createElementsListener());
    newSize = service.getElementsListener().size();
    assertEquals(initialSize + 2, newSize);
  }

  @Test
  public void addIMListenersTest() {
    final MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
    final int initialSize = service.getIMListener().size();
    int newSize;

    // Add first item
    service.addListeners(this.createIMListener());
    newSize = service.getIMListener().size();
    assertEquals(initialSize + 1, newSize);

    // Add second item
    service.addListeners(this.createIMListener());
    newSize = service.getIMListener().size();
    assertEquals(initialSize + 2, newSize);
  }

  @Test
  public void removeRoomListenersTest() {
    final MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
    final RoomListener roomListener1 = this.createRoomListener();
    final RoomListener roomListener2 = this.createRoomListener();
    final RoomListener roomListener3 = this.createRoomListener();

    service.addListeners(roomListener1, roomListener2, roomListener3);

    final int initialSize = service.getRoomListeners().size();
    int newSize;

    service.removeListeners(roomListener1);
    newSize = service.getRoomListeners().size();
    assertEquals(initialSize - 1, newSize);

    service.removeListeners(roomListener3);
    newSize = service.getRoomListeners().size();
    assertEquals(initialSize - 2, newSize);
  }

  @Test
  public void removeConnectionListenersTest() {
    final MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
    final ConnectionListener connectionListener1 = this.createConnectionListener();
    final ConnectionListener connectionListener2 = this.createConnectionListener();
    final ConnectionListener connectionListener3 = this.createConnectionListener();

    service.addListeners(connectionListener1, connectionListener2, connectionListener3);

    final int initialSize = service.getConnectionListeners().size();
    int newSize;

    service.removeListeners(connectionListener1);
    newSize = service.getConnectionListeners().size();
    assertEquals(initialSize - 1, newSize);

    service.removeListeners(connectionListener3);
    newSize = service.getConnectionListeners().size();
    assertEquals(initialSize - 2, newSize);
  }

  @Test
  public void removeElementsListenersTest() {
    final MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
    final ElementsListener elementsListener1 = this.createElementsListener();
    final ElementsListener elementsListener2 = this.createElementsListener();
    final ElementsListener elementsListener3 = this.createElementsListener();

    service.addListeners(elementsListener1, elementsListener2, elementsListener3);

    final int initialSize = service.getElementsListener().size();
    int newSize;

    service.removeListeners(elementsListener1);
    newSize = service.getElementsListener().size();
    assertEquals(initialSize - 1, newSize);

    service.removeListeners(elementsListener3);
    newSize = service.getElementsListener().size();
    assertEquals(initialSize - 2, newSize);
  }

  @Test
  public void removeIMListenersTest() {
    final MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
    final IMListener imListener1 = this.createIMListener();
    final IMListener imListener2 = this.createIMListener();
    final IMListener imListener3 = this.createIMListener();

    service.addListeners(imListener1, imListener2, imListener3);

    final int initialSize = service.getIMListener().size();
    int newSize;

    service.removeListeners(imListener1);
    newSize = service.getIMListener().size();
    assertEquals(initialSize - 1, newSize);

    service.removeListeners(imListener3);
    newSize = service.getIMListener().size();
    assertEquals(initialSize - 2, newSize);
  }
  //endregion

  //region Events Handler Test
  @SneakyThrows
  @Test
  public void handleEventsMessageSentRoomTest() {
    configEventType("MESSAGESENT");
    when(eventPayload.getMessageSent()).thenReturn(messageSent);
    when(messageSent.getMessage()).thenReturn(message);
    when(message.getStream()).thenReturn(stream);
    when(stream.getStreamType()).thenReturn("ROOM");

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final RoomListener listener = this.createRoomListener();
    datafeedEventsService.addRoomListener(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onRoomMessage(message);
  }

  @SneakyThrows
  @Test
  public void handleEventsMessageSentIMTest() {
    configEventType("MESSAGESENT");
    when(eventPayload.getMessageSent()).thenReturn(messageSent);
    when(messageSent.getMessage()).thenReturn(message);
    when(message.getStream()).thenReturn(stream);
    when(stream.getStreamType()).thenReturn("IMTest");

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final IMListener listener = this.createIMListener();
    datafeedEventsService.addIMListener(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onIMMessage(message);
  }

  @SneakyThrows
  @Test
  public void handleEventsInstantMessageCreatedTest() {
    configEventType("INSTANTMESSAGECREATED");
    final IMCreated imCreated = mock(IMCreated.class);
    when(eventPayload.getInstantMessageCreated()).thenReturn(imCreated);
    when(imCreated.getStream()).thenReturn(stream);

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final IMListener listener = this.createIMListener();
    datafeedEventsService.addIMListener(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onIMCreated(stream);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomCreatedTest() {
    configEventType("ROOMCREATED");
    final RoomCreated roomCreated = mock(RoomCreated.class);
    when(eventPayload.getRoomCreated()).thenReturn(roomCreated);

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final RoomListener listener = createRoomListener();
    datafeedEventsService.addRoomListener(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onRoomCreated(roomCreated);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomUpdatedTest() {
    configEventType("ROOMUPDATED");
    final RoomUpdated roomUpdated = mock(RoomUpdated.class);
    when(eventPayload.getRoomUpdated()).thenReturn(roomUpdated);

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final RoomListener listener = createRoomListener();
    datafeedEventsService.addRoomListener(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onRoomUpdated(roomUpdated);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomDeactivatedTest() {
    configEventType("ROOMDEACTIVATED");
    final RoomDeactivated roomDeactivated = mock(RoomDeactivated.class);
    when(eventPayload.getRoomDeactivated()).thenReturn(roomDeactivated);

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final RoomListener listener = createRoomListener();
    datafeedEventsService.addRoomListener(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onRoomDeactivated(roomDeactivated);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomReactivatedTest() {
    configEventType("ROOMREACTIVATED");
    final RoomReactivated roomReactivated = mock(RoomReactivated.class);
    when(eventPayload.getRoomReactivated()).thenReturn(roomReactivated);
    when(roomReactivated.getStream()).thenReturn(stream);

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final RoomListener listener = createRoomListener();
    datafeedEventsService.addRoomListener(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onRoomReactivated(stream);
  }

  @SneakyThrows
  @Test
  public void handleEventsUserJoinedRoomTest() {
    configEventType("USERJOINEDROOM");
    final UserJoinedRoom userJoinedRoom = mock(UserJoinedRoom.class);
    when(eventPayload.getUserJoinedRoom()).thenReturn(userJoinedRoom);

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final RoomListener listener = createRoomListener();
    datafeedEventsService.addRoomListener(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onUserJoinedRoom(userJoinedRoom);
  }

  @SneakyThrows
  @Test
  public void handleEventsUserLeftRoomTest() {
    configEventType("USERLEFTROOM");
    final UserLeftRoom userLeftRoom = mock(UserLeftRoom.class);
    when(eventPayload.getUserLeftRoom()).thenReturn(userLeftRoom);

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final RoomListener listener = createRoomListener();
    datafeedEventsService.addRoomListener(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onUserLeftRoom(userLeftRoom);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomMemberPromotedToOwnerTest() {
    configEventType("ROOMMEMBERPROMOTEDTOOWNER");
    final RoomMemberPromotedToOwner roomMemberPromotedToOwner = mock(RoomMemberPromotedToOwner.class);
    when(eventPayload.getRoomMemberPromotedToOwner()).thenReturn(roomMemberPromotedToOwner);

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final RoomListener listener = createRoomListener();
    datafeedEventsService.addRoomListener(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onRoomMemberPromotedToOwner(roomMemberPromotedToOwner);
  }

  @SneakyThrows
  @Test
  public void handleEventsRoomMemberDemotedFromOwnerTest() {
    configEventType("ROOMMEMBERDEMOTEDFROMOWNER");
    final RoomMemberDemotedFromOwner roomMemberDemotedFromOwner = mock(RoomMemberDemotedFromOwner.class);
    when(eventPayload.getRoomMemberDemotedFromOwner()).thenReturn(roomMemberDemotedFromOwner);

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final RoomListener listener = createRoomListener();
    datafeedEventsService.addRoomListener(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onRoomMemberDemotedFromOwner(roomMemberDemotedFromOwner);
  }

  @SneakyThrows
  @Test
  public void handleEventsConnectionAcceptedTest() {
    configEventType("CONNECTIONACCEPTED");

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final ConnectionListener listener = createConnectionListener();
    datafeedEventsService.addConnectionsListener(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onConnectionAccepted(user);
  }

  @SneakyThrows
  @Test
  public void handleEventsConnectionRequestedTest() {
    configEventType("CONNECTIONREQUESTED");

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final ConnectionListener listener = createConnectionListener();
    datafeedEventsService.addListeners(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onConnectionRequested(user);
  }

  @SneakyThrows
  @Test
  public void handleEventsSymphonyElementActionTest() {
    configEventType("SYMPHONYELEMENTSACTION");
    final SymphonyElementsAction symphonyElementsAction = mock(SymphonyElementsAction.class);
    when(eventPayload.getSymphonyElementsAction()).thenReturn(symphonyElementsAction);

    final MyAbstractDatafeedEventsService datafeedEventsService = new MyAbstractDatafeedEventsService(symBotClient);
    final ElementsListener listener = createElementsListener();
    datafeedEventsService.addElementsListener(listener);

    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);
    datafeedEventsService.handleEvents(datafeedEventList);

    verify(listener, atLeastOnce()).onElementsAction(user, symphonyElementsAction);
  }

  private void configEventType(String datafeedEventType) {
    final ArrayList<DatafeedEvent> datafeedEventList = new ArrayList<DatafeedEvent>();
    datafeedEventList.add(datafeedEvent);

    when(initiator.getUser()).thenReturn(user);
    when(datafeedEvent.getInitiator()).thenReturn(initiator);
    when(datafeedEvent.getPayload()).thenReturn(eventPayload);
    when(datafeedEvent.getType()).thenReturn(datafeedEventType);
    when(datafeedClient.readDatafeed("datafeedId123", "ackId123")).thenReturn(datafeedEventList);
  }
  //endregion

  // Mock Listeners creation
  private RoomListener createRoomListener() {
    return mock(RoomListener.class);
  }
  private ConnectionListener createConnectionListener() {
    return mock(ConnectionListener.class);
  }
  private ElementsListener createElementsListener() { return mock(ElementsListener.class); }
  private IMListener createIMListener() {
    return mock(IMListener.class);
  }
}


/**
 * This class is only used to test 'addListeners' and 'removeListeners' from AbstractDatafeedEventsService
 */
class MyAbstractDatafeedEventsService extends AbstractDatafeedEventsService {

  public MyAbstractDatafeedEventsService(SymBotClient client) {
    super(client);
  }

  public List<RoomListener> getRoomListeners() {
    return this.roomListeners;
  }

  public List<ConnectionListener> getConnectionListeners() {
    return this.connectionListeners;
  }

  public List<ElementsListener> getElementsListener() {
    return this.elementsListeners;
  }

  public List<IMListener> getIMListener() {
    return this.imListeners;
  }

  @Override
  public void readDatafeed() {
  }

  @Override
  public void stopDatafeedService() {
  }

  @Override
  public void restartDatafeedService() {
  }
}
