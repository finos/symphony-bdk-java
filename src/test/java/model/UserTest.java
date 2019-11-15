package model;

import static org.junit.Assert.assertEquals;
import model.User;
import org.junit.Test;

public class UserTest {
  @Test
  public void UserTest() throws Exception {
    // Arrange and Act
    User user = new User();

    // Assert
    assertEquals(null, user.getLastName());
  }

  @Test
  public void getDisplayNameTest() throws Exception {
    // Arrange
    User user = new User();

    // Act
    String actual = user.getDisplayName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getEmailTest() throws Exception {
    // Arrange
    User user = new User();

    // Act
    String actual = user.getEmail();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getFirstNameTest() throws Exception {
    // Arrange
    User user = new User();

    // Act
    String actual = user.getFirstName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLastNameTest() throws Exception {
    // Arrange
    User user = new User();

    // Act
    String actual = user.getLastName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserIdTest() throws Exception {
    // Arrange
    User user = new User();

    // Act
    Long actual = user.getUserId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUsernameTest() throws Exception {
    // Arrange
    User user = new User();

    // Act
    String actual = user.getUsername();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setDisplayNameTest() throws Exception {
    // Arrange
    User user = new User();
    String displayName = "aaaaa";

    // Act
    user.setDisplayName(displayName);

    // Assert
    assertEquals("aaaaa", user.getDisplayName());
  }

  @Test
  public void setEmailTest() throws Exception {
    // Arrange
    User user = new User();
    String email = "aaaaa";

    // Act
    user.setEmail(email);

    // Assert
    assertEquals("aaaaa", user.getEmail());
  }

  @Test
  public void setFirstNameTest() throws Exception {
    // Arrange
    User user = new User();
    String firstName = "aaaaa";

    // Act
    user.setFirstName(firstName);

    // Assert
    assertEquals("aaaaa", user.getFirstName());
  }

  @Test
  public void setLastNameTest() throws Exception {
    // Arrange
    User user = new User();
    String lastName = "aaaaa";

    // Act
    user.setLastName(lastName);

    // Assert
    assertEquals("aaaaa", user.getLastName());
  }

  @Test
  public void setUserIdTest() throws Exception {
    // Arrange
    User user = new User();
    Long userId = new Long(1L);

    // Act
    user.setUserId(userId);

    // Assert
    assertEquals(Long.valueOf(1L), user.getUserId());
  }

  @Test
  public void setUsernameTest() throws Exception {
    // Arrange
    User user = new User();
    String username = "aaaaa";

    // Act
    user.setUsername(username);

    // Assert
    assertEquals("aaaaa", user.getUsername());
  }
}
