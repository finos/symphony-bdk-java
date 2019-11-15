package model;

import static org.junit.Assert.assertEquals;
import model.OutboundImportMessage;
import org.junit.Test;

public class OutboundImportMessageTest {
  @Test
  public void OutboundImportMessageTest() throws Exception {
    // Arrange and Act
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();

    // Assert
    assertEquals(0L, outboundImportMessage.getIntendedMessageFromUserId());
  }

  @Test
  public void getDataTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();

    // Act
    String actual = outboundImportMessage.getData();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getIntendedMessageFromUserIdTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();

    // Act
    long actual = outboundImportMessage.getIntendedMessageFromUserId();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getIntendedMessageTimestampTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();

    // Act
    long actual = outboundImportMessage.getIntendedMessageTimestamp();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getMessageTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();

    // Act
    String actual = outboundImportMessage.getMessage();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getOriginalMessageIdTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();

    // Act
    String actual = outboundImportMessage.getOriginalMessageId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getOriginatingSystemIdTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();

    // Act
    String actual = outboundImportMessage.getOriginatingSystemId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamIdTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();

    // Act
    String actual = outboundImportMessage.getStreamId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setDataTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();
    String data = "aaaaa";

    // Act
    outboundImportMessage.setData(data);

    // Assert
    assertEquals("aaaaa", outboundImportMessage.getData());
  }

  @Test
  public void setIntendedMessageFromUserIdTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();
    long intendedMessageFromUserId = 1L;

    // Act
    outboundImportMessage.setIntendedMessageFromUserId(intendedMessageFromUserId);

    // Assert
    assertEquals(1L, outboundImportMessage.getIntendedMessageFromUserId());
  }

  @Test
  public void setIntendedMessageTimestampTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();
    long intendedMessageTimestamp = 1L;

    // Act
    outboundImportMessage.setIntendedMessageTimestamp(intendedMessageTimestamp);

    // Assert
    assertEquals(1L, outboundImportMessage.getIntendedMessageTimestamp());
  }

  @Test
  public void setMessageTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();
    String message = "aaaaa";

    // Act
    outboundImportMessage.setMessage(message);

    // Assert
    assertEquals("aaaaa", outboundImportMessage.getMessage());
  }

  @Test
  public void setOriginalMessageIdTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();
    String originalMessageId = "aaaaa";

    // Act
    outboundImportMessage.setOriginalMessageId(originalMessageId);

    // Assert
    assertEquals("aaaaa", outboundImportMessage.getOriginalMessageId());
  }

  @Test
  public void setOriginatingSystemIdTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();
    String originatingSystemId = "aaaaa";

    // Act
    outboundImportMessage.setOriginatingSystemId(originatingSystemId);

    // Assert
    assertEquals("aaaaa", outboundImportMessage.getOriginatingSystemId());
  }

  @Test
  public void setStreamIdTest() throws Exception {
    // Arrange
    OutboundImportMessage outboundImportMessage = new OutboundImportMessage();
    String streamId = "aaaaa";

    // Act
    outboundImportMessage.setStreamId(streamId);

    // Assert
    assertEquals("aaaaa", outboundImportMessage.getStreamId());
  }
}
