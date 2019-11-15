package model;

import static org.junit.Assert.assertEquals;
import model.SessionToken;
import org.junit.Test;

public class SessionTokenTest {
  @Test
  public void SessionTokenTest() throws Exception {
    // Arrange and Act
    SessionToken sessionToken = new SessionToken();

    // Assert
    assertEquals(null, sessionToken.getSessionToken());
  }

  @Test
  public void getSessionTokenTest() throws Exception {
    // Arrange
    SessionToken sessionToken = new SessionToken();

    // Act
    String actual = sessionToken.getSessionToken();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setSessionTokenTest() throws Exception {
    // Arrange
    SessionToken sessionToken = new SessionToken();
    String sessionToken1 = "aaaaa";

    // Act
    sessionToken.setSessionToken(sessionToken1);

    // Assert
    assertEquals("aaaaa", sessionToken.getSessionToken());
  }
}
