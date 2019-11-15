package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.Stream;
import model.events.RoomReactivated;
import org.junit.Test;

public class RoomReactivatedTest {
  @Test
  public void RoomReactivatedTest() throws Exception {
    // Arrange and Act
    RoomReactivated roomReactivated = new RoomReactivated();

    // Assert
    assertEquals(null, roomReactivated.getStream());
  }

  @Test
  public void getStreamTest() throws Exception {
    // Arrange
    RoomReactivated roomReactivated = new RoomReactivated();

    // Act
    Stream actual = roomReactivated.getStream();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setStreamTest() throws Exception {
    // Arrange
    RoomReactivated roomReactivated = new RoomReactivated();
    Stream stream = new Stream();

    // Act
    roomReactivated.setStream(stream);

    // Assert
    assertSame(stream, roomReactivated.getStream());
  }
}
