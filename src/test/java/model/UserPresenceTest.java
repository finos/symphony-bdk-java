package model;

import static org.junit.Assert.assertEquals;
import model.UserPresence;
import org.junit.Test;

public class UserPresenceTest {
  @Test
  public void UserPresenceTest() throws Exception {
    // Arrange and Act
    UserPresence userPresence = new UserPresence();

    // Assert
    assertEquals(null, userPresence.getTimestamp());
  }

  @Test
  public void getCategoryTest() throws Exception {
    // Arrange
    UserPresence userPresence = new UserPresence();

    // Act
    String actual = userPresence.getCategory();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTimestampTest() throws Exception {
    // Arrange
    UserPresence userPresence = new UserPresence();

    // Act
    Long actual = userPresence.getTimestamp();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserIdTest() throws Exception {
    // Arrange
    UserPresence userPresence = new UserPresence();

    // Act
    Long actual = userPresence.getUserId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setCategoryTest() throws Exception {
    // Arrange
    UserPresence userPresence = new UserPresence();
    String category = "aaaaa";

    // Act
    userPresence.setCategory(category);

    // Assert
    assertEquals("aaaaa", userPresence.getCategory());
  }

  @Test
  public void setTimestampTest() throws Exception {
    // Arrange
    UserPresence userPresence = new UserPresence();
    Long timestamp = new Long(1L);

    // Act
    userPresence.setTimestamp(timestamp);

    // Assert
    assertEquals(Long.valueOf(1L), userPresence.getTimestamp());
  }

  @Test
  public void setUserIdTest() throws Exception {
    // Arrange
    UserPresence userPresence = new UserPresence();
    Long userId = new Long(1L);

    // Act
    userPresence.setUserId(userId);

    // Assert
    assertEquals(Long.valueOf(1L), userPresence.getUserId());
  }
}
