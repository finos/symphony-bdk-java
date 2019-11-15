package exceptions;

import static org.junit.Assert.assertEquals;
import exceptions.SymClientException;
import org.junit.Test;

public class SymClientExceptionTest {
  @Test
  public void SymClientExceptionTest() throws Exception {
    // Arrange
    String message = "aaaaa";

    // Act
    SymClientException symClientException = new SymClientException(message);

    // Assert
    assertEquals("exceptions.SymClientException: aaaaa", symClientException.toString());
  }
}
