package exceptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import exceptions.AuthenticationException;
import org.junit.Test;

public class AuthenticationExceptionTest {
  @Test
  public void AuthenticationExceptionTest() throws Exception {
    // Arrange
    Exception exception = new Exception();

    // Act
    AuthenticationException authenticationException = new AuthenticationException(exception);

    // Assert
    assertSame(exception, authenticationException.getRootException());
  }

  @Test
  public void getRootExceptionTest() throws Exception {
    // Arrange
    AuthenticationException authenticationException = new AuthenticationException(new Exception());

    // Act
    Exception actual = authenticationException.getRootException();

    // Assert
    assertEquals("java.lang.Exception", actual.toString());
  }

  @Test
  public void setRootExceptionTest() throws Exception {
    // Arrange
    AuthenticationException authenticationException = new AuthenticationException(new Exception());
    Exception exception = new Exception();

    // Act
    authenticationException.setRootException(exception);

    // Assert
    assertSame(exception, authenticationException.getRootException());
  }
}
