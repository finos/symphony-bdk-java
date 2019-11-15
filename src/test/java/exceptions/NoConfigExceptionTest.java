package exceptions;

import static org.junit.Assert.assertEquals;
import exceptions.NoConfigException;
import org.junit.Test;

public class NoConfigExceptionTest {
  @Test
  public void NoConfigExceptionTest() throws Exception {
    // Arrange
    String message = "aaaaa";

    // Act
    NoConfigException noConfigException = new NoConfigException(message);

    // Assert
    assertEquals("exceptions.NoConfigException: aaaaa", noConfigException.toString());
  }
}
