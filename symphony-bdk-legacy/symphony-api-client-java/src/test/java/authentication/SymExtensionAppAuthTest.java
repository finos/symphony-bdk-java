package authentication;

import it.commons.BotTest;
import model.AppAuthResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SymExtensionAppAuthTest extends BotTest {
    private SymExtensionAppAuth symExtensionAppAuth;

    @Before
    public void initAuth() {
        symExtensionAppAuth = new SymExtensionAppAuth(config);
    }

    @Test
    public void appAuthenticateSuccess() {
        stubGet(AuthEndpointConstants.POD_CERT_PATH,
                "{\"certificate\":\"-----BEGIN CERTIFICATE-----\\nMIIEQDCCAyigAwIBAgIVAKmSDvvk3rea1n\\n-----END CERTIFICATE-----\\n\"}");
        stubPost(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH,
                "{ \"appId\" : \"APP_ID\", \"appToken\" : \"APP_TOKEN\", \"symphonyToken\" : \"SYMPHONY_TOKEN\", \"expireAt\" : 1539636528288 }");
        AppAuthResponse response = symExtensionAppAuth.appAuthenticate();

        assertNotNull(response);
        assertEquals("SYMPHONY_TOKEN", response.getSymphonyToken());
    }

    @Test
    public void sessionAuthenticateSuccess() {
        stubGet(AuthEndpointConstants.POD_CERT_PATH,
                "{\"certificate\":\"-----BEGIN CERTIFICATE-----\\nMIIEQDCCAyigAwIBAgIVAKmSDvvk3rea1n\\n-----END CERTIFICATE-----\\n\"}");
        stubPost(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH,
                "{ \"appId\" : \"APP_ID\", \"appToken\" : \"APP_TOKEN\", \"symphonyToken\" : \"SYMPHONY_TOKEN\", \"expireAt\" : 1539636528288 }");
        AppAuthResponse response = symExtensionAppAuth.sessionAppAuthenticate("APP_TOKEN");
        assertNotNull(response);
        assertEquals("APP_TOKEN", response.getAppToken());
        assertEquals("SYMPHONY_TOKEN", response.getSymphonyToken());
    }

    @Test
    public void validateTokens() {
        stubGet(AuthEndpointConstants.POD_CERT_PATH,
                "{\"certificate\":\"-----BEGIN CERTIFICATE-----\\nMIIEQDCCAyigAwIBAgIVAKmSDvvk3rea1n\\n-----END CERTIFICATE-----\\n\"}");
        stubPost(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH,
                "{ \"appId\" : \"APP_ID\", \"appToken\" : \"APP_TOKEN\", \"symphonyToken\" : \"SYMPHONY_TOKEN\", \"expireAt\" : 1539636528288 }");
        symExtensionAppAuth.appAuthenticate();
        Boolean validated = symExtensionAppAuth.validateTokens("APP_TOKEN", "SYMPHONY_TOKEN");
        assertEquals(true, validated);
    }

    @Test
    public void formattedPodSessionAuthUrlTest() {
        String formattedUrl = symExtensionAppAuth.formattedPodSessionAuthUrl();
        assertEquals("https://localhost:7443", formattedUrl);
        String formattedUrlWithInput = symExtensionAppAuth.formattedPodSessionAuthUrl("localhost.symphony.com:8443");
        assertEquals("https://localhost.symphony.com:8443", formattedUrlWithInput);
    }

    @Test
    public void generateTokensTest() {
        String token = symExtensionAppAuth.generateToken();
        assertNotNull(token);
    }
}
