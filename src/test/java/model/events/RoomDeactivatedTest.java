package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.Stream;
import model.events.RoomDeactivated;
import org.junit.Test;

public class RoomDeactivatedTest {
  @Test
  public void RoomDeactivatedTest() throws Exception {
    // Arrange and Act
    RoomDeactivated roomDeactivated = new RoomDeactivated();

    // Assert
    assertEquals(null, roomDeactivated.getStream());
  }

  @Test
  public void getStreamTest() throws Exception {
    // Arrange
    RoomDeactivated roomDeactivated = new RoomDeactivated();

    // Act
    Stream actual = roomDeactivated.getStream();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setStreamTest() throws Exception {
    // Arrange
    RoomDeactivated roomDeactivated = new RoomDeactivated();
    Stream stream = new Stream();

    // Act
    roomDeactivated.setStream(stream);

    // Assert
    assertSame(stream, roomDeactivated.getStream());
  }
}
