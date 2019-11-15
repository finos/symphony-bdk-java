package authentication;

import authentication.SymExtensionAppRSAAuth;
import authentication.extensionapp.InMemoryTokensRepository;
import configuration.SymConfig;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import sun.security.pkcs.PKCS8Key;

public class SymExtensionAppRSAAuthTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void SymExtensionAppRSAAuthTest() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();
    PKCS8Key appPrivateKey = new PKCS8Key();

    // Act and Assert
    thrown.expect(ProcessingException.class);
    new SymExtensionAppRSAAuth(config, appPrivateKey);
  }

  @Test
  public void SymExtensionAppRSAAuthTest2() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();

    // Act and Assert
    thrown.expect(ProcessingException.class);
    new SymExtensionAppRSAAuth(config);
  }

  @Test
  public void SymExtensionAppRSAAuthTest3() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();
    ClientConfig sessionAuthClientConfig = new ClientConfig();
    InMemoryTokensRepository tokensRepository = new InMemoryTokensRepository();

    // Act and Assert
    thrown.expect(ProcessingException.class);
    new SymExtensionAppRSAAuth(config, sessionAuthClientConfig, tokensRepository);
  }
}
