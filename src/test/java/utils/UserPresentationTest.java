package utils;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import utils.UserPresentation;

public class UserPresentationTest {
  @Test
  public void UserPresentationTest() throws Exception {
    // Arrange
    long id = 1L;
    String screenName = "aaaaa";
    String prettyName = "aaaaa";
    String email = "aaaaa";

    // Act
    UserPresentation userPresentation = new UserPresentation(id, screenName, prettyName, email);

    // Assert
    long id1 = userPresentation.getId();
    String prettyName1 = userPresentation.getPrettyName();
    String screenName1 = userPresentation.getScreenName();
    assertEquals(1L, id1);
    assertEquals("aaaaa", userPresentation.getEmail());
    assertEquals("aaaaa", screenName1);
    assertEquals("aaaaa", prettyName1);
  }

  @Test
  public void UserPresentationTest2() throws Exception {
    // Arrange
    long id = 1L;
    String screenName = "aaaaa";
    String prettyName = "aaaaa";

    // Act
    UserPresentation userPresentation = new UserPresentation(id, screenName, prettyName);

    // Assert
    long id1 = userPresentation.getId();
    String prettyName1 = userPresentation.getPrettyName();
    String screenName1 = userPresentation.getScreenName();
    assertEquals(1L, id1);
    assertEquals(null, userPresentation.getEmail());
    assertEquals("aaaaa", screenName1);
    assertEquals("aaaaa", prettyName1);
  }

  @Test
  public void getEmailTest() throws Exception {
    // Arrange
    UserPresentation userPresentation = new UserPresentation(1L, "aaaaa", "aaaaa");

    // Act
    String actual = userPresentation.getEmail();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    UserPresentation userPresentation = new UserPresentation(1L, "aaaaa", "aaaaa");

    // Act
    long actual = userPresentation.getId();

    // Assert
    assertEquals(1L, actual);
  }

  @Test
  public void getPrettyNameTest() throws Exception {
    // Arrange
    UserPresentation userPresentation = new UserPresentation(1L, "aaaaa", "aaaaa");

    // Act
    String actual = userPresentation.getPrettyName();

    // Assert
    assertEquals("aaaaa", actual);
  }

  @Test
  public void getScreenNameTest() throws Exception {
    // Arrange
    UserPresentation userPresentation = new UserPresentation(1L, "aaaaa", "aaaaa");

    // Act
    String actual = userPresentation.getScreenName();

    // Assert
    assertEquals("aaaaa", actual);
  }
}
