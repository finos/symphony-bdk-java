package model;

import static org.junit.Assert.assertEquals;
import model.UserKey;
import org.junit.Test;

public class UserKeyTest {
  @Test
  public void UserKeyTest() throws Exception {
    // Arrange and Act
    UserKey userKey = new UserKey();

    // Assert
    assertEquals(null, userKey.getKey());
  }

  @Test
  public void getActionTest() throws Exception {
    // Arrange
    UserKey userKey = new UserKey();

    // Act
    String actual = userKey.getAction();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getExpirationDateTest() throws Exception {
    // Arrange
    UserKey userKey = new UserKey();

    // Act
    Long actual = userKey.getExpirationDate();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getKeyTest() throws Exception {
    // Arrange
    UserKey userKey = new UserKey();

    // Act
    String actual = userKey.getKey();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setActionTest() throws Exception {
    // Arrange
    UserKey userKey = new UserKey();
    String action = "aaaaa";

    // Act
    userKey.setAction(action);

    // Assert
    assertEquals("aaaaa", userKey.getAction());
  }

  @Test
  public void setExpirationDateTest() throws Exception {
    // Arrange
    UserKey userKey = new UserKey();
    Long expirationDate = new Long(1L);

    // Act
    userKey.setExpirationDate(expirationDate);

    // Assert
    assertEquals(Long.valueOf(1L), userKey.getExpirationDate());
  }

  @Test
  public void setKeyTest() throws Exception {
    // Arrange
    UserKey userKey = new UserKey();
    String key = "aaaaa";

    // Act
    userKey.setKey(key);

    // Assert
    assertEquals("aaaaa", userKey.getKey());
  }
}
