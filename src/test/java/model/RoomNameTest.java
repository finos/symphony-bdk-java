package model;

import static org.junit.Assert.assertEquals;
import model.RoomName;
import org.junit.Test;

public class RoomNameTest {
  @Test
  public void RoomNameTest() throws Exception {
    // Arrange and Act
    RoomName roomName = new RoomName();

    // Assert
    assertEquals(null, roomName.getName());
  }

  @Test
  public void getNameTest() throws Exception {
    // Arrange
    RoomName roomName = new RoomName();

    // Act
    String actual = roomName.getName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setNameTest() throws Exception {
    // Arrange
    RoomName roomName = new RoomName();
    String name = "aaaaa";

    // Act
    roomName.setName(name);

    // Assert
    assertEquals("aaaaa", roomName.getName());
  }
}
