package it.authentication;

import authentication.AuthEndpointConstants;
import authentication.SymExtensionAppRSAAuth;
import it.commons.ServerTest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import model.AppAuthResponse;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SymExtensionAppRSAAuthTest extends ServerTest {
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

    SymExtensionAppRSAAuth symExtensionAppRSAAuth = new SymExtensionAppRSAAuth(config);
    AppAuthResponse response = symExtensionAppRSAAuth.appAuthenticate();

    assertNotNull(response);
    assertEquals("SYMPHONY_TOKEN", response.getSymphonyToken());
  }

}
