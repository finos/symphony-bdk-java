package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.Stream;
import model.User;
import model.events.UserJoinedRoom;
import org.junit.Test;

public class UserJoinedRoomTest {
  @Test
  public void UserJoinedRoomTest() throws Exception {
    // Arrange and Act
    UserJoinedRoom userJoinedRoom = new UserJoinedRoom();

    // Assert
    assertEquals(null, userJoinedRoom.getAffectedUser());
  }

  @Test
  public void getAffectedUserTest() throws Exception {
    // Arrange
    UserJoinedRoom userJoinedRoom = new UserJoinedRoom();

    // Act
    User actual = userJoinedRoom.getAffectedUser();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamTest() throws Exception {
    // Arrange
    UserJoinedRoom userJoinedRoom = new UserJoinedRoom();

    // Act
    Stream actual = userJoinedRoom.getStream();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAffectedUserTest() throws Exception {
    // Arrange
    UserJoinedRoom userJoinedRoom = new UserJoinedRoom();
    User user = new User();

    // Act
    userJoinedRoom.setAffectedUser(user);

    // Assert
    assertSame(user, userJoinedRoom.getAffectedUser());
  }

  @Test
  public void setStreamTest() throws Exception {
    // Arrange
    UserJoinedRoom userJoinedRoom = new UserJoinedRoom();
    Stream stream = new Stream();

    // Act
    userJoinedRoom.setStream(stream);

    // Assert
    assertSame(stream, userJoinedRoom.getStream());
  }
}
