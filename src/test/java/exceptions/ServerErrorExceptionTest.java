package exceptions;

import static org.junit.Assert.assertEquals;
import exceptions.ServerErrorException;
import org.junit.Test;

public class ServerErrorExceptionTest {
  @Test
  public void ServerErrorExceptionTest() throws Exception {
    // Arrange
    String message = "aaaaa";

    // Act
    ServerErrorException serverErrorException = new ServerErrorException(message);

    // Assert
    assertEquals("exceptions.ServerErrorException: aaaaa", serverErrorException.toString());
  }
}
