package model;

import static org.junit.Assert.assertEquals;
import model.StreamType;
import model.StreamTypes;
import org.junit.Test;

public class StreamTypeTest {
  @Test
  public void StreamTypeTest() throws Exception {
    // Arrange and Act
    StreamType streamType = new StreamType();

    // Assert
    assertEquals(null, streamType.getType());
  }

  @Test
  public void getTypeTest() throws Exception {
    // Arrange
    StreamType streamType = new StreamType();

    // Act
    StreamTypes actual = streamType.getType();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setTypeTest() throws Exception {
    // Arrange
    StreamType streamType = new StreamType();
    StreamTypes type = StreamTypes.IM;

    // Act
    streamType.setType(type);
  }
}
