package authentication;

import authentication.SymBotAuth;
import configuration.SymConfig;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SymBotAuthTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void SymBotAuthTest() throws Exception {
    // Arrange
    SymConfig inputConfig = new SymConfig();
    ClientConfig sessionAuthClientConfig = new ClientConfig();
    ClientConfig kmAuthClientConfig = new ClientConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    new SymBotAuth(inputConfig, sessionAuthClientConfig, kmAuthClientConfig);
  }

  @Test
  public void SymBotAuthTest2() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    new SymBotAuth(config);
  }
}
