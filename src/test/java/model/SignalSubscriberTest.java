package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import model.SignalSubscriber;
import org.junit.Test;

public class SignalSubscriberTest {
  @Test
  public void SignalSubscriberTest() throws Exception {
    // Arrange and Act
    SignalSubscriber signalSubscriber = new SignalSubscriber();

    // Assert
    assertEquals(null, signalSubscriber.getTimestamp());
  }

  @Test
  public void getSubscriberNameTest() throws Exception {
    // Arrange
    SignalSubscriber signalSubscriber = new SignalSubscriber();

    // Act
    String actual = signalSubscriber.getSubscriberName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTimestampTest() throws Exception {
    // Arrange
    SignalSubscriber signalSubscriber = new SignalSubscriber();

    // Act
    Long actual = signalSubscriber.getTimestamp();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserIdTest() throws Exception {
    // Arrange
    SignalSubscriber signalSubscriber = new SignalSubscriber();

    // Act
    Long actual = signalSubscriber.getUserId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void isOwnerTest() throws Exception {
    // Arrange
    SignalSubscriber signalSubscriber = new SignalSubscriber();

    // Act
    boolean actual = signalSubscriber.isOwner();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void isPushedTest() throws Exception {
    // Arrange
    SignalSubscriber signalSubscriber = new SignalSubscriber();

    // Act
    boolean actual = signalSubscriber.isPushed();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void setOwnerTest() throws Exception {
    // Arrange
    SignalSubscriber signalSubscriber = new SignalSubscriber();
    boolean owner = true;

    // Act
    signalSubscriber.setOwner(owner);

    // Assert
    assertTrue(signalSubscriber.isOwner());
  }

  @Test
  public void setPushedTest() throws Exception {
    // Arrange
    SignalSubscriber signalSubscriber = new SignalSubscriber();
    boolean pushed = true;

    // Act
    signalSubscriber.setPushed(pushed);

    // Assert
    assertTrue(signalSubscriber.isPushed());
  }

  @Test
  public void setSubscriberNameTest() throws Exception {
    // Arrange
    SignalSubscriber signalSubscriber = new SignalSubscriber();
    String subscriberName = "aaaaa";

    // Act
    signalSubscriber.setSubscriberName(subscriberName);

    // Assert
    assertEquals("aaaaa", signalSubscriber.getSubscriberName());
  }

  @Test
  public void setTimestampTest() throws Exception {
    // Arrange
    SignalSubscriber signalSubscriber = new SignalSubscriber();
    Long timestamp = new Long(1L);

    // Act
    signalSubscriber.setTimestamp(timestamp);

    // Assert
    assertEquals(Long.valueOf(1L), signalSubscriber.getTimestamp());
  }

  @Test
  public void setUserIdTest() throws Exception {
    // Arrange
    SignalSubscriber signalSubscriber = new SignalSubscriber();
    Long userId = new Long(1L);

    // Act
    signalSubscriber.setUserId(userId);

    // Assert
    assertEquals(Long.valueOf(1L), signalSubscriber.getUserId());
  }
}
