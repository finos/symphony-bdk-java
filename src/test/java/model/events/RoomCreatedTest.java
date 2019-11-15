package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.RoomProperties;
import model.Stream;
import model.events.RoomCreated;
import org.junit.Test;

public class RoomCreatedTest {
  @Test
  public void RoomCreatedTest() throws Exception {
    // Arrange and Act
    RoomCreated roomCreated = new RoomCreated();

    // Assert
    assertEquals(null, roomCreated.getRoomProperties());
  }

  @Test
  public void getRoomPropertiesTest() throws Exception {
    // Arrange
    RoomCreated roomCreated = new RoomCreated();

    // Act
    RoomProperties actual = roomCreated.getRoomProperties();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamTest() throws Exception {
    // Arrange
    RoomCreated roomCreated = new RoomCreated();

    // Act
    Stream actual = roomCreated.getStream();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setRoomPropertiesTest() throws Exception {
    // Arrange
    RoomCreated roomCreated = new RoomCreated();
    RoomProperties roomProperties = new RoomProperties();

    // Act
    roomCreated.setRoomProperties(roomProperties);

    // Assert
    assertSame(roomProperties, roomCreated.getRoomProperties());
  }

  @Test
  public void setStreamTest() throws Exception {
    // Arrange
    RoomCreated roomCreated = new RoomCreated();
    Stream stream = new Stream();

    // Act
    roomCreated.setStream(stream);

    // Assert
    assertSame(stream, roomCreated.getStream());
  }
}
