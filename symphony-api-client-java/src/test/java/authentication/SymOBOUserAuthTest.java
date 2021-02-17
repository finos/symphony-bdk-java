package authentication;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import it.commons.ServerTest;
import org.junit.*;
import utils.HttpClientBuilderHelper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SymOBOUserAuthTest extends ServerTest {
  private Client client;
  private ISymOBOAuth symOBOAuth;

  @Before
  public void init() {
    super.resetSymBot();
    ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientAppBuilder(config);
    client = clientBuilder.build();
    symOBOAuth = mock(SymOBOAuth.class);
    when(symOBOAuth.getSessionToken()).thenReturn("OBO_APP_SESSION_TOKEN")
        .thenReturn("OBO_APP_SESSION_TOKEN_2");
  }

  @Test
  public void sessionAuthenticateByUidTest() {
    stubFor(post(AuthEndpointConstants.OBO_USER_ID_AUTH_PATH.replace("{uid}", Long.toString(123456L)))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withHeader("sessionToken", equalTo("OBO_APP_SESSION_TOKEN"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"sessionToken\": \"eyJhbGciOiJSUYhdXNlcjEiLCJ\" }")));

    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(config, client, 123456L, symOBOAuth);
    symOBOUserAuth.authenticate();
    String token = symOBOUserAuth.getSessionToken();
    assertEquals("eyJhbGciOiJSUYhdXNlcjEiLCJ", token);
  }

  @Test
  public void sessionAuthenticateByUsernameTest() {
    stubFor(post(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test_user"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withHeader("sessionToken", equalTo("OBO_APP_SESSION_TOKEN"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"sessionToken\": \"eyJhbGciOiJSUYhdXNlcjEiLCJ\" }")));

    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(config, client, "test_user", symOBOAuth);
    symOBOUserAuth.authenticate();
    String token = symOBOUserAuth.getSessionToken();
    assertEquals("eyJhbGciOiJSUYhdXNlcjEiLCJ", token);
  }

  @Test
  public void sessionAuthenticateFailedTest() {
    doNothing().when(symOBOAuth).sessionAppAuthenticate();
    // First Try failed
    stubFor(post(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test_user"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withHeader("sessionToken", equalTo("OBO_APP_SESSION_TOKEN"))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}"))
        .willSetStateTo("Failed with status 400"));
    // Second try success
    stubFor(post(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test_user"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withHeader("sessionToken", equalTo("OBO_APP_SESSION_TOKEN_2"))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 400")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"sessionToken\": \"eyJhbGciOiJSUYhdXNlcjEiLCJ\" }")));

    SymOBOUserAuth symOBOUserAuth = spy(new SymOBOUserAuth(config, client, "test_user",
        symOBOAuth, 1, 5));
    symOBOUserAuth.authenticate();
    String token = symOBOUserAuth.getSessionToken();
    assertEquals("eyJhbGciOiJSUYhdXNlcjEiLCJ", token);
    verify(symOBOAuth, times(2)).getSessionToken();
    verify(symOBOAuth, times(1)).sessionAppAuthenticate();
    verify(symOBOUserAuth, times(2)).sessionAuthenticate();
  }

  @Test
  public void sessionAuthenticateMaxRetryTest() {
    doNothing().when(symOBOAuth).sessionAppAuthenticate();
    stubFor(post(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test_user"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withHeader("sessionToken", equalTo("OBO_APP_SESSION_TOKEN"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(config, client, "test_user",
        symOBOAuth, 1, -1);
    symOBOUserAuth.authenticate();
    String token = symOBOUserAuth.getSessionToken();
    assertNull(token);
    verify(symOBOAuth, times(1)).getSessionToken();
    verify(symOBOAuth, times(0)).sessionAppAuthenticate();
  }

  @Test
  public void setSessionTokenTest() {
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(config, client, "test_user", symOBOAuth);
    symOBOUserAuth.setSessionToken("test-token1");
    assertEquals("test-token1", symOBOUserAuth.getSessionToken());
    symOBOUserAuth.setSessionToken("test-token2");
    assertEquals("test-token2", symOBOUserAuth.getSessionToken());
  }

  @Test(expected = RuntimeException.class)
  public void kmAuthenticateTest() {
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(config, client, "test_user", symOBOAuth);
    symOBOUserAuth.kmAuthenticate();
  }

  @Test(expected = RuntimeException.class)
  public void getKmTokenTest() {
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(config, client, "test_user", symOBOAuth);
    symOBOUserAuth.getKmToken();
  }

  @Test(expected = RuntimeException.class)
  public void setKmTokenTest() {
    SymOBOUserAuth symOBOUserAuth = new SymOBOUserAuth(config, client, "test_user", symOBOAuth);
    symOBOUserAuth.setKmToken("token");
  }
}
