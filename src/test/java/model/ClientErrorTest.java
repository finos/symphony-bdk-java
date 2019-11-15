package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import model.ClientError;
import org.junit.Test;

public class ClientErrorTest {
  @Test
  public void ClientErrorTest() throws Exception {
    // Arrange and Act
    ClientError clientError = new ClientError();

    // Assert
    assertEquals(0, clientError.getCode());
  }

  @Test
  public void getCodeTest() throws Exception {
    // Arrange
    ClientError clientError = new ClientError();

    // Act
    int actual = clientError.getCode();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getDetailsTest() throws Exception {
    // Arrange
    ClientError clientError = new ClientError();

    // Act
    Object actual = clientError.getDetails();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMessageTest() throws Exception {
    // Arrange
    ClientError clientError = new ClientError();

    // Act
    String actual = clientError.getMessage();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setCodeTest() throws Exception {
    // Arrange
    ClientError clientError = new ClientError();
    int code = 1;

    // Act
    clientError.setCode(code);

    // Assert
    assertEquals(1, clientError.getCode());
  }

  @Test
  public void setDetailsTest() throws Exception {
    // Arrange
    ClientError clientError = new ClientError();
    String details = "aaaaa";

    // Act
    clientError.setDetails(details);

    // Assert
    assertTrue(clientError.getDetails() instanceof String);
  }

  @Test
  public void setMessageTest() throws Exception {
    // Arrange
    ClientError clientError = new ClientError();
    String message = "aaaaa";

    // Act
    clientError.setMessage(message);

    // Assert
    assertEquals("aaaaa", clientError.getMessage());
  }
}
