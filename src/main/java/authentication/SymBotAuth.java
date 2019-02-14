package authentication;

import clients.symphony.api.APIClient;
import configuration.SymConfig;
import exceptions.NoConfigException;
import model.Token;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
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

public final class SymBotAuth extends APIClient implements ISymAuth {
    private final Logger logger = LoggerFactory.getLogger(SymBotAuth.class);
    private String sessionToken;
    private String kmToken;
    private SymConfig config;
    private Client sessionAuthClient;
    private Client kmAuthClient;
    private long lastAuthTime = 0;

    public SymBotAuth(final SymConfig inputConfig) {
        this.config = inputConfig;
        ClientBuilder clientBuilder =
                HttpClientBuilderHelper.getHttpClientBotBuilder(config);
        Client client = clientBuilder.build();
        if (config.getProxyURL() == null || config.getProxyURL().equals("")) {
            this.sessionAuthClient = client;
        } else {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.connectorProvider(new ApacheConnectorProvider());
            clientConfig
                    .property(ClientProperties.PROXY_URI, config.getProxyURL());
            if (config.getProxyUsername() != null
                    && config.getProxyPassword() != null) {
                clientConfig.property(ClientProperties.PROXY_USERNAME,
                        config.getProxyUsername());
                clientConfig.property(ClientProperties.PROXY_PASSWORD,
                        config.getProxyPassword());
            }
            Client proxyClient = clientBuilder.withConfig(clientConfig)
                    .build();
            this.sessionAuthClient = proxyClient;
        }
        if (config.getKeyManagerProxyURL() == null || config.getKeyManagerProxyURL().equals("")) {
            this.kmAuthClient = client;
        } else {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.connectorProvider(new ApacheConnectorProvider());
            clientConfig
                    .property(ClientProperties.PROXY_URI, config.getKeyManagerProxyURL());
            if (config.getKeyManagerProxyUsername() != null
                    && config.getKeyManagerProxyPassword() != null) {
                clientConfig.property(ClientProperties.PROXY_USERNAME,
                        config.getKeyManagerProxyUsername());
                clientConfig.property(ClientProperties.PROXY_PASSWORD,
                        config.getKeyManagerProxyPassword());
            }
            Client proxyClient = clientBuilder.withConfig(clientConfig)
                    .build();
            this.kmAuthClient = proxyClient;
        }
    }

    public SymBotAuth(final SymConfig inputConfig,
                      final ClientConfig sessionAuthClientConfig,
                      final ClientConfig kmAuthClientConfig) {
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
        if (kmAuthClient == null) {
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
                e.printStackTrace();
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
                    logger.error("Unexpected error, "
                            + "retry authentication in 30 seconds");
                }
                try {
                    TimeUnit.SECONDS.sleep(AuthEndpointConstants.TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                    logger.error("Unexpected error, "
                            + "retry authentication in 30 seconds");
                }
                try {
                    TimeUnit.SECONDS.sleep(AuthEndpointConstants.TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                kmAuthenticate();
            } else {
                Token kmTokenResponseContent = response.readEntity(Token.class);
                this.kmToken = kmTokenResponseContent.getToken();
            }

        } else {
            try {
                throw new NoConfigException(
                        "Must provide a SymConfig object to authenticate");
            } catch (NoConfigException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
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
        Response response = sessionAuthClient.target(
                AuthEndpointConstants.HTTPSPREFIX
                + config.getSessionAuthHost()
                + ":" + config.getSessionAuthPort())
                .path(AuthEndpointConstants.LOGOUTPATH)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken", getSessionToken())
                .post(null);
    }
}
