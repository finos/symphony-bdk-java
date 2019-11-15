package authentication;

import static org.junit.Assert.assertEquals;
import authentication.SymOBORSAAuth;
import configuration.SymConfig;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SymOBORSAAuthTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void SymOBORSAAuthTest() throws Exception {
    // Arrange
    SymConfig configuration = new SymConfig();
    ClientConfig sessionAuthClientConfig = new ClientConfig();

    // Act
    SymOBORSAAuth symOBORSAAuth = new SymOBORSAAuth(configuration, sessionAuthClientConfig);

    // Assert
    assertEquals(null, symOBORSAAuth.getSessionToken());
  }

  @Test
  public void SymOBORSAAuthTest2() throws Exception {
    // Arrange
    SymConfig configuration = new SymConfig();

    // Act
    SymOBORSAAuth symOBORSAAuth = new SymOBORSAAuth(configuration);

    // Assert
    assertEquals(null, symOBORSAAuth.getSessionToken());
  }

  @Test
  public void authenticateTest() throws Exception {
    // Arrange
    SymOBORSAAuth symOBORSAAuth = new SymOBORSAAuth(new SymConfig());

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    symOBORSAAuth.authenticate();
  }

  @Test
  public void getSessionTokenTest() throws Exception {
    // Arrange
    SymOBORSAAuth symOBORSAAuth = new SymOBORSAAuth(new SymConfig());

    // Act
    String actual = symOBORSAAuth.getSessionToken();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserAuthTest() throws Exception {
    // Arrange
    SymOBORSAAuth symOBORSAAuth = new SymOBORSAAuth(new SymConfig());
    String username = "aaaaa";

    // Act and Assert
    thrown.expect(ProcessingException.class);
    symOBORSAAuth.getUserAuth(username);
  }

  @Test
  public void getUserAuthTest2() throws Exception {
    // Arrange
    SymOBORSAAuth symOBORSAAuth = new SymOBORSAAuth(new SymConfig());
    Long uid = new Long(1L);

    // Act and Assert
    thrown.expect(ProcessingException.class);
    symOBORSAAuth.getUserAuth(uid);
  }

  @Test
  public void setSessionTokenTest() throws Exception {
    // Arrange
    SymOBORSAAuth symOBORSAAuth = new SymOBORSAAuth(new SymConfig());
    String sessionToken = "aaaaa";

    // Act
    symOBORSAAuth.setSessionToken(sessionToken);

    // Assert
    assertEquals("aaaaa", symOBORSAAuth.getSessionToken());
  }
}
