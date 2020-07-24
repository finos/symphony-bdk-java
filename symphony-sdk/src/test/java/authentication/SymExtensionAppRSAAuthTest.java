package authentication;

import it.commons.BotTest;
import model.AppAuthResponse;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SymExtensionAppRSAAuthTest extends BotTest {

  private SymExtensionAppRSAAuth symExtensionAppRSAAuth;

  @Before
  public void initAuth() {
    symExtensionAppRSAAuth = new SymExtensionAppRSAAuth(config);
  }

  @Test
  public void appAuthenticateSuccess() {
    stubFor(get(urlEqualTo(AuthEndpointConstants.POD_CERT_RSA_PATH))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\"certificate\":\"-----BEGIN CERTIFICATE-----\\nMIIEQDCCAyigAwIBAgIVAKmSDvvk3rea1n\\n-----END CERTIFICATE-----\\n\"}")));

    stubFor(post(urlEqualTo(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH_RSA))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"appId\" : \"APP_ID\", \"appToken\" : \"APP_TOKEN\", \"symphonyToken\" : \"SYMPHONY_TOKEN\", \"expireAt\" : 1539636528288 }")));
    AppAuthResponse response = symExtensionAppRSAAuth.appAuthenticate();

    assertNotNull(response);
    assertEquals("SYMPHONY_TOKEN", response.getSymphonyToken());
  }

  @Test
  public void sessionAuthenticateSuccess() {
    stubGet(AuthEndpointConstants.POD_CERT_RSA_PATH,
            "{\"certificate\":\"-----BEGIN CERTIFICATE-----\\nMIIEQDCCAyigAwIBAgIVAKmSDvvk3rea1n\\n-----END CERTIFICATE-----\\n\"}");
    stubPost(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH_RSA,
            "{ \"appId\" : \"APP_ID\", \"appToken\" : \"APP_TOKEN\", \"symphonyToken\" : \"SYMPHONY_TOKEN\", \"expireAt\" : 1539636528288 }");
    AppAuthResponse response = symExtensionAppRSAAuth.sessionAppAuthenticate("APP_TOKEN");
    assertNotNull(response);
    assertEquals("APP_TOKEN", response.getAppToken());
    assertEquals("SYMPHONY_TOKEN", response.getSymphonyToken());
  }

  @Test
  public void validateTokens() {
    stubGet(AuthEndpointConstants.POD_CERT_RSA_PATH,
            "{\"certificate\":\"-----BEGIN CERTIFICATE-----\\nMIIEQDCCAyigAwIBAgIVAKmSDvvk3rea1n\\n-----END CERTIFICATE-----\\n\"}");
    stubPost(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH_RSA,
            "{ \"appId\" : \"APP_ID\", \"appToken\" : \"APP_TOKEN\", \"symphonyToken\" : \"SYMPHONY_TOKEN\", \"expireAt\" : 1539636528288 }");
    symExtensionAppRSAAuth.appAuthenticate();
    Boolean validated = symExtensionAppRSAAuth.validateTokens("APP_TOKEN", "SYMPHONY_TOKEN");
    assertEquals(true, validated);
  }

}
