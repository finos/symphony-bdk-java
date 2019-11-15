package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.Stream;
import model.events.IMCreated;
import org.junit.Test;

public class IMCreatedTest {
  @Test
  public void IMCreatedTest() throws Exception {
    // Arrange and Act
    IMCreated iMCreated = new IMCreated();

    // Assert
    assertEquals(null, iMCreated.getStream());
  }

  @Test
  public void getStreamTest() throws Exception {
    // Arrange
    IMCreated iMCreated = new IMCreated();

    // Act
    Stream actual = iMCreated.getStream();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setStreamTest() throws Exception {
    // Arrange
    IMCreated iMCreated = new IMCreated();
    Stream stream = new Stream();

    // Act
    iMCreated.setStream(stream);

    // Assert
    assertSame(stream, iMCreated.getStream());
  }
}
