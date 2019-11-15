package authentication;

import static org.junit.Assert.assertEquals;
import authentication.SymBotRSAAuth;
import clients.SymOBOClient;
import configuration.SymConfig;
import exceptions.AuthenticationException;
import java.io.InputStream;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.reflect.Whitebox;

public class SymBotRSAAuthTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void SymBotRSAAuthTest() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();
    ClientConfig sessionAuthClientConfig = new ClientConfig();
    ClientConfig kmAuthClientConfig = new ClientConfig();

    // Act
    SymBotRSAAuth symBotRSAAuth = new SymBotRSAAuth(config, sessionAuthClientConfig, kmAuthClientConfig);

    // Assert
    String sessionToken = symBotRSAAuth.getSessionToken();
    assertEquals(null, sessionToken);
    assertEquals(null, symBotRSAAuth.getKmToken());
  }

  @Test
  public void SymBotRSAAuthTest2() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    SymBotRSAAuth symBotRSAAuth = new SymBotRSAAuth(symConfig);

    // Assert
    String sessionToken = symBotRSAAuth.getSessionToken();
    assertEquals(null, sessionToken);
    assertEquals(null, symBotRSAAuth.getKmToken());
    assertEquals(35000, symConfig.getConnectionTimeout());
  }

  @Test
  public void authenticateTest() throws Exception {
    // Arrange
    SymBotRSAAuth symBotRSAAuth = new SymBotRSAAuth(new SymConfig());

    // Act and Assert
    thrown.expect(NullPointerException.class);
    symBotRSAAuth.authenticate();
  }

  @Test
  public void getKmTokenTest() throws Exception {
    // Arrange
    SymBotRSAAuth symBotRSAAuth = new SymBotRSAAuth(new SymConfig());

    // Act
    String actual = symBotRSAAuth.getKmToken();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRSAPrivateKeyFileTest() throws Exception {
    // Arrange
    SymBotRSAAuth symBotRSAAuth = new SymBotRSAAuth(new SymConfig());
    SymConfig config = new SymConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    symBotRSAAuth.getRSAPrivateKeyFile(config);
  }

  @Test
  public void getSessionTokenTest() throws Exception {
    // Arrange
    SymBotRSAAuth symBotRSAAuth = new SymBotRSAAuth(new SymConfig());

    // Act
    String actual = symBotRSAAuth.getSessionToken();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void handleErrorTest() throws Exception {
    // Arrange
    SymBotRSAAuth symBotRSAAuth = new SymBotRSAAuth(new SymConfig());
    Response.StatusType statusType = Whitebox.newInstance(Response.StatusType.class);
    OutboundJaxrsResponse response = new OutboundJaxrsResponse(statusType, new OutboundMessageContext());
    SymConfig symConfig = new SymConfig();
    SymConfig symConfig1 = new SymConfig();
    symConfig1.setAgentHost("aaaaa");
    symConfig1.setAgentHost("aaaaa");
    SymOBOClient botClient = new SymOBOClient(symConfig, null);

    // Act and Assert
    thrown.expect(IllegalStateException.class);
    symBotRSAAuth.handleError(response, botClient);
  }

  @Test
  public void kmAuthenticateTest() throws Exception {
    // Arrange
    SymBotRSAAuth symBotRSAAuth = new SymBotRSAAuth(new SymConfig());

    // Act and Assert
    thrown.expect(AuthenticationException.class);
    symBotRSAAuth.kmAuthenticate();
  }

  @Test
  public void logoutTest() throws Exception {
    // Arrange
    SymBotRSAAuth symBotRSAAuth = new SymBotRSAAuth(new SymConfig());

    // Act and Assert
    thrown.expect(ProcessingException.class);
    symBotRSAAuth.logout();
  }

  @Test
  public void sessionAuthenticateTest() throws Exception {
    // Arrange
    SymBotRSAAuth symBotRSAAuth = new SymBotRSAAuth(new SymConfig());

    // Act and Assert
    thrown.expect(AuthenticationException.class);
    symBotRSAAuth.sessionAuthenticate();
  }

  @Test
  public void setKmTokenTest() throws Exception {
    // Arrange
    SymBotRSAAuth symBotRSAAuth = new SymBotRSAAuth(new SymConfig());
    String kmToken = "aaaaa";

    // Act
    symBotRSAAuth.setKmToken(kmToken);

    // Assert
    assertEquals("aaaaa", symBotRSAAuth.getKmToken());
  }

  @Test
  public void setSessionTokenTest() throws Exception {
    // Arrange
    SymBotRSAAuth symBotRSAAuth = new SymBotRSAAuth(new SymConfig());
    String sessionToken = "aaaaa";

    // Act
    symBotRSAAuth.setSessionToken(sessionToken);

    // Assert
    assertEquals("aaaaa", symBotRSAAuth.getSessionToken());
  }
}
