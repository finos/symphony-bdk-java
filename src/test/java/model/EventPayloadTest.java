package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.EventPayload;
import model.events.ConnectionAccepted;
import model.events.ConnectionRequested;
import model.events.IMCreated;
import model.events.MessageSent;
import model.events.MessageSuppressed;
import model.events.RoomCreated;
import model.events.RoomDeactivated;
import model.events.RoomMemberDemotedFromOwner;
import model.events.RoomMemberPromotedToOwner;
import model.events.RoomReactivated;
import model.events.RoomUpdated;
import model.events.SharedPost;
import model.events.SymphonyElementsAction;
import model.events.UserJoinedRoom;
import model.events.UserLeftRoom;
import org.junit.Test;

public class EventPayloadTest {
  @Test
  public void EventPayloadTest() throws Exception {
    // Arrange and Act
    EventPayload eventPayload = new EventPayload();

    // Assert
    assertEquals(null, eventPayload.getConnectionRequested());
  }

  @Test
  public void getConnectionAcceptedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    ConnectionAccepted actual = eventPayload.getConnectionAccepted();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getConnectionRequestedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    ConnectionRequested actual = eventPayload.getConnectionRequested();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getInstantMessageCreatedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    IMCreated actual = eventPayload.getInstantMessageCreated();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMessageSentTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    MessageSent actual = eventPayload.getMessageSent();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMessageSuppressedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    MessageSuppressed actual = eventPayload.getMessageSuppressed();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRoomCreatedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    RoomCreated actual = eventPayload.getRoomCreated();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRoomDeactivatedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    RoomDeactivated actual = eventPayload.getRoomDeactivated();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRoomMemberDemotedFromOwnerTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    RoomMemberDemotedFromOwner actual = eventPayload.getRoomMemberDemotedFromOwner();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRoomMemberPromotedToOwnerTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    RoomMemberPromotedToOwner actual = eventPayload.getRoomMemberPromotedToOwner();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRoomReactivatedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    RoomReactivated actual = eventPayload.getRoomReactivated();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRoomUpdatedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    RoomUpdated actual = eventPayload.getRoomUpdated();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSharedPostTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    SharedPost actual = eventPayload.getSharedPost();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSymphonyElementsActionTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    SymphonyElementsAction actual = eventPayload.getSymphonyElementsAction();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserJoinedRoomTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    UserJoinedRoom actual = eventPayload.getUserJoinedRoom();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserLeftRoomTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();

    // Act
    UserLeftRoom actual = eventPayload.getUserLeftRoom();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setConnectionAcceptedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    ConnectionAccepted connectionAccepted = new ConnectionAccepted();

    // Act
    eventPayload.setConnectionAccepted(connectionAccepted);

    // Assert
    assertSame(connectionAccepted, eventPayload.getConnectionAccepted());
  }

  @Test
  public void setConnectionRequestedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    ConnectionRequested connectionRequested = new ConnectionRequested();

    // Act
    eventPayload.setConnectionRequested(connectionRequested);

    // Assert
    assertSame(connectionRequested, eventPayload.getConnectionRequested());
  }

  @Test
  public void setInstantMessageCreatedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    IMCreated iMCreated = new IMCreated();

    // Act
    eventPayload.setInstantMessageCreated(iMCreated);

    // Assert
    assertSame(iMCreated, eventPayload.getInstantMessageCreated());
  }

  @Test
  public void setMessageSentTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    MessageSent messageSent = new MessageSent();

    // Act
    eventPayload.setMessageSent(messageSent);

    // Assert
    assertSame(messageSent, eventPayload.getMessageSent());
  }

  @Test
  public void setMessageSuppressedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    MessageSuppressed messageSuppressed = new MessageSuppressed();

    // Act
    eventPayload.setMessageSuppressed(messageSuppressed);

    // Assert
    assertSame(messageSuppressed, eventPayload.getMessageSuppressed());
  }

  @Test
  public void setRoomCreatedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    RoomCreated roomCreated = new RoomCreated();

    // Act
    eventPayload.setRoomCreated(roomCreated);

    // Assert
    assertSame(roomCreated, eventPayload.getRoomCreated());
  }

  @Test
  public void setRoomDeactivatedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    RoomDeactivated roomDeactivated = new RoomDeactivated();

    // Act
    eventPayload.setRoomDeactivated(roomDeactivated);

    // Assert
    assertSame(roomDeactivated, eventPayload.getRoomDeactivated());
  }

  @Test
  public void setRoomMemberDemotedFromOwnerTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    RoomMemberDemotedFromOwner roomMemberDemotedFromOwner = new RoomMemberDemotedFromOwner();

    // Act
    eventPayload.setRoomMemberDemotedFromOwner(roomMemberDemotedFromOwner);

    // Assert
    assertSame(roomMemberDemotedFromOwner, eventPayload.getRoomMemberDemotedFromOwner());
  }

  @Test
  public void setRoomMemberPromotedToOwnerTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    RoomMemberPromotedToOwner roomMemberPromotedToOwner = new RoomMemberPromotedToOwner();

    // Act
    eventPayload.setRoomMemberPromotedToOwner(roomMemberPromotedToOwner);

    // Assert
    assertSame(roomMemberPromotedToOwner, eventPayload.getRoomMemberPromotedToOwner());
  }

  @Test
  public void setRoomReactivatedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    RoomReactivated roomReactivated = new RoomReactivated();

    // Act
    eventPayload.setRoomReactivated(roomReactivated);

    // Assert
    assertSame(roomReactivated, eventPayload.getRoomReactivated());
  }

  @Test
  public void setRoomUpdatedTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    RoomUpdated roomUpdated = new RoomUpdated();

    // Act
    eventPayload.setRoomUpdated(roomUpdated);

    // Assert
    assertSame(roomUpdated, eventPayload.getRoomUpdated());
  }

  @Test
  public void setSharedPostTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    SharedPost sharedPost = new SharedPost();

    // Act
    eventPayload.setSharedPost(sharedPost);

    // Assert
    assertSame(sharedPost, eventPayload.getSharedPost());
  }

  @Test
  public void setSymphonyElementsActionTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    SymphonyElementsAction symphonyElementsAction = new SymphonyElementsAction();

    // Act
    eventPayload.setSymphonyElementsAction(symphonyElementsAction);

    // Assert
    assertSame(symphonyElementsAction, eventPayload.getSymphonyElementsAction());
  }

  @Test
  public void setUserJoinedRoomTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    UserJoinedRoom userJoinedRoom = new UserJoinedRoom();

    // Act
    eventPayload.setUserJoinedRoom(userJoinedRoom);

    // Assert
    assertSame(userJoinedRoom, eventPayload.getUserJoinedRoom());
  }

  @Test
  public void setUserLeftRoomTest() throws Exception {
    // Arrange
    EventPayload eventPayload = new EventPayload();
    UserLeftRoom userLeftRoom = new UserLeftRoom();

    // Act
    eventPayload.setUserLeftRoom(userLeftRoom);

    // Assert
    assertSame(userLeftRoom, eventPayload.getUserLeftRoom());
  }
}
