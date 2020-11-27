package authentication;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import exceptions.AuthenticationException;
import it.commons.ServerTest;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import javax.ws.rs.core.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SymBotAuthTest extends ServerTest {
  private SymBotAuth symBotAuth;
  private ClientConfig sessionClientConfig;
  private ClientConfig kmClientConfig;

  @Before
  public void init() {
    symBotAuth = spy(new SymBotAuth(config, 1, 5));
    sessionClientConfig = new ClientConfig();
    sessionClientConfig.property("sessionAuthHost", "localhost");
    sessionClientConfig.property("sessionAuthPort", 7443);
    kmClientConfig = new ClientConfig();
    kmClientConfig.property("keyAuthHost", "localhost");
    kmClientConfig.property("keyAuthPort", 7443);
  }

  @Test
  public void authenticateTest() throws AuthenticationException {
    doNothing().when(symBotAuth).sessionAuthenticate();
    doNothing().when(symBotAuth).kmAuthenticate();

    symBotAuth.authenticate();
    verify(symBotAuth, times(1)).authenticate();
    verify(symBotAuth, times(1)).sessionAuthenticate();
    verify(symBotAuth, times(1)).kmAuthenticate();
  }

  @Test
  public void authenticateTooFastTest() throws AuthenticationException {
    SymBotAuth symBotAuth2 = spy(new SymBotAuth(config, 5, 5));
    doNothing().when(symBotAuth2).sessionAuthenticate();
    doNothing().when(symBotAuth2).kmAuthenticate();

    symBotAuth2.authenticate();
    symBotAuth2.authenticate();
    verify(symBotAuth2, times(3)).authenticate();
    verify(symBotAuth2, times(2)).sessionAuthenticate();
    verify(symBotAuth2, times(2)).kmAuthenticate();
  }

  @Test
  public void sessionAuthenticateTest() throws AuthenticationException {
    symBotAuth = spy(new SymBotAuth(config, sessionClientConfig, kmClientConfig));
    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_AUTH_PATH))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\"name\": \"sessionToken\", \"token\": \"sessionTokenTest\"}")));

    symBotAuth.sessionAuthenticate();
    assertEquals(symBotAuth.getSessionToken(), "sessionTokenTest");
  }

  @Test
  public void sessionAuthenticateFailedTest() throws AuthenticationException {
    // First Try failed
    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_AUTH_PATH))
        .inScenario("Session authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400))
        .willSetStateTo("Failed with status 400"));
    // Second try success
    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_AUTH_PATH))
        .inScenario("Session authentication")
        .whenScenarioStateIs("Failed with status 400")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\"name\": \"sessionToken\", \"token\": \"sessionTokenTest\"}")));

    symBotAuth.sessionAuthenticate();

    ArgumentCaptor<Response> argument = ArgumentCaptor.forClass(Response.class);
    verify(2, postRequestedFor(urlEqualTo(AuthEndpointConstants.SESSION_AUTH_PATH)));
    verify(symBotAuth).handleError(argument.capture(), any());
    assertEquals(400, argument.getValue().getStatus());
    assertEquals("sessionTokenTest", symBotAuth.getSessionToken());
  }

  @Test
  public void kmAuthenticateTest() throws AuthenticationException {
    stubFor(post(urlEqualTo(AuthEndpointConstants.KEY_AUTH_PATH))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\"name\": \"keyManagerToken\", \"token\": \"kmTokenTest\"}")));

    symBotAuth.kmAuthenticate();
    assertEquals(symBotAuth.getKmToken(), "kmTokenTest");
  }

  @Test
  public void kmAuthenticateFailedTest() throws AuthenticationException {
    // First Try failed
    stubFor(post(urlEqualTo(AuthEndpointConstants.KEY_AUTH_PATH))
        .inScenario("Km authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400))
        .willSetStateTo("Failed with status 400"));
    // Second try success
    stubFor(post(urlEqualTo(AuthEndpointConstants.KEY_AUTH_PATH))
        .inScenario("Km authentication")
        .whenScenarioStateIs("Failed with status 400")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\"name\": \"keyManagerToken\", \"token\": \"kmTokenTest\"}")));

    symBotAuth.kmAuthenticate();

    ArgumentCaptor<Response> argument = ArgumentCaptor.forClass(Response.class);
    verify(2, postRequestedFor(urlEqualTo(AuthEndpointConstants.KEY_AUTH_PATH)));
    verify(symBotAuth).handleError(argument.capture(), any());
    assertEquals(400, argument.getValue().getStatus());
    assertEquals("kmTokenTest", symBotAuth.getKmToken());
  }

  @Test
  public void setSessionTokenTest() {
    String testSessionToken = "Test Session Token";
    symBotAuth.setSessionToken(testSessionToken);
    assertEquals(testSessionToken, symBotAuth.getSessionToken());
  }

  @Test
  public void setKmTokenTest() {
    String testKmToken = "Test KmToken";
    symBotAuth.setKmToken(testKmToken);
    assertEquals(testKmToken, symBotAuth.getKmToken());
  }

  @Test
  public void logoutTest() {
    String testSessionToken = "Test Session Token";
    stubFor(post(urlEqualTo(AuthEndpointConstants.LOGOUT_PATH))
        .withHeader("sessiontoken", equalTo(testSessionToken))
        .willReturn(aResponse()
          .withStatus(200)
          .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
          .withBody("{\"message\": \"Session logout was successful.\"}")));

    symBotAuth.setSessionToken(testSessionToken);
    symBotAuth.logout();

    verify(1, postRequestedFor(urlEqualTo(AuthEndpointConstants.LOGOUT_PATH)));
  }

  @Test
  public void logoutFailedTest() {
    String testSessionToken = "Bad Session Token";
    stubFor(post(urlEqualTo(AuthEndpointConstants.LOGOUT_PATH))
        .withHeader("sessiontoken", equalTo(testSessionToken))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    symBotAuth.setSessionToken(testSessionToken);
    symBotAuth.logout();

    ArgumentCaptor<Response> argument = ArgumentCaptor.forClass(Response.class);
    verify(1, postRequestedFor(urlEqualTo(AuthEndpointConstants.LOGOUT_PATH)));
    verify(symBotAuth).handleError(argument.capture(), any());
    assertEquals(400, argument.getValue().getStatus());
  }
}
