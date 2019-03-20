package authentication;

import clients.symphony.api.APIClient;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import exceptions.NoConfigException;
import model.AppAuthResponse;
import org.apache.commons.codec.binary.Hex;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpClientBuilderHelper;
import utils.JwtHelper;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public final class SymExtensionAppRSAAuth extends APIClient {
    private final SecureRandom secureRandom = new SecureRandom();
    private final Logger logger = LoggerFactory.getLogger(SymExtensionAppRSAAuth.class);
    private SymConfig config;
    private Client sessionAuthClient;
    private String jwt;
    private int authRetries = 0;

    public SymExtensionAppRSAAuth(final SymConfig configuration) {
        this.config = configuration;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config);
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

    public SymExtensionAppRSAAuth(SymConfig config, ClientConfig sessionAuthClientConfig, ClientConfig kmAuthClientConfig) {
        this.config = config;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config);
        if (sessionAuthClientConfig != null) {
            this.sessionAuthClient = clientBuilder.withConfig(sessionAuthClientConfig).build();
        } else {
            this.sessionAuthClient = clientBuilder.build();
        }
    }

    public AppAuthResponse appAuthenticate() {
        PrivateKey privateKey = getPrivateKey();
        if (config != null) {
            logger.info("RSA extension app auth");
            jwt = JwtHelper.createSignedJwt(config.getAppId(), AuthEndpointConstants.JWT_EXPIRY_MS, privateKey);
            Map<String, String> token = new HashMap<>();
            token.put("appToken", generateToken());
            token.put("authToken", jwt);
            Response response
                = sessionAuthClient.target(CommonConstants.HTTPS_PREFIX +
                config.getSessionAuthHost() + ":" + config.getSessionAuthPort())
                .path(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH_RSA)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(token, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, null);
                } catch (Exception e) {
                    logger.error("Unexpected error, retry authentication in 30 seconds");
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
                appAuthenticate();
            } else {
                AppAuthResponse appAuthResponse = response.readEntity(AppAuthResponse.class);
                return appAuthResponse;
            }
        } else {
            throw new NoConfigException("Must provide a SymConfig object to authenticate");

        }
        return null;
    }

    private PrivateKey getPrivateKey() {
        PrivateKey privateKey = null;
        try {
            privateKey = JwtHelper.parseRSAPrivateKey(
                new File(config.getAppPrivateKeyPath() + config.getAppPrivateKeyName()));
        } catch (IOException | GeneralSecurityException e) {
            logger.error(e.getMessage());
        }
        return privateKey;
    }

    public String generateToken() {
        byte[] randBytes = new byte[64];
        secureRandom.nextBytes(randBytes);
        return Hex.encodeHexString(randBytes);
    }

}
