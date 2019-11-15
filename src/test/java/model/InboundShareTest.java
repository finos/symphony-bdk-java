package model;

import static org.junit.Assert.assertEquals;
import model.InboundShare;
import org.junit.Test;

public class InboundShareTest {
  @Test
  public void InboundShareTest() throws Exception {
    // Arrange and Act
    InboundShare inboundShare = new InboundShare();

    // Assert
    assertEquals(null, inboundShare.getV2messageType());
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    InboundShare inboundShare = new InboundShare();

    // Act
    String actual = inboundShare.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMessageTest() throws Exception {
    // Arrange
    InboundShare inboundShare = new InboundShare();

    // Act
    String actual = inboundShare.getMessage();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamIdTest() throws Exception {
    // Arrange
    InboundShare inboundShare = new InboundShare();

    // Act
    String actual = inboundShare.getStreamId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTimestampTest() throws Exception {
    // Arrange
    InboundShare inboundShare = new InboundShare();

    // Act
    long actual = inboundShare.getTimestamp();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getUserIdTest() throws Exception {
    // Arrange
    InboundShare inboundShare = new InboundShare();

    // Act
    long actual = inboundShare.getUserId();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getV2messageTypeTest() throws Exception {
    // Arrange
    InboundShare inboundShare = new InboundShare();

    // Act
    String actual = inboundShare.getV2messageType();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    InboundShare inboundShare = new InboundShare();
    String id = "aaaaa";

    // Act
    inboundShare.setId(id);

    // Assert
    assertEquals("aaaaa", inboundShare.getId());
  }

  @Test
  public void setMessageTest() throws Exception {
    // Arrange
    InboundShare inboundShare = new InboundShare();
    String message = "aaaaa";

    // Act
    inboundShare.setMessage(message);

    // Assert
    assertEquals("aaaaa", inboundShare.getMessage());
  }

  @Test
  public void setStreamIdTest() throws Exception {
    // Arrange
    InboundShare inboundShare = new InboundShare();
    String streamId = "aaaaa";

    // Act
    inboundShare.setStreamId(streamId);

    // Assert
    assertEquals("aaaaa", inboundShare.getStreamId());
  }

  @Test
  public void setTimestampTest() throws Exception {
    // Arrange
    InboundShare inboundShare = new InboundShare();
    long timestamp = 1L;

    // Act
    inboundShare.setTimestamp(timestamp);

    // Assert
    assertEquals(1L, inboundShare.getTimestamp());
  }

  @Test
  public void setUserIdTest() throws Exception {
    // Arrange
    InboundShare inboundShare = new InboundShare();
    long userId = 1L;

    // Act
    inboundShare.setUserId(userId);

    // Assert
    assertEquals(1L, inboundShare.getUserId());
  }

  @Test
  public void setV2messageTypeTest() throws Exception {
    // Arrange
    InboundShare inboundShare = new InboundShare();
    String v2messageType = "aaaaa";

    // Act
    inboundShare.setV2messageType(v2messageType);

    // Assert
    assertEquals("aaaaa", inboundShare.getV2messageType());
  }
}
