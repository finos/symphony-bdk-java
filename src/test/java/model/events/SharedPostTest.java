package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.InboundMessage;
import model.events.SharedPost;
import org.junit.Test;

public class SharedPostTest {
  @Test
  public void SharedPostTest() throws Exception {
    // Arrange and Act
    SharedPost sharedPost = new SharedPost();

    // Assert
    assertEquals(null, sharedPost.getMessage());
  }

  @Test
  public void getMessageTest() throws Exception {
    // Arrange
    SharedPost sharedPost = new SharedPost();

    // Act
    InboundMessage actual = sharedPost.getMessage();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSharedMessageTest() throws Exception {
    // Arrange
    SharedPost sharedPost = new SharedPost();

    // Act
    InboundMessage actual = sharedPost.getSharedMessage();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setMessageTest() throws Exception {
    // Arrange
    SharedPost sharedPost = new SharedPost();
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    sharedPost.setMessage(inboundMessage);

    // Assert
    assertSame(inboundMessage, sharedPost.getMessage());
  }

  @Test
  public void setSharedMessageTest() throws Exception {
    // Arrange
    SharedPost sharedPost = new SharedPost();
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    sharedPost.setSharedMessage(inboundMessage);

    // Assert
    assertSame(inboundMessage, sharedPost.getSharedMessage());
  }
}
