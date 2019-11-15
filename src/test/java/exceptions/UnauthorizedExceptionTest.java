package exceptions;

import static org.junit.Assert.assertEquals;
import exceptions.UnauthorizedException;
import org.junit.Test;

public class UnauthorizedExceptionTest {
  @Test
  public void UnauthorizedExceptionTest() throws Exception {
    // Arrange
    String message = "aaaaa";

    // Act
    UnauthorizedException unauthorizedException = new UnauthorizedException(message);

    // Assert
    assertEquals("exceptions.UnauthorizedException: aaaaa", unauthorizedException.toString());
  }
}
