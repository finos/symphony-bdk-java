package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.Room;
import model.RoomInfo;
import org.junit.Test;

public class RoomInfoTest {
  @Test
  public void RoomInfoTest() throws Exception {
    // Arrange and Act
    RoomInfo roomInfo = new RoomInfo();

    // Assert
    assertEquals(null, roomInfo.getRoomAttributes());
  }

  @Test
  public void getRoomAttributesTest() throws Exception {
    // Arrange
    RoomInfo roomInfo = new RoomInfo();

    // Act
    Room actual = roomInfo.getRoomAttributes();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRoomSystemInfoTest() throws Exception {
    // Arrange
    RoomInfo roomInfo = new RoomInfo();

    // Act
    RoomSystemInfo actual = roomInfo.getRoomSystemInfo();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setRoomAttributesTest() throws Exception {
    // Arrange
    RoomInfo roomInfo = new RoomInfo();
    Room room = new Room();

    // Act
    roomInfo.setRoomAttributes(room);

    // Assert
    assertSame(room, roomInfo.getRoomAttributes());
  }

  @Test
  public void setRoomSystemInfoTest() throws Exception {
    // Arrange
    RoomInfo roomInfo = new RoomInfo();
    RoomSystemInfo roomSystemInfo = new RoomSystemInfo();

    // Act
    roomInfo.setRoomSystemInfo(roomSystemInfo);

    // Assert
    assertSame(roomSystemInfo, roomInfo.getRoomSystemInfo());
  }
}
