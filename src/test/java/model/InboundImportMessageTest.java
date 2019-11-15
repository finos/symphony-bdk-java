package model;

import static org.junit.Assert.assertEquals;
import model.InboundImportMessage;
import org.junit.Test;

public class InboundImportMessageTest {
  @Test
  public void InboundImportMessageTest() throws Exception {
    // Arrange and Act
    InboundImportMessage inboundImportMessage = new InboundImportMessage();

    // Assert
    assertEquals(null, inboundImportMessage.getOriginalMessageId());
  }

  @Test
  public void getDiagnosticTest() throws Exception {
    // Arrange
    InboundImportMessage inboundImportMessage = new InboundImportMessage();

    // Act
    String actual = inboundImportMessage.getDiagnostic();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMessageIdTest() throws Exception {
    // Arrange
    InboundImportMessage inboundImportMessage = new InboundImportMessage();

    // Act
    String actual = inboundImportMessage.getMessageId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getOriginalMessageIdTest() throws Exception {
    // Arrange
    InboundImportMessage inboundImportMessage = new InboundImportMessage();

    // Act
    String actual = inboundImportMessage.getOriginalMessageId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getOriginatingSystemIdTest() throws Exception {
    // Arrange
    InboundImportMessage inboundImportMessage = new InboundImportMessage();

    // Act
    String actual = inboundImportMessage.getOriginatingSystemId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setDiagnosticTest() throws Exception {
    // Arrange
    InboundImportMessage inboundImportMessage = new InboundImportMessage();
    String diagnostic = "aaaaa";

    // Act
    inboundImportMessage.setDiagnostic(diagnostic);

    // Assert
    assertEquals("aaaaa", inboundImportMessage.getDiagnostic());
  }

  @Test
  public void setMessageIdTest() throws Exception {
    // Arrange
    InboundImportMessage inboundImportMessage = new InboundImportMessage();
    String messageId = "aaaaa";

    // Act
    inboundImportMessage.setMessageId(messageId);

    // Assert
    assertEquals("aaaaa", inboundImportMessage.getMessageId());
  }

  @Test
  public void setOriginalMessageIdTest() throws Exception {
    // Arrange
    InboundImportMessage inboundImportMessage = new InboundImportMessage();
    String originalMessageId = "aaaaa";

    // Act
    inboundImportMessage.setOriginalMessageId(originalMessageId);

    // Assert
    assertEquals("aaaaa", inboundImportMessage.getOriginalMessageId());
  }

  @Test
  public void setOriginatingSystemIdTest() throws Exception {
    // Arrange
    InboundImportMessage inboundImportMessage = new InboundImportMessage();
    String originatingSystemId = "aaaaa";

    // Act
    inboundImportMessage.setOriginatingSystemId(originatingSystemId);

    // Assert
    assertEquals("aaaaa", inboundImportMessage.getOriginatingSystemId());
  }
}
