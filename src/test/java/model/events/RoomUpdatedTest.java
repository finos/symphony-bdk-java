package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.RoomProperties;
import model.Stream;
import model.events.RoomUpdated;
import org.junit.Test;

public class RoomUpdatedTest {
  @Test
  public void RoomUpdatedTest() throws Exception {
    // Arrange and Act
    RoomUpdated roomUpdated = new RoomUpdated();

    // Assert
    assertEquals(null, roomUpdated.getNewRoomProperties());
  }

  @Test
  public void getNewRoomPropertiesTest() throws Exception {
    // Arrange
    RoomUpdated roomUpdated = new RoomUpdated();

    // Act
    RoomProperties actual = roomUpdated.getNewRoomProperties();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamTest() throws Exception {
    // Arrange
    RoomUpdated roomUpdated = new RoomUpdated();

    // Act
    Stream actual = roomUpdated.getStream();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setNewRoomPropertiesTest() throws Exception {
    // Arrange
    RoomUpdated roomUpdated = new RoomUpdated();
    RoomProperties roomProperties = new RoomProperties();

    // Act
    roomUpdated.setNewRoomProperties(roomProperties);

    // Assert
    assertSame(roomProperties, roomUpdated.getNewRoomProperties());
  }

  @Test
  public void setStreamTest() throws Exception {
    // Arrange
    RoomUpdated roomUpdated = new RoomUpdated();
    Stream stream = new Stream();

    // Act
    roomUpdated.setStream(stream);

    // Assert
    assertSame(stream, roomUpdated.getStream());
  }
}
