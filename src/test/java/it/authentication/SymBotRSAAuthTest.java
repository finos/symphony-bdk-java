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
import authentication.ISymAuth;
import authentication.SymBotRSAAuth;
import it.commons.ServerTest;

public class SymBotRSAAuthTest extends ServerTest {
  @Test
  public void authenticateSuccess() {
    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_AUTH_PATH_RSA))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.KEY_AUTH_PATH_RSA))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"keyManagerToken\" }")));

    ISymAuth symBotRSAAuth = new SymBotRSAAuth(config);
    symBotRSAAuth.authenticate();

    assertNotNull(symBotRSAAuth.getSessionToken());
    assertEquals("eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ", symBotRSAAuth.getSessionToken());
    assertNotNull(symBotRSAAuth.getKmToken());
    assertEquals("0100e4feOiJSUzUxMiJ97oqGf729d1866f", symBotRSAAuth.getKmToken());
  }
}
