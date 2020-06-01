package it.authentication;

import authentication.AuthEndpointConstants;
import authentication.SymBotRSAAuth;
import exceptions.AuthenticationException;
import it.commons.ServerTest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static it.commons.BotTest.stubPost;
import static org.junit.Assert.*;

public class SymBotRSAAuthTest extends ServerTest {

    private final SymBotRSAAuth symBotRSAAuth = new SymBotRSAAuth(config);

    @Before
    public void setup() {
        // stub KM auth response
        stubPost(
            AuthEndpointConstants.KEY_AUTH_PATH_RSA,
            "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }"
        );
    }

    @Test
    public void should_authenticate_with_success() throws AuthenticationException {

        // session auth returns 200
        stubPost(
            AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
            "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }"
        );

        this.symBotRSAAuth.authenticate();
        assertNotNull(this.symBotRSAAuth.getSessionToken());
        assertEquals("eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ", this.symBotRSAAuth.getSessionToken());
        assertNotNull(this.symBotRSAAuth.getKmToken());
        assertEquals("0100e4feOiJSUzUxMiJ97oqGf729d1866f", this.symBotRSAAuth.getKmToken());
    }

    @Test
    public void should_fail_to_authenticate_session() {

        final String responsePayload = "{ \"error\": \"Service unavailable\" }";

        // session auth returns 503
        stubPost(
            AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
            responsePayload,
            503
        );

        try {
            this.symBotRSAAuth.authenticate();
        } catch (AuthenticationException ex) {
            assertEquals(responsePayload, ex.getMessage());
        }
    }
}
