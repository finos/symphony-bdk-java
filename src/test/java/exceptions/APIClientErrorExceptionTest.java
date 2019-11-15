package exceptions;

import static org.junit.Assert.assertEquals;
import exceptions.APIClientErrorException;
import org.junit.Test;

public class APIClientErrorExceptionTest {
  @Test
  public void APIClientErrorExceptionTest() throws Exception {
    // Arrange
    String message = "aaaaa";

    // Act
    APIClientErrorException aPIClientErrorException = new APIClientErrorException(message);

    // Assert
    assertEquals("exceptions.APIClientErrorException: aaaaa", aPIClientErrorException.toString());
  }
}
