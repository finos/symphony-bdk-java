package authentication;

import clients.symphony.api.APIClient;
import configuration.SymConfig;
import exceptions.NoConfigException;
import model.Token;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpClientBuilderHelper;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public final class SymBotAuth extends APIClient implements ISymAuth {
    private final Logger logger = LoggerFactory.getLogger(SymBotAuth.class);
    private String sessionToken;
    private String kmToken;
    private SymConfig config;
    private Client sessionAuthClient;
    private Client kmAuthClient;
    private long lastAuthTime = 0;
    private int authRetries = 0;

    public SymBotAuth(SymConfig config) {
        this.config = config;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBotBuilder(config);
        Client client = clientBuilder.build();
        if (isEmpty(config.getProxyURL())) {
            this.sessionAuthClient = client;
            this.kmAuthClient = client;
        } else {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.property(ClientProperties.PROXY_URI, config.getProxyURL());
            if (!isEmpty(config.getProxyUsername()) && !isEmpty(config.getProxyPassword())) {
                clientConfig.property(ClientProperties.PROXY_USERNAME, config.getProxyUsername());
                clientConfig.property(ClientProperties.PROXY_PASSWORD, config.getProxyPassword());
            }
            this.sessionAuthClient = clientBuilder.withConfig(clientConfig).build();
            this.kmAuthClient = clientBuilder.withConfig(clientConfig).build();
        }

        if (!isEmpty(config.getKeyManagerProxyURL())) {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.property(ClientProperties.PROXY_URI, config.getKeyManagerProxyURL());
            if (config.getKeyManagerProxyUsername() != null && config.getKeyManagerProxyPassword() != null) {
                clientConfig.property(ClientProperties.PROXY_USERNAME, config.getKeyManagerProxyUsername());
                clientConfig.property(ClientProperties.PROXY_PASSWORD, config.getKeyManagerProxyPassword());
            }
            this.kmAuthClient = clientBuilder.withConfig(clientConfig).build();
        }
    }

    public SymBotAuth(final SymConfig inputConfig,
                      final ClientConfig sessionAuthClientConfig,
                      final ClientConfig kmAuthClientConfig) {
        logger.info("SymBotAuth with ClientConfig variables");
        this.config = inputConfig;
        ClientBuilder clientBuilder = HttpClientBuilderHelper
            .getHttpClientBotBuilder(config);
        if (sessionAuthClientConfig != null) {
            this.sessionAuthClient = clientBuilder
                .withConfig(sessionAuthClientConfig)
                .build();
        } else {
            this.sessionAuthClient = clientBuilder.build();
        }
        if (kmAuthClientConfig != null) {
            this.kmAuthClient = clientBuilder
                .withConfig(kmAuthClientConfig)
                .build();
        } else {
            this.kmAuthClient = clientBuilder.build();
        }
    }

    public void authenticate() {
        if (lastAuthTime == 0
            | System.currentTimeMillis() - lastAuthTime
            > AuthEndpointConstants.WAITIME) {
            sessionAuthenticate();
            kmAuthenticate();
            lastAuthTime = System.currentTimeMillis();
        } else {
            try {
                logger.info("Re-authenticated too fast. "
                    + "Wait 30 seconds to try again.");
                TimeUnit.SECONDS.sleep(AuthEndpointConstants.TIMEOUT);
                authenticate();
            } catch (InterruptedException e) {
                logger.error("Error with authentication", e);
            }
        }
    }

    public void sessionAuthenticate() {
        if (config != null) {
            logger.info("Session auth");
            Response response
                = sessionAuthClient.target(AuthEndpointConstants.HTTPSPREFIX
                + config.getSessionAuthHost()
                + ":" + config.getSessionAuthPort())
                .path(AuthEndpointConstants.SESSIONAUTHPATH)
                .request(MediaType.APPLICATION_JSON)
                .post(null);
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, null);
                } catch (Exception e) {
                    logger.error("Unexpected error, retry authentication in 30 seconds", e);
                }
                try {
                    TimeUnit.SECONDS.sleep(AuthEndpointConstants.TIMEOUT);
                } catch (InterruptedException e) {
                    logger.error("Error with session authentication", e);
                }
                if (authRetries++ > AuthEndpointConstants.MAX_AUTH_RETRY) {
                    logger.error("Max retries reached. Giving up on auth.");
                    return;
                }
                sessionAuthenticate();
            } else {
                Token sessionTokenResponseContent =
                    response.readEntity(Token.class);
                this.sessionToken = sessionTokenResponseContent.getToken();
            }
        } else {
            throw new NoConfigException(
                "Must provide a SymConfig object to authenticate");
        }
    }

    public void kmAuthenticate() {
        logger.info("KM auth");
        if (config != null) {
            Response response
                = kmAuthClient.target(AuthEndpointConstants.HTTPSPREFIX
                + config.getKeyAuthHost()
                + ":" + config.getKeyAuthPort())
                .path(AuthEndpointConstants.KEYAUTHPATH)
                .request(MediaType.APPLICATION_JSON)
                .post(null);
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, null);
                } catch (Exception e) {
                    logger.error("Unexpected error, retry authentication in 30 seconds", e);
                }
                try {
                    TimeUnit.SECONDS.sleep(AuthEndpointConstants.TIMEOUT);
                } catch (InterruptedException e) {
                    logger.error("Error with authentication", e);
                }
                if (authRetries++ > AuthEndpointConstants.MAX_AUTH_RETRY) {
                    logger.error("Max retries reached. Giving up on auth.");
                    return;
                }
                kmAuthenticate();
            } else {
                Token kmTokenResponseContent = response.readEntity(Token.class);
                this.kmToken = kmTokenResponseContent.getToken();
            }

        } else {
            throw new NoConfigException(
                "Must provide a SymConfig object to authenticate");
        }
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(final String sessionTokenInput) {
        this.sessionToken = sessionTokenInput;
    }

    public String getKmToken() {
        return kmToken;
    }

    public void setKmToken(final String kmTokenInput) {
        this.kmToken = kmTokenInput;
    }

    public void logout() {
        logger.info("Logging out");
        Response response = sessionAuthClient.target(
            AuthEndpointConstants.HTTPSPREFIX
                + config.getSessionAuthHost()
                + ":" + config.getSessionAuthPort())
            .path(AuthEndpointConstants.LOGOUTPATH)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", getSessionToken())
            .post(null);

        if (response.getStatusInfo().getFamily()
            != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, null);
            } catch (Exception e) {
                logger.error("Unexpected error, retry logout in 30 seconds", e);
            }
        }
    }
}
