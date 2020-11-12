package authentication;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static it.commons.BotTest.stubPost;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import it.commons.ServerTest;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class SymOBOAuthTest extends ServerTest {

  private final int timeout = 1;
  private final int maxRetry = 0;

  // getUserAuthByUsername
  @Test
  public void testGetUserAuthByUsernameSuccess(){
    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth symOBOUserAuth = symOBOAuth.getUserAuth("test1");
    assertNotNull(symOBOUserAuth);

    assertEquals("", symOBOUserAuth.getSessionToken());
  }

  @Test
  public void testGetUserAuthFailure400() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400)
            .withBody("{}"))
        .willSetStateTo("Failed with status 400"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 400")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"9887676\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 400")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth userAuth = symOBOAuth.getUserAuth("test1");
    assertEquals("9887676", userAuth.getSessionToken());
  }

  @Test
  public void testGetUserAuthFailure401() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(401)
            .withBody("{}"))
        .willSetStateTo("Failed with status 401"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 401")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"9887676\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 401")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth userAuth = symOBOAuth.getUserAuth("test1");
    assertEquals("9887676", userAuth.getSessionToken());
  }

  @Test
  public void testGetUserAuthFailure403() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(403)
            .withBody("{}"))
        .willSetStateTo("Failed with status 403"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 403")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"9887676\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 403")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth userAuth = symOBOAuth.getUserAuth("test1");
    assertEquals("9887676", userAuth.getSessionToken());
  }

  @Test
  public void testGetUserAuthFailure500() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(500)
            .withBody("{}"))
        .willSetStateTo("Failed with status 500"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 500")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"9887676\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 500")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth userAuth = symOBOAuth.getUserAuth("test1");
    assertEquals("9887676", userAuth.getSessionToken());
  }
  // End getUserAuthByUsername

  // getUserAuthById
  @Test
  public void testGetUserAuthByIdSuccess(){
    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_ID_AUTH_PATH.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth symOBOUserAuth = symOBOAuth.getUserAuth(1L);
    assertNotNull(symOBOUserAuth);

    assertEquals("", symOBOUserAuth.getSessionToken());
  }

  @Test
  public void testGetUserAuthByIdFailure400() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_ID_AUTH_PATH.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400)
            .withBody("{}"))
        .willSetStateTo("Failed with status 400"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_ID_AUTH_PATH.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 400")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 400")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth userAuth = symOBOAuth.getUserAuth(1L);
    assertEquals("", userAuth.getSessionToken());
  }

  @Test
  public void testGetUserAuthByIdFailure401() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_ID_AUTH_PATH.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(401)
            .withBody("{}"))
        .willSetStateTo("Failed with status 401"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_ID_AUTH_PATH.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 401")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 401")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth userAuth = symOBOAuth.getUserAuth(1L);
    assertEquals("", userAuth.getSessionToken());
  }

  @Test
  public void testGetUserAuthByIdFailure403() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_ID_AUTH_PATH.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(403)
            .withBody("{}"))
        .willSetStateTo("Failed with status 403"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_ID_AUTH_PATH.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 403")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 403")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth userAuth = symOBOAuth.getUserAuth(1L);
    assertEquals("", userAuth.getSessionToken());
  }

  @Test
  public void testGetUserAuthByIdFailure500() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_ID_AUTH_PATH.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(500)
            .withBody("{}"))
        .willSetStateTo("Failed with status 500"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_ID_AUTH_PATH.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 500")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 500")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth userAuth = symOBOAuth.getUserAuth(1L);
    assertEquals("", userAuth.getSessionToken());
  }
  // End getUserAuthById

  // sessionAppAuthenticate
  @Test
  public void testSessionAppAuthenticateSuccess(){
    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"sessionToken\","
                + "\"token\": \"SESSION_TOKEN\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    symOBOAuth.sessionAppAuthenticate();
    assertTrue(true);
  }

  @Test
  public void testSessionAppAuthenticateFailure400() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400)
            .withBody("{}"))
        .willSetStateTo("Failed with status 400"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 400")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"sessionToken\""
                + "\"token\": \"SESSION_TOKEN\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 400")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    symOBOAuth.sessionAppAuthenticate();
    assertTrue(true);
  }

  @Test
  public void testSessionAppAuthenticateFailure401() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(401)
            .withBody("{}"))
        .willSetStateTo("Failed with status 401"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 401")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"sessionToken\""
                + "\"token\": \"SESSION_TOKEN\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 401")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    symOBOAuth.sessionAppAuthenticate();
    assertTrue(true);
  }

  @Test
  public void testSessionAppAuthenticateFailure403() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(403)
            .withBody("{}"))
        .willSetStateTo("Failed with status 403"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 403")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"sessionToken\""
                + "\"token\": \"SESSION_TOKEN\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 403")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    symOBOAuth.sessionAppAuthenticate();
    assertTrue(true);
  }

  @Test
  public void testSessionAppAuthenticateFailure500() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(500)
            .withBody("{}"))
        .willSetStateTo("Failed with status 500"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 500")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"sessionToken\""
                + "\"token\": \"SESSION_TOKEN\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 500")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    symOBOAuth.sessionAppAuthenticate();
    assertTrue(true);
  }
  // End sessionAppAuthenticate

  // getSessionToken
  @Test
  public void testGetSessionTokenSuccess(){
    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"sessTok\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth symOBOUserAuth = symOBOAuth.getUserAuth("test1");
    assertEquals("sessTok", symOBOUserAuth.getSessionToken());
  }

  @Test
  public void testGetSessionTokenFailure400() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400)
            .withBody("{}"))
        .willSetStateTo("Failed with status 400"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 400")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"sessTok\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 400")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth symOBOUserAuth = symOBOAuth.getUserAuth("test1");
    assertEquals("sessTok", symOBOUserAuth.getSessionToken());
  }

  @Test
  public void testGetSessionTokenFailure401() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(401)
            .withBody("{}"))
        .willSetStateTo("Failed with status 401"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 401")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"sessTok\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 401")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth symOBOUserAuth = symOBOAuth.getUserAuth("test1");
    assertEquals("sessTok", symOBOUserAuth.getSessionToken());
  }

  @Test
  public void testGetSessionTokenFailure403() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(403)
            .withBody("{}"))
        .willSetStateTo("Failed with status 403"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 403")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"sessTok\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 403")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth symOBOUserAuth = symOBOAuth.getUserAuth("test1");
    assertEquals("sessTok", symOBOUserAuth.getSessionToken());
  }

  @Test
  public void testGetSessionTokenFailure500() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(500)
            .withBody("{}"))
        .willSetStateTo("Failed with status 500"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 500")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"sessTok\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 500")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth symOBOUserAuth = symOBOAuth.getUserAuth("test1");
    assertEquals("sessTok", symOBOUserAuth.getSessionToken());
  }
  // End getSessionToken

  // setSessionToken
  @Test
  public void testSetSessionTokenSuccess(){
    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"sessTok\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth symOBOUserAuth = symOBOAuth.getUserAuth("test1");
    assertNotNull(symOBOUserAuth);

    symOBOUserAuth.setSessionToken("sessTok1");
    assertEquals("sessTok1", symOBOUserAuth.getSessionToken());
  }

  @Test
  public void testSetSessionTokenFailure400() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400)
            .withBody("{}"))
        .willSetStateTo("Failed with status 400"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 400")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"sessTok\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 400")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth symOBOUserAuth = symOBOAuth.getUserAuth("test1");
    assertNotNull(symOBOUserAuth);

    symOBOUserAuth.setSessionToken("sessTok1");
    assertEquals("sessTok1", symOBOUserAuth.getSessionToken());
  }

  @Test
  public void testSetSessionTokenFailure401() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(401)
            .withBody("{}"))
        .willSetStateTo("Failed with status 401"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 401")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"sessTok\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 401")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth symOBOUserAuth = symOBOAuth.getUserAuth("test1");
    assertNotNull(symOBOUserAuth);

    symOBOUserAuth.setSessionToken("sessTok1");
    assertEquals("sessTok1", symOBOUserAuth.getSessionToken());
  }

  @Test
  public void testSetSessionTokenFailure403() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(403)
            .withBody("{}"))
        .willSetStateTo("Failed with status 403"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 403")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"sessTok\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 403")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth symOBOUserAuth = symOBOAuth.getUserAuth("test1");
    assertNotNull(symOBOUserAuth);

    symOBOUserAuth.setSessionToken("sessTok1");
    assertEquals("sessTok1", symOBOUserAuth.getSessionToken());
  }

  @Test
  public void testSetSessionTokenFailure500() {
    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(500)
            .withBody("{}"))
        .willSetStateTo("Failed with status 500"));

    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", "test1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 500")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"sessionToken\": \"sessTok\""
                + "}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get user authentication")
        .whenScenarioStateIs("Failed with status 500")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"name\": \"tokenName\","
                + "\"token\": \"123456\""
                + "}")));

    final SymOBOAuth symOBOAuth = new SymOBOAuth(config, this.timeout, this.maxRetry);
    final SymOBOUserAuth symOBOUserAuth = symOBOAuth.getUserAuth("test1");
    assertNotNull(symOBOUserAuth);

    symOBOUserAuth.setSessionToken("sessTok1");
    assertEquals("sessTok1", symOBOUserAuth.getSessionToken());
  }
  // End setSessionToken
}
