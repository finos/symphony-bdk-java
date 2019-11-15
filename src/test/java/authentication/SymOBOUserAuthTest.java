package authentication;

import static org.junit.Assert.assertEquals;
import authentication.SymOBOAuth;
import authentication.SymOBOUserAuth;
import configuration.SymConfig;
import org.glassfish.jersey.client.JerseyClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SymOBOUserAuthTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void SymOBOUserAuthTest() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();
    JerseyClient sessionAuthClient = null;
    String username = "aaaaa";
    SymOBOAuth appAuth = null;

    // Act
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(config, sessionAuthClient, username, appAuth);

    // Assert
    assertEquals(null, symOBOUserAuth.getSessionToken());
  }

  @Test
  public void SymOBOUserAuthTest2() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();
    JerseyClient sessionAuthClient = null;
    Long uid = new Long(1L);
    SymOBOAuth appAuth = null;

    // Act
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(config, sessionAuthClient, uid, appAuth);

    // Assert
    assertEquals(null, symOBOUserAuth.getSessionToken());
  }

  @Test
  public void authenticateTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(symConfig, null, new Long(1L), null);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    symOBOUserAuth.authenticate();
  }

  @Test
  public void getKmTokenTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(symConfig, null, new Long(1L), null);

    // Act and Assert
    thrown.expect(RuntimeException.class);
    symOBOUserAuth.getKmToken();
  }

  @Test
  public void getSessionTokenTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(symConfig, null, new Long(1L), null);

    // Act
    String actual = symOBOUserAuth.getSessionToken();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void kmAuthenticateTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(symConfig, null, new Long(1L), null);

    // Act and Assert
    thrown.expect(RuntimeException.class);
    symOBOUserAuth.kmAuthenticate();
  }

  @Test
  public void logoutTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(symConfig, null, new Long(1L), null);

    // Act
    symOBOUserAuth.logout();

    // Assert
    assertEquals(null, symOBOUserAuth.getSessionToken());
  }

  @Test
  public void sessionAuthenticateTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(symConfig, null, new Long(1L), null);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    symOBOUserAuth.sessionAuthenticate();
  }

  @Test
  public void setKmTokenTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(symConfig, null, new Long(1L), null);
    String kmToken = "aaaak";

    // Act and Assert
    thrown.expect(RuntimeException.class);
    symOBOUserAuth.setKmToken(kmToken);
  }

  @Test
  public void setSessionTokenTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(symConfig, null, new Long(1L), null);
    String sessionToken = "aaaak";

    // Act
    symOBOUserAuth.setSessionToken(sessionToken);

    // Assert
    assertEquals("aaaak", symOBOUserAuth.getSessionToken());
  }
}
