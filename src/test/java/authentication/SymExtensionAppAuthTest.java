package authentication;

import authentication.SymExtensionAppAuth;
import configuration.SymConfig;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SymExtensionAppAuthTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void SymExtensionAppAuthTest() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();
    ClientConfig sessionAuthClientConfig = new ClientConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    new SymExtensionAppAuth(config, sessionAuthClientConfig);
  }

  @Test
  public void SymExtensionAppAuthTest2() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    new SymExtensionAppAuth(config);
  }
}
