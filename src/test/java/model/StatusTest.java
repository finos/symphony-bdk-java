package model;

import static org.junit.Assert.assertEquals;
import model.Status;
import model.UserStatus;
import org.junit.Test;

public class StatusTest {
  @Test
  public void StatusTest() throws Exception {
    // Arrange
    UserStatus status = UserStatus.ENABLED;

    // Act
    Status status1 = new Status(status);

    // Assert
    assertEquals("ENABLED", status1.getStatus());
  }

  @Test
  public void StatusTest2() throws Exception {
    // Arrange
    String status = "aaaaa";

    // Act
    Status status1 = new Status(status);

    // Assert
    assertEquals("aaaaa", status1.getStatus());
  }

  @Test
  public void StatusTest3() throws Exception {
    // Arrange and Act
    Status status = new Status();

    // Assert
    assertEquals(null, status.getStatus());
  }

  @Test
  public void getStatusTest() throws Exception {
    // Arrange
    Status status = new Status();

    // Act
    String actual = status.getStatus();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setStatusTest() throws Exception {
    // Arrange
    Status status = new Status();
    UserStatus status1 = UserStatus.ENABLED;

    // Act
    status.setStatus(status1);

    // Assert
    assertEquals("ENABLED", status.getStatus());
  }

  @Test
  public void setStatusTest2() throws Exception {
    // Arrange
    Status status = new Status();
    String status1 = "aaaaa";

    // Act
    status.setStatus(status1);

    // Assert
    assertEquals("aaaaa", status.getStatus());
  }
}
