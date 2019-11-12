package authentication;

import clients.symphony.api.APIClient;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import exceptions.NoConfigException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.AppAuthResponse;
import model.PodCert;
import model.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpClientBuilderHelper;
import static utils.JwtHelper.validateJwt;

public final class SymExtensionAppAuth extends APIClient {
    private final Logger logger = LoggerFactory.getLogger(SymExtensionAppAuth.class);
    private SymConfig config;
    private Client sessionAuthClient;
    private int authRetries = 0;

    /**
     * Create an instance initialized with provided Symphony configuration.
     *
     * @param config the Symphony configuration
     */
    public SymExtensionAppAuth(SymConfig config) {
        this.config = config;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientAppBuilder(config);
        this.sessionAuthClient = clientBuilder.build();

        ClientConfig clientConfig = HttpClientBuilderHelper.getPodClientConfig(config);
        if (clientConfig != null) {
            this.sessionAuthClient = clientBuilder.withConfig(clientConfig).build();
        }
    }

    /**
     * Create an instance initialized with provided Symphony and session authentication client configuration.
     *
     * @param config                  the Symphony configuration
     * @param sessionAuthClientConfig the session authentication client configuration
     */
    public SymExtensionAppAuth(SymConfig config, ClientConfig sessionAuthClientConfig) {
        this.config = config;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientAppBuilder(config);

        if (sessionAuthClientConfig != null) {
            this.sessionAuthClient = clientBuilder.withConfig(sessionAuthClientConfig).build();
        } else {
            this.sessionAuthClient = clientBuilder.build();
        }
    }

    public AppAuthResponse sessionAppAuthenticate(String appToken) {
        if (config == null) {
            throw new NoConfigException("Must provide a SymConfig object to authenticate");
        }

        logger.info("Session extension app auth");
        Map<String, String> input = new HashMap<>();
        input.put("appToken", appToken);

        String urlTarget = CommonConstants.HTTPS_PREFIX + config.getSessionAuthHost();
        if (config.getSessionAuthPort() != 443) {
            urlTarget += ":" + config.getSessionAuthPort();
        }

        Invocation.Builder builder = sessionAuthClient.target(urlTarget)
            .path(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH)
            .request(MediaType.APPLICATION_JSON);

        try (Response response = builder.post(Entity.entity(input, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return response.readEntity(AppAuthResponse.class);
            } else {
                return handleSessionAppAuthFailure(response, appToken, null);
            }
        }
    }

    public AppAuthResponse sessionAppAuthenticate(String appToken, String podSessionAuthUrl) {
        if (podSessionAuthUrl == null) {
            return sessionAppAuthenticate(appToken);
        }

        logger.info("Session extension app auth");
        Map<String, String> input = new HashMap<>();
        input.put("appToken", appToken);

        String target = CommonConstants.HTTPS_PREFIX + podSessionAuthUrl;
        Invocation.Builder builder = sessionAuthClient.target(target)
            .path(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH)
            .request(MediaType.APPLICATION_JSON);
        try (Response response = builder.post(Entity.entity(input, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return response.readEntity(AppAuthResponse.class);
            } else {
                return handleSessionAppAuthFailure(response, appToken, podSessionAuthUrl);
            }
        }
    }

    private AppAuthResponse handleSessionAppAuthFailure(Response response, String appToken, String podSessionAuthUrl) {
        try {
            handleError(response, null);
        } catch (Exception e) {
            logger.error("Unexpected error, retry authentication in {} seconds", AuthEndpointConstants.TIMEOUT);
        }
        try {
            TimeUnit.SECONDS.sleep(AuthEndpointConstants.TIMEOUT);
        } catch (InterruptedException e) {
            logger.error("Error with authentication", e);
        }
        if (authRetries++ > AuthEndpointConstants.MAX_AUTH_RETRY) {
            logger.error("Max retries reached. Giving up on auth.");
            return null;
        }
        return sessionAppAuthenticate(appToken, podSessionAuthUrl);
    }

    public UserInfo verifyJWT(final String jwt, final String podSessionAuthUrl) {
        String authUrl = podSessionAuthUrl;
        if (StringUtils.isBlank(authUrl)) {
            authUrl = config.getSessionAuthHost() + ":" + config.getSessionAuthPort();
        }
        Response response
            = sessionAuthClient.target(CommonConstants.HTTPS_PREFIX + authUrl)
            .path(AuthEndpointConstants.POD_CERT_PATH)
            .request(MediaType.APPLICATION_JSON)
            .get();
        if (response.getStatusInfo().getFamily()
            != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, null);
            } catch (Exception e) {
                logger.error("Unexpected error, retry authentication in 30 seconds");
            }
            try {
                TimeUnit.SECONDS.sleep(AuthEndpointConstants.TIMEOUT);
            } catch (InterruptedException e) {
                logger.error("Error with verify", e);
            }
            verifyJWT(jwt, podSessionAuthUrl);
        } else {
            return validateJwt(jwt, response.readEntity(PodCert.class).getCertificate());
        }
        return null;
    }
}
