package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.User;
import model.events.ConnectionRequested;
import org.junit.Test;

public class ConnectionRequestedTest {
  @Test
  public void ConnectionRequestedTest() throws Exception {
    // Arrange and Act
    ConnectionRequested connectionRequested = new ConnectionRequested();

    // Assert
    assertEquals(null, connectionRequested.getToUser());
  }

  @Test
  public void getToUserTest() throws Exception {
    // Arrange
    ConnectionRequested connectionRequested = new ConnectionRequested();

    // Act
    User actual = connectionRequested.getToUser();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setToUserTest() throws Exception {
    // Arrange
    ConnectionRequested connectionRequested = new ConnectionRequested();
    User user = new User();

    // Act
    connectionRequested.setToUser(user);

    // Assert
    assertSame(user, connectionRequested.getToUser());
  }
}
