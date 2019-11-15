package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.Stream;
import model.User;
import model.events.RoomMemberDemotedFromOwner;
import org.junit.Test;

public class RoomMemberDemotedFromOwnerTest {
  @Test
  public void RoomMemberDemotedFromOwnerTest() throws Exception {
    // Arrange and Act
    RoomMemberDemotedFromOwner roomMemberDemotedFromOwner = new RoomMemberDemotedFromOwner();

    // Assert
    assertEquals(null, roomMemberDemotedFromOwner.getAffectedUser());
  }

  @Test
  public void getAffectedUserTest() throws Exception {
    // Arrange
    RoomMemberDemotedFromOwner roomMemberDemotedFromOwner = new RoomMemberDemotedFromOwner();

    // Act
    User actual = roomMemberDemotedFromOwner.getAffectedUser();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamTest() throws Exception {
    // Arrange
    RoomMemberDemotedFromOwner roomMemberDemotedFromOwner = new RoomMemberDemotedFromOwner();

    // Act
    Stream actual = roomMemberDemotedFromOwner.getStream();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAffectedUserTest() throws Exception {
    // Arrange
    RoomMemberDemotedFromOwner roomMemberDemotedFromOwner = new RoomMemberDemotedFromOwner();
    User user = new User();

    // Act
    roomMemberDemotedFromOwner.setAffectedUser(user);

    // Assert
    assertSame(user, roomMemberDemotedFromOwner.getAffectedUser());
  }

  @Test
  public void setStreamTest() throws Exception {
    // Arrange
    RoomMemberDemotedFromOwner roomMemberDemotedFromOwner = new RoomMemberDemotedFromOwner();
    Stream stream = new Stream();

    // Act
    roomMemberDemotedFromOwner.setStream(stream);

    // Assert
    assertSame(stream, roomMemberDemotedFromOwner.getStream());
  }
}
