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
import static org.apache.commons.lang3.StringUtils.isEmpty;

public final class SymOBOAuth extends APIClient {
    private final Logger logger = LoggerFactory
            .getLogger(SymOBOAuth.class);
    private String sessionToken;
    private SymConfig config;
    private Client sessionAuthClient;

    public SymOBOAuth(final SymConfig configuration) {
        logger.info("SymOBOAuth being constructed");
        this.config = configuration;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientAppBuilder(config);
        Client client = clientBuilder.build();
        if (isEmpty(config.getProxyURL())) {
            this.sessionAuthClient = client;
        } else {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.connectorProvider(new ApacheConnectorProvider());
            clientConfig.property(ClientProperties.PROXY_URI, config.getProxyURL());
            if (!isEmpty(config.getProxyUsername()) && !isEmpty(config.getProxyPassword())) {
                clientConfig.property(ClientProperties.PROXY_USERNAME, config.getProxyUsername());
                clientConfig.property(ClientProperties.PROXY_PASSWORD, config.getProxyPassword());
            }
            this.sessionAuthClient = clientBuilder.withConfig(clientConfig).build();
        }
    }

    public SymOBOAuth(final SymConfig configuration, final ClientConfig sessionAuthClientConfig) {
        logger.info("SymOBOAuth being constructed with ClientConfig variable");
        this.config = configuration;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientAppBuilder(config);
        if (sessionAuthClientConfig != null) {
            this.sessionAuthClient = clientBuilder.withConfig(sessionAuthClientConfig).build();
        } else {
            this.sessionAuthClient = clientBuilder.build();
        }
    }

    public SymOBOUserAuth getUserAuth(final String username) {
        SymOBOUserAuth userAuth = new SymOBOUserAuth(config,
                sessionAuthClient, username, this);
        userAuth.authenticate();
        return userAuth;
    }

    public SymOBOUserAuth getUserAuth(final Long uid) {
        SymOBOUserAuth userAuth = new SymOBOUserAuth(config, sessionAuthClient,
                uid, this);
        userAuth.authenticate();
        return userAuth;
    }

    public void sessionAppAuthenticate() {
        if (config != null) {
            logger.info("Session app auth");
            Response response
                    = sessionAuthClient.target(AuthEndpointConstants.HTTPSPREFIX
                    + config.getSessionAuthHost()
                    + ":" + config.getSessionAuthPort())
                    .path(AuthEndpointConstants.SESSIONAPPAUTH)
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
                    logger.error("Error with session app authentication", e);
                }
                sessionAppAuthenticate();
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

    public String getSessionToken() {
        return this.sessionToken;
    }

    public void setSessionToken(final String sessionToken) {
        this.sessionToken = sessionToken;
    }


}
