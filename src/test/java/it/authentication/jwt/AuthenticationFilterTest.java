package it.authentication.jwt;

import authentication.AuthEndpointConstants;
import authentication.SymExtensionAppRSAAuth;
import authentication.jwt.AuthenticationFilter;
import it.commons.ServerTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.PrintWriter;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationFilterTest extends ServerTest {

    private static AuthenticationFilter authFilter;
    private static final String MISSING_JWT_MESSAGE = "Missing JWT";
    private static final String UNAUTHORIZED_JWT_MESSAGE = "Unauthorized JWT";

    @Mock
    private static HttpServletRequest mRequest;
    @Mock
    private static HttpServletResponse mResponse;
    @Mock
    private static FilterChain mFilterChain;
    @Mock
    private static PrintWriter mWriter;

    @Before
    public void setup() {
        config.setAuthenticationFilterUrlPattern("");
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

        authFilter = new AuthenticationFilter(new SymExtensionAppRSAAuth(config), config);
    }

    @Test
    public void missingAuthorizationTest() throws IOException, ServletException {
        when(mRequest.getServletPath()).thenReturn("");
        when(mResponse.getWriter()).thenReturn(mWriter);
        authFilter.doFilter(mRequest, mResponse, mFilterChain);
        verify(mResponse).setStatus(401);
        verify(mWriter).write(MISSING_JWT_MESSAGE);
    }

    @Test
    public void tooShortTokenTest() throws IOException, ServletException {
        when(mRequest.getServletPath()).thenReturn("");
        when(mRequest.getHeader("Authorization")).thenReturn("short");
        when(mResponse.getWriter()).thenReturn(mWriter);
        authFilter.doFilter(mRequest, mResponse, mFilterChain);
        verify(mResponse).setStatus(401);
        verify(mWriter).write(MISSING_JWT_MESSAGE);
    }

    @Test
    public void unAuthorizedJwtTest() throws IOException, ServletException {
        when(mRequest.getServletPath()).thenReturn("");
        when(mRequest.getHeader("Authorization")).thenReturn("unauthorized-token");
        when(mResponse.getWriter()).thenReturn(mWriter);
        authFilter.doFilter(mRequest, mResponse, mFilterChain);
        verify(mResponse).setStatus(401);
        verify(mWriter).write(UNAUTHORIZED_JWT_MESSAGE);
    }
}
