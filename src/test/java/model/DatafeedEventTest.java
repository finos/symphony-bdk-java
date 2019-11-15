package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.DatafeedEvent;
import org.junit.Test;

public class DatafeedEventTest {
  @Test
  public void DatafeedEventTest() throws Exception {
    // Arrange and Act
    DatafeedEvent datafeedEvent = new DatafeedEvent();

    // Assert
    assertEquals(null, datafeedEvent.getTimestamp());
  }

  @Test
  public void getDiagnosticTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();

    // Act
    String actual = datafeedEvent.getDiagnostic();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();

    // Act
    String actual = datafeedEvent.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getInitiatorTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();

    // Act
    Initiator actual = datafeedEvent.getInitiator();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMessageIdTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();

    // Act
    String actual = datafeedEvent.getMessageId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPayloadTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();

    // Act
    EventPayload actual = datafeedEvent.getPayload();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTimestampTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();

    // Act
    Long actual = datafeedEvent.getTimestamp();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTypeTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();

    // Act
    String actual = datafeedEvent.getType();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setDiagnosticTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();
    String diagnostic = "aaaaa";

    // Act
    datafeedEvent.setDiagnostic(diagnostic);

    // Assert
    assertEquals("aaaaa", datafeedEvent.getDiagnostic());
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();
    String id = "aaaaa";

    // Act
    datafeedEvent.setId(id);

    // Assert
    assertEquals("aaaaa", datafeedEvent.getId());
  }

  @Test
  public void setInitiatorTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();
    Initiator initiator = new Initiator();

    // Act
    datafeedEvent.setInitiator(initiator);

    // Assert
    assertSame(initiator, datafeedEvent.getInitiator());
  }

  @Test
  public void setMessageIdTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();
    String messageId = "aaaaa";

    // Act
    datafeedEvent.setMessageId(messageId);

    // Assert
    assertEquals("aaaaa", datafeedEvent.getMessageId());
  }

  @Test
  public void setPayloadTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();
    EventPayload eventPayload = new EventPayload();

    // Act
    datafeedEvent.setPayload(eventPayload);

    // Assert
    assertSame(eventPayload, datafeedEvent.getPayload());
  }

  @Test
  public void setTimestampTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();
    Long timestamp = new Long(1L);

    // Act
    datafeedEvent.setTimestamp(timestamp);

    // Assert
    assertEquals(Long.valueOf(1L), datafeedEvent.getTimestamp());
  }

  @Test
  public void setTypeTest() throws Exception {
    // Arrange
    DatafeedEvent datafeedEvent = new DatafeedEvent();
    String type = "aaaaa";

    // Act
    datafeedEvent.setType(type);

    // Assert
    assertEquals("aaaaa", datafeedEvent.getType());
  }
}
