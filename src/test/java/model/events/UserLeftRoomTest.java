package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.Stream;
import model.User;
import model.events.UserLeftRoom;
import org.junit.Test;

public class UserLeftRoomTest {
  @Test
  public void UserLeftRoomTest() throws Exception {
    // Arrange and Act
    UserLeftRoom userLeftRoom = new UserLeftRoom();

    // Assert
    assertEquals(null, userLeftRoom.getAffectedUser());
  }

  @Test
  public void getAffectedUserTest() throws Exception {
    // Arrange
    UserLeftRoom userLeftRoom = new UserLeftRoom();

    // Act
    User actual = userLeftRoom.getAffectedUser();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamTest() throws Exception {
    // Arrange
    UserLeftRoom userLeftRoom = new UserLeftRoom();

    // Act
    Stream actual = userLeftRoom.getStream();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAffectedUserTest() throws Exception {
    // Arrange
    UserLeftRoom userLeftRoom = new UserLeftRoom();
    User user = new User();

    // Act
    userLeftRoom.setAffectedUser(user);

    // Assert
    assertSame(user, userLeftRoom.getAffectedUser());
  }

  @Test
  public void setStreamTest() throws Exception {
    // Arrange
    UserLeftRoom userLeftRoom = new UserLeftRoom();
    Stream stream = new Stream();

    // Act
    userLeftRoom.setStream(stream);

    // Assert
    assertSame(stream, userLeftRoom.getStream());
  }
}
