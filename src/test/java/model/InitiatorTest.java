package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.Initiator;
import model.User;
import org.junit.Test;

public class InitiatorTest {
  @Test
  public void InitiatorTest() throws Exception {
    // Arrange and Act
    Initiator initiator = new Initiator();

    // Assert
    assertEquals(null, initiator.getUser());
  }

  @Test
  public void getUserTest() throws Exception {
    // Arrange
    Initiator initiator = new Initiator();

    // Act
    User actual = initiator.getUser();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setUserTest() throws Exception {
    // Arrange
    Initiator initiator = new Initiator();
    User user = new User();

    // Act
    initiator.setUser(user);

    // Assert
    assertSame(user, initiator.getUser());
  }
}
