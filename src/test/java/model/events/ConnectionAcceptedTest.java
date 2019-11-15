package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.User;
import model.events.ConnectionAccepted;
import org.junit.Test;

public class ConnectionAcceptedTest {
  @Test
  public void ConnectionAcceptedTest() throws Exception {
    // Arrange and Act
    ConnectionAccepted connectionAccepted = new ConnectionAccepted();

    // Assert
    assertEquals(null, connectionAccepted.getFromUser());
  }

  @Test
  public void getFromUserTest() throws Exception {
    // Arrange
    ConnectionAccepted connectionAccepted = new ConnectionAccepted();

    // Act
    User actual = connectionAccepted.getFromUser();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setFromUserTest() throws Exception {
    // Arrange
    ConnectionAccepted connectionAccepted = new ConnectionAccepted();
    User user = new User();

    // Act
    connectionAccepted.setFromUser(user);

    // Assert
    assertSame(user, connectionAccepted.getFromUser());
  }
}
