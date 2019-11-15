package authentication;

import authentication.SymOBOAuth;
import configuration.SymConfig;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SymOBOAuthTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void SymOBOAuthTest() throws Exception {
    // Arrange
    SymConfig configuration = new SymConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    new SymOBOAuth(configuration);
  }

  @Test
  public void SymOBOAuthTest2() throws Exception {
    // Arrange
    SymConfig configuration = new SymConfig();
    ClientConfig sessionAuthClientConfig = new ClientConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    new SymOBOAuth(configuration, sessionAuthClientConfig);
  }
}
