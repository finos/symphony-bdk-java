package authentication;

import static org.apache.commons.lang3.StringUtils.isEmpty;

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

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public final class SymOBOAuth extends APIClient implements ISymOBOAuth {
    private final Logger logger = LoggerFactory
            .getLogger(SymOBOAuth.class);
    private String sessionToken;
    private SymConfig config;
    private Client sessionAuthClient;
    private int authRetries = 0;

    // For the tests
    private int timeout;
    private int maxRetry;

    public SymOBOAuth(final SymConfig configuration) {
      initConfig(configuration);
      this.timeout = AuthEndpointConstants.TIMEOUT;
      this.maxRetry = AuthEndpointConstants.MAX_AUTH_RETRY;
    }

    public SymOBOAuth(final SymConfig configuration, final ClientConfig sessionAuthClientConfig, int timeout, int maxRetry) {
      initConfig(configuration, sessionAuthClientConfig);
      this.timeout = timeout;
      this.maxRetry = maxRetry;
    }

    // For the tests
    public SymOBOAuth(final SymConfig configuration, final ClientConfig sessionAuthClientConfig) {
      initConfig(configuration, sessionAuthClientConfig);
      this.timeout = AuthEndpointConstants.TIMEOUT;
      this.maxRetry = AuthEndpointConstants.MAX_AUTH_RETRY;
    }

    // For the tests
    public SymOBOAuth(final SymConfig configuration, int timeout, int maxRetry) {
      initConfig(configuration);
      this.timeout = timeout;
      this.maxRetry = maxRetry;
    }

  private void initConfig(SymConfig configuration) {
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

  private void initConfig(SymConfig configuration, ClientConfig sessionAuthClientConfig) {
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
    SymOBOUserAuth userAuth = new SymOBOUserAuth(config, sessionAuthClient, username, this, timeout);
    userAuth.authenticate();
    return userAuth;
  }

  public SymOBOUserAuth getUserAuth(final Long uid) {
    SymOBOUserAuth userAuth = new SymOBOUserAuth(config, sessionAuthClient, uid, this, timeout);
    userAuth.authenticate();
    return userAuth;
  }

    public void sessionAppAuthenticate() {
        if (config != null) {
            logger.info("Session app auth");
            Response response
                = sessionAuthClient.target(this.config.getSessionAuthUrl())
                .path(AuthEndpointConstants.SESSION_APP_AUTH_PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(null);
            if (response.getStatusInfo().getFamily()
                    != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, null);
                } catch (Exception e) {
                  logger.error("Unexpected error, retry authentication in {} seconds", this.timeout);
                }
                try {
                    TimeUnit.SECONDS.sleep(this.timeout);
                } catch (InterruptedException e) {
                    logger.error("Error with session app authentication", e);
                }
                if (authRetries++ > this.maxRetry) {
                    logger.error("Max retries reached. Giving up on auth.");
                    return;
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
