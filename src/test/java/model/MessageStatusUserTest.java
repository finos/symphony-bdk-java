package model;

import static org.junit.Assert.assertEquals;
import model.MessageStatusUser;
import org.junit.Test;

public class MessageStatusUserTest {
  @Test
  public void MessageStatusUserTest() throws Exception {
    // Arrange and Act
    MessageStatusUser messageStatusUser = new MessageStatusUser();

    // Assert
    assertEquals(null, messageStatusUser.getLastName());
  }

  @Test
  public void getEmailTest() throws Exception {
    // Arrange
    MessageStatusUser messageStatusUser = new MessageStatusUser();

    // Act
    String actual = messageStatusUser.getEmail();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getFirstNameTest() throws Exception {
    // Arrange
    MessageStatusUser messageStatusUser = new MessageStatusUser();

    // Act
    String actual = messageStatusUser.getFirstName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLastNameTest() throws Exception {
    // Arrange
    MessageStatusUser messageStatusUser = new MessageStatusUser();

    // Act
    String actual = messageStatusUser.getLastName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTimestampTest() throws Exception {
    // Arrange
    MessageStatusUser messageStatusUser = new MessageStatusUser();

    // Act
    Long actual = messageStatusUser.getTimestamp();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserIdTest() throws Exception {
    // Arrange
    MessageStatusUser messageStatusUser = new MessageStatusUser();

    // Act
    String actual = messageStatusUser.getUserId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUsernameTest() throws Exception {
    // Arrange
    MessageStatusUser messageStatusUser = new MessageStatusUser();

    // Act
    String actual = messageStatusUser.getUsername();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setEmailTest() throws Exception {
    // Arrange
    MessageStatusUser messageStatusUser = new MessageStatusUser();
    String email = "aaaaa";

    // Act
    messageStatusUser.setEmail(email);

    // Assert
    assertEquals("aaaaa", messageStatusUser.getEmail());
  }

  @Test
  public void setFirstNameTest() throws Exception {
    // Arrange
    MessageStatusUser messageStatusUser = new MessageStatusUser();
    String firstName = "aaaaa";

    // Act
    messageStatusUser.setFirstName(firstName);

    // Assert
    assertEquals("aaaaa", messageStatusUser.getFirstName());
  }

  @Test
  public void setLastNameTest() throws Exception {
    // Arrange
    MessageStatusUser messageStatusUser = new MessageStatusUser();
    String lastName = "aaaaa";

    // Act
    messageStatusUser.setLastName(lastName);

    // Assert
    assertEquals("aaaaa", messageStatusUser.getLastName());
  }

  @Test
  public void setTimestampTest() throws Exception {
    // Arrange
    MessageStatusUser messageStatusUser = new MessageStatusUser();
    Long timestamp = new Long(1L);

    // Act
    messageStatusUser.setTimestamp(timestamp);

    // Assert
    assertEquals(Long.valueOf(1L), messageStatusUser.getTimestamp());
  }

  @Test
  public void setUserIdTest() throws Exception {
    // Arrange
    MessageStatusUser messageStatusUser = new MessageStatusUser();
    String userId = "aaaaa";

    // Act
    messageStatusUser.setUserId(userId);

    // Assert
    assertEquals("aaaaa", messageStatusUser.getUserId());
  }

  @Test
  public void setUsernameTest() throws Exception {
    // Arrange
    MessageStatusUser messageStatusUser = new MessageStatusUser();
    String username = "aaaaa";

    // Act
    messageStatusUser.setUsername(username);

    // Assert
    assertEquals("aaaaa", messageStatusUser.getUsername());
  }
}
