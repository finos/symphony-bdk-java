package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.Stream;
import model.events.MessageSuppressed;
import org.junit.Test;

public class MessageSuppressedTest {
  @Test
  public void MessageSuppressedTest() throws Exception {
    // Arrange and Act
    MessageSuppressed messageSuppressed = new MessageSuppressed();

    // Assert
    assertEquals(null, messageSuppressed.getMessageId());
  }

  @Test
  public void getMessageIdTest() throws Exception {
    // Arrange
    MessageSuppressed messageSuppressed = new MessageSuppressed();

    // Act
    String actual = messageSuppressed.getMessageId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamTest() throws Exception {
    // Arrange
    MessageSuppressed messageSuppressed = new MessageSuppressed();

    // Act
    Stream actual = messageSuppressed.getStream();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setMessageIdTest() throws Exception {
    // Arrange
    MessageSuppressed messageSuppressed = new MessageSuppressed();
    String messageId = "aaaaa";

    // Act
    messageSuppressed.setMessageId(messageId);

    // Assert
    assertEquals("aaaaa", messageSuppressed.getMessageId());
  }

  @Test
  public void setStreamTest() throws Exception {
    // Arrange
    MessageSuppressed messageSuppressed = new MessageSuppressed();
    Stream stream = new Stream();

    // Act
    messageSuppressed.setStream(stream);

    // Assert
    assertSame(stream, messageSuppressed.getStream());
  }
}
