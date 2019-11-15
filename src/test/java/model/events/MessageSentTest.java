package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.InboundMessage;
import model.events.MessageSent;
import org.junit.Test;

public class MessageSentTest {
  @Test
  public void MessageSentTest() throws Exception {
    // Arrange and Act
    MessageSent messageSent = new MessageSent();

    // Assert
    assertEquals(null, messageSent.getMessage());
  }

  @Test
  public void getMessageTest() throws Exception {
    // Arrange
    MessageSent messageSent = new MessageSent();

    // Act
    InboundMessage actual = messageSent.getMessage();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setMessageTest() throws Exception {
    // Arrange
    MessageSent messageSent = new MessageSent();
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    messageSent.setMessage(inboundMessage);

    // Assert
    assertSame(inboundMessage, messageSent.getMessage());
  }
}
