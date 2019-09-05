package authentication;

import clients.symphony.api.APIClient;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import exceptions.NoConfigException;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Token;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpClientBuilderHelper;
import utils.JwtHelper;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public final class SymOBORSAAuth extends APIClient implements ISymOBOAuth {
    private final Logger logger = LoggerFactory.getLogger(SymOBORSAAuth.class);
    private String sessionToken;
    private SymConfig config;
    private Client sessionAuthClient;
    private long lastAuthTime = 0;
    private String jwt;
    private int authRetries = 0;

    public SymOBORSAAuth(final SymConfig configuration) {
        logger.info("SymOBOAuth being constructed");
        this.config = configuration;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config);
        Client client = clientBuilder.build();

        if (isEmpty(config.getProxyURL()) && isEmpty(config.getPodProxyURL())) {
            this.sessionAuthClient = client;
        } else {
            String proxyURL = !isEmpty(config.getPodProxyURL()) ?
                config.getPodProxyURL() : config.getProxyURL();
            String proxyUser = !isEmpty(config.getPodProxyUsername()) ?
                config.getPodProxyUsername() : config.getProxyUsername();
            String proxyPass = !isEmpty(config.getPodProxyPassword()) ?
                config.getPodProxyPassword() : config.getProxyPassword();

            ClientConfig clientConfig = new ClientConfig();
            clientConfig.connectorProvider(new ApacheConnectorProvider());
            clientConfig.property(ClientProperties.PROXY_URI, proxyURL);
            if (!isEmpty(proxyUser) && !isEmpty(proxyPass)) {
                clientConfig.property(ClientProperties.PROXY_USERNAME, proxyUser);
                clientConfig.property(ClientProperties.PROXY_PASSWORD, proxyPass);
            }
            this.sessionAuthClient = clientBuilder.withConfig(clientConfig).build();
        }
    }

    public SymOBORSAAuth(final SymConfig configuration, final ClientConfig sessionAuthClientConfig) {
        logger.info("SymOBORSAAuth being constructed with ClientConfig variable");
        this.config = configuration;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config);
        if (sessionAuthClientConfig != null) {
            this.sessionAuthClient = clientBuilder.withConfig(sessionAuthClientConfig).build();
        } else {
            this.sessionAuthClient = clientBuilder.build();
        }
    }

    public SymOBOUserRSAAuth getUserAuth(final String username) {
        SymOBOUserRSAAuth userAuth = new SymOBOUserRSAAuth(config, sessionAuthClient, username, this);
        userAuth.authenticate();
        return userAuth;
    }

    public SymOBOUserRSAAuth getUserAuth(final Long uid) {
        SymOBOUserRSAAuth userAuth = new SymOBOUserRSAAuth(config, sessionAuthClient, uid, this);
        userAuth.authenticate();
        return userAuth;
    }

    public void authenticate() {
        PrivateKey privateKey = null;
        try {
            privateKey = JwtHelper.parseRSAPrivateKey(new File(config.getAppPrivateKeyPath() + config.getAppPrivateKeyName()));
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Error trying to parse RSA private key", e);
        }
        if (lastAuthTime == 0 | System.currentTimeMillis() - lastAuthTime > 3000) {
            logger.info("Last auth time was {}", lastAuthTime);
            logger.info("Now is {}", System.currentTimeMillis());
            jwt = JwtHelper.createSignedJwt(config.getAppId(), AuthEndpointConstants.JWT_EXPIRY_MS, privateKey);
            sessionAppAuthenticate();
            lastAuthTime = System.currentTimeMillis();
        } else {
            try {
                logger.info("Re-authenticated too fast. Wait 30 seconds to try again.");
                TimeUnit.SECONDS.sleep(AuthEndpointConstants.TIMEOUT);
                authenticate();
            } catch (InterruptedException e) {
                logger.error("Error with authentication", e);
            }
        }
    }

    public void sessionAppAuthenticate() {
        Map<String, String> token = new HashMap<>();
        token.put("token", jwt);
        if (config != null) {
            logger.info("Session app auth");
            Response response
                = sessionAuthClient.target(CommonConstants.HTTPS_PREFIX
                + config.getSessionAuthHost()
                + ":" + config.getSessionAuthPort())
                .path(AuthEndpointConstants.SESSION_APP_AUTH_PATH_RSA)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(token, MediaType.APPLICATION_JSON));
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
                if (authRetries++ > AuthEndpointConstants.MAX_AUTH_RETRY) {
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
