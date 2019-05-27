package it.authentication;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.junit.Test;
import authentication.AuthEndpointConstants;
import authentication.SymOBORSAAuth;
import authentication.SymOBOUserRSAAuth;
import it.commons.ServerTest;

public class SymOBORSAAuthTest extends ServerTest {
  @Test
  public void authenticateSuccess() {
    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_APP_AUTH_PATH_RSA))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"name\": \"sessionToken\", \"token\": \"eyJhbGciOiJSUYhdXNlcjEiLCJ\" }")));


    SymOBORSAAuth symOBORSAAuth = new SymOBORSAAuth(config);
    symOBORSAAuth.authenticate();

    assertNotNull(symOBORSAAuth.getSessionToken());
    assertEquals("eyJhbGciOiJSUYhdXNlcjEiLCJ", symOBORSAAuth.getSessionToken());
  }

  @Test
  public void getUserAuthByUsernameSuccess() {
    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH_RSA.replace("{username}", config.getBotUsername())))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"name\": \"sessionToken\", \"token\": \"eyJhbGciOiJSUYhdXNlcjEiLCJ\" }")));


    SymOBORSAAuth symOBORSAAuth = new SymOBORSAAuth(config);
    SymOBOUserRSAAuth userAuth = symOBORSAAuth.getUserAuth(config.getBotUsername());

    assertNotNull(userAuth);
    assertEquals("eyJhbGciOiJSUYhdXNlcjEiLCJ", userAuth.getSessionToken());
  }

  @Test
  public void getUserAuthByUserIdSuccess() {
    stubFor(post(urlEqualTo(AuthEndpointConstants.OBO_USER_ID_AUTH_PATH_RSA.replace("{uid}", Long.toString(1L))))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"name\": \"sessionToken\", \"token\": \"eyJhbGciOiJSUYhdXNlcjEiLCJ\" }")));


    SymOBORSAAuth symOBORSAAuth = new SymOBORSAAuth(config);
    SymOBOUserRSAAuth userAuth = symOBORSAAuth.getUserAuth(1L);

    assertNotNull(userAuth);
    assertEquals("eyJhbGciOiJSUYhdXNlcjEiLCJ", userAuth.getSessionToken());
  }
}
