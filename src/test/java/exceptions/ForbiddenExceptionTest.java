package exceptions;

import static org.junit.Assert.assertEquals;
import exceptions.ForbiddenException;
import org.junit.Test;

public class ForbiddenExceptionTest {
  @Test
  public void ForbiddenExceptionTest() throws Exception {
    // Arrange
    String message = "aaaaa";

    // Act
    ForbiddenException forbiddenException = new ForbiddenException(message);

    // Assert
    assertEquals("exceptions.ForbiddenException: aaaaa", forbiddenException.toString());
  }
}
