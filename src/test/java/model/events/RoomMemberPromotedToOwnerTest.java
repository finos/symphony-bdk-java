package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.Stream;
import model.User;
import model.events.RoomMemberPromotedToOwner;
import org.junit.Test;

public class RoomMemberPromotedToOwnerTest {
  @Test
  public void RoomMemberPromotedToOwnerTest() throws Exception {
    // Arrange and Act
    RoomMemberPromotedToOwner roomMemberPromotedToOwner = new RoomMemberPromotedToOwner();

    // Assert
    assertEquals(null, roomMemberPromotedToOwner.getAffectedUser());
  }

  @Test
  public void getAffectedUserTest() throws Exception {
    // Arrange
    RoomMemberPromotedToOwner roomMemberPromotedToOwner = new RoomMemberPromotedToOwner();

    // Act
    User actual = roomMemberPromotedToOwner.getAffectedUser();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamTest() throws Exception {
    // Arrange
    RoomMemberPromotedToOwner roomMemberPromotedToOwner = new RoomMemberPromotedToOwner();

    // Act
    Stream actual = roomMemberPromotedToOwner.getStream();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAffectedUserTest() throws Exception {
    // Arrange
    RoomMemberPromotedToOwner roomMemberPromotedToOwner = new RoomMemberPromotedToOwner();
    User user = new User();

    // Act
    roomMemberPromotedToOwner.setAffectedUser(user);

    // Assert
    assertSame(user, roomMemberPromotedToOwner.getAffectedUser());
  }

  @Test
  public void setStreamTest() throws Exception {
    // Arrange
    RoomMemberPromotedToOwner roomMemberPromotedToOwner = new RoomMemberPromotedToOwner();
    Stream stream = new Stream();

    // Act
    roomMemberPromotedToOwner.setStream(stream);

    // Assert
    assertSame(stream, roomMemberPromotedToOwner.getStream());
  }
}
