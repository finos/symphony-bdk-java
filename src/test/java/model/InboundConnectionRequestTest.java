package model;

import static org.junit.Assert.assertEquals;
import model.InboundConnectionRequest;
import org.junit.Test;

public class InboundConnectionRequestTest {
  @Test
  public void InboundConnectionRequestTest() throws Exception {
    // Arrange and Act
    InboundConnectionRequest inboundConnectionRequest = new InboundConnectionRequest();

    // Assert
    assertEquals(null, inboundConnectionRequest.getFirstRequestedAt());
  }

  @Test
  public void getFirstRequestedAtTest() throws Exception {
    // Arrange
    InboundConnectionRequest inboundConnectionRequest = new InboundConnectionRequest();

    // Act
    Long actual = inboundConnectionRequest.getFirstRequestedAt();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRequestCounterTest() throws Exception {
    // Arrange
    InboundConnectionRequest inboundConnectionRequest = new InboundConnectionRequest();

    // Act
    Integer actual = inboundConnectionRequest.getRequestCounter();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStatusTest() throws Exception {
    // Arrange
    InboundConnectionRequest inboundConnectionRequest = new InboundConnectionRequest();

    // Act
    String actual = inboundConnectionRequest.getStatus();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUpdatedAtTest() throws Exception {
    // Arrange
    InboundConnectionRequest inboundConnectionRequest = new InboundConnectionRequest();

    // Act
    Long actual = inboundConnectionRequest.getUpdatedAt();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserIdTest() throws Exception {
    // Arrange
    InboundConnectionRequest inboundConnectionRequest = new InboundConnectionRequest();

    // Act
    Long actual = inboundConnectionRequest.getUserId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setFirstRequestedAtTest() throws Exception {
    // Arrange
    InboundConnectionRequest inboundConnectionRequest = new InboundConnectionRequest();
    Long firstRequestedAt = new Long(1L);

    // Act
    inboundConnectionRequest.setFirstRequestedAt(firstRequestedAt);

    // Assert
    assertEquals(Long.valueOf(1L), inboundConnectionRequest.getFirstRequestedAt());
  }

  @Test
  public void setRequestCounterTest() throws Exception {
    // Arrange
    InboundConnectionRequest inboundConnectionRequest = new InboundConnectionRequest();
    Integer requestCounter = new Integer(1);

    // Act
    inboundConnectionRequest.setRequestCounter(requestCounter);

    // Assert
    assertEquals(Integer.valueOf(1), inboundConnectionRequest.getRequestCounter());
  }

  @Test
  public void setStatusTest() throws Exception {
    // Arrange
    InboundConnectionRequest inboundConnectionRequest = new InboundConnectionRequest();
    String status = "aaaaa";

    // Act
    inboundConnectionRequest.setStatus(status);

    // Assert
    assertEquals("aaaaa", inboundConnectionRequest.getStatus());
  }

  @Test
  public void setUpdatedAtTest() throws Exception {
    // Arrange
    InboundConnectionRequest inboundConnectionRequest = new InboundConnectionRequest();
    Long updatedAt = new Long(1L);

    // Act
    inboundConnectionRequest.setUpdatedAt(updatedAt);

    // Assert
    assertEquals(Long.valueOf(1L), inboundConnectionRequest.getUpdatedAt());
  }

  @Test
  public void setUserIdTest() throws Exception {
    // Arrange
    InboundConnectionRequest inboundConnectionRequest = new InboundConnectionRequest();
    Long userId = new Long(1L);

    // Act
    inboundConnectionRequest.setUserId(userId);

    // Assert
    assertEquals(Long.valueOf(1L), inboundConnectionRequest.getUserId());
  }
}
