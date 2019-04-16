package authentication;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static utils.JwtHelper.validateJwt;

import clients.symphony.api.APIClient;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import exceptions.NoConfigException;
import model.AppAuthResponse;
import model.PodCert;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpClientBuilderHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public final class SymExtensionAppAuth extends APIClient {
    private final Logger logger = LoggerFactory
            .getLogger(SymExtensionAppAuth.class);
    private SymConfig config;
    private Client sessionAuthClient;
    private int authRetries = 0;

    public SymExtensionAppAuth(final SymConfig configuration) {
        this.config = configuration;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientAppBuilder(config);
        Client client = clientBuilder.build();

        if (isEmpty(config.getProxyURL())) {
            this.sessionAuthClient = client;
        } else {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.property(ClientProperties.PROXY_URI, config.getProxyURL());
            if (!isEmpty(config.getProxyUsername()) && !isEmpty(config.getProxyPassword())) {
                clientConfig.property(ClientProperties.PROXY_USERNAME, config.getProxyUsername());
                clientConfig.property(ClientProperties.PROXY_PASSWORD, config.getProxyPassword());
            }
            this.sessionAuthClient = clientBuilder.withConfig(clientConfig).build();
        }
    }

    public SymExtensionAppAuth(final SymConfig configuration,
                               final ClientConfig sessionAuthClientConfig) {
        this.config = configuration;
        ClientBuilder clientBuilder = HttpClientBuilderHelper
                .getHttpClientAppBuilder(config);
        if (sessionAuthClientConfig  !=  null) {
            this.sessionAuthClient = clientBuilder
                    .withConfig(sessionAuthClientConfig).build();
        } else {
            this.sessionAuthClient = clientBuilder.build();
        }
    }

    public AppAuthResponse sessionAppAuthenticate(final String appToken) {
        if (config != null) {
            logger.info("Session extension app auth");
            Map<String, String> input = new HashMap<>();
            input.put("appToken", appToken);
            Response response
                = sessionAuthClient.target(CommonConstants.HTTPS_PREFIX
                    + config.getSessionAuthHost() + ":"
                    + config.getSessionAuthPort())
                .path(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(input, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily()
                     !=  Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, null);
                } catch (Exception e) {
                    logger.error("Unexpected error, "
                            + "retry authentication in 30 seconds");
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
                sessionAppAuthenticate(appToken);
            } else {
                AppAuthResponse appAuthResponse =
                        response.readEntity(AppAuthResponse.class);
                return appAuthResponse;
            }
        } else {
            throw new NoConfigException(
                    "Must provide a SymConfig object to authenticate");

        }
        return null;
    }

    public AppAuthResponse sessionAppAuthenticate(final String appToken,
                                                  final String podSessionAuthUrl) {
        logger.info("Session extension app auth");
        Map<String, String> input = new HashMap<>();
        input.put("appToken", appToken);
        Response response
            = sessionAuthClient.target(CommonConstants.HTTPS_PREFIX
                + podSessionAuthUrl)
            .path(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(input, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily()
                 !=  Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, null);
            } catch (Exception e) {
                logger.error("Unexpected error, "
                        + "retry authentication in 30 seconds");
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
            sessionAppAuthenticate(appToken, podSessionAuthUrl);
        } else {
            return response.readEntity(AppAuthResponse.class);
        }
        return null;
    }

    public Object verifyJWT(final String jwt, final String podSessionAuthUrl) {
        String authUrl = podSessionAuthUrl;
        if(StringUtils.isBlank(authUrl)) {
            authUrl = config.getSessionAuthHost() + ":" + config.getSessionAuthPort();
        }
        Response response
            = sessionAuthClient.target(CommonConstants.HTTPS_PREFIX + authUrl)
            .path(AuthEndpointConstants.POD_CERT_PATH)
                .request(MediaType.APPLICATION_JSON)
                .get();
        if (response.getStatusInfo().getFamily()
                !=  Response.Status.Family.SUCCESSFUL) {
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
