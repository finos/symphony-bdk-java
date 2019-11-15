package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import model.SuppressionResult;
import org.junit.Test;

public class SuppressionResultTest {
  @Test
  public void SuppressionResultTest() throws Exception {
    // Arrange and Act
    SuppressionResult suppressionResult = new SuppressionResult();

    // Assert
    assertEquals(null, suppressionResult.getMessageId());
  }

  @Test
  public void getMessageIdTest() throws Exception {
    // Arrange
    SuppressionResult suppressionResult = new SuppressionResult();

    // Act
    String actual = suppressionResult.getMessageId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSuppressionDateTest() throws Exception {
    // Arrange
    SuppressionResult suppressionResult = new SuppressionResult();

    // Act
    long actual = suppressionResult.getSuppressionDate();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void isSuppressedTest() throws Exception {
    // Arrange
    SuppressionResult suppressionResult = new SuppressionResult();

    // Act
    boolean actual = suppressionResult.isSuppressed();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void setMessageIdTest() throws Exception {
    // Arrange
    SuppressionResult suppressionResult = new SuppressionResult();
    String messageId = "aaaaa";

    // Act
    suppressionResult.setMessageId(messageId);

    // Assert
    assertEquals("aaaaa", suppressionResult.getMessageId());
  }

  @Test
  public void setSuppressedTest() throws Exception {
    // Arrange
    SuppressionResult suppressionResult = new SuppressionResult();
    boolean suppressed = true;

    // Act
    suppressionResult.setSuppressed(suppressed);

    // Assert
    assertTrue(suppressionResult.isSuppressed());
  }

  @Test
  public void setSuppressionDateTest() throws Exception {
    // Arrange
    SuppressionResult suppressionResult = new SuppressionResult();
    long suppressionDate = 1L;

    // Act
    suppressionResult.setSuppressionDate(suppressionDate);

    // Assert
    assertEquals(1L, suppressionResult.getSuppressionDate());
  }
}
