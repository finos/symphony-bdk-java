package authentication;

import clients.symphony.api.APIClient;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import model.Token;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpClientBuilderHelper;
import utils.JwtHelper;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SymBotRSAAuth extends APIClient implements ISymAuth {
    private final Logger logger = LoggerFactory.getLogger(SymBotRSAAuth.class);
    private String sessionToken;
    private String kmToken;
    private SymConfig config;
    private Client sessionAuthClient;
    private Client kmAuthClient;
    private String jwt;
    private long lastAuthTime = 0;
    private int authRetries = 0;

    public SymBotRSAAuth(SymConfig config) {
        this.config = config;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config);
        Client client = clientBuilder.build();

        this.sessionAuthClient = client;
        this.kmAuthClient = client;

        ClientConfig clientConfig = HttpClientBuilderHelper.getPodClientConfig(config);
        if (clientConfig != null) {
            this.sessionAuthClient = clientBuilder.withConfig(clientConfig).build();
        }

        ClientConfig kmClientConfig = HttpClientBuilderHelper.getKMClientConfig(config);
        if (kmClientConfig != null) {
            this.kmAuthClient = clientBuilder.withConfig(kmClientConfig).build();
        }
    }

    public SymBotRSAAuth(SymConfig config, ClientConfig sessionAuthClientConfig, ClientConfig kmAuthClientConfig) {
        this.config = config;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config);
        if (sessionAuthClientConfig != null) {
            this.sessionAuthClient = clientBuilder.withConfig(sessionAuthClientConfig).build();
        } else {
            this.sessionAuthClient = clientBuilder.build();
        }
        if (kmAuthClientConfig != null) {
            this.kmAuthClient = clientBuilder.withConfig(kmAuthClientConfig).build();
        } else {
            this.kmAuthClient = clientBuilder.build();
        }
    }

    @Override
    public void authenticate() {
        PrivateKey privateKey = null;
        try {
            privateKey = JwtHelper.parseRSAPrivateKey(this.getRSAPrivateKeyFile(this.config));
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Error trying to parse RSA private key", e);
        }
        if (lastAuthTime == 0 | System.currentTimeMillis() - lastAuthTime > 3000) {
            logger.info("Last auth time was {}", lastAuthTime);
            logger.info("Now is {}", System.currentTimeMillis());
            jwt = JwtHelper.createSignedJwt(config.getBotUsername(), AuthEndpointConstants.JWT_EXPIRY_MS, privateKey);
            sessionAuthenticate();
            kmAuthenticate();
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

    protected InputStream getRSAPrivateKeyFile(final SymConfig config) throws FileNotFoundException {
        final String dirPath = config.getBotPrivateKeyPath();
        final String keyName = config.getBotPrivateKeyName();
        final String path = dirPath + (dirPath.endsWith(File.separator) ? "" : File.separator) + keyName;
        if (path.startsWith("classpath:")) {
            return this.getClass().getResourceAsStream(path.replace("classpath:", ""));
        }
        return new FileInputStream(path);
    }

    @Override
    public void sessionAuthenticate() {
        Map<String, String> token = new HashMap<>();
        token.put("token", jwt);
        Response response
            = this.sessionAuthClient.target(CommonConstants.HTTPS_PREFIX + config.getPodHost() + ":" + config.getPodPort())
            .path(AuthEndpointConstants.SESSION_AUTH_PATH_RSA)
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
                return;
            }
            sessionAuthenticate();
        } else {
            sessionToken = response.readEntity(Token.class).getToken();
        }
    }

    @Override
    public void kmAuthenticate() {
        Map<String, String> token = new HashMap<>();
        token.put("token", jwt);
        Response response
            = this.kmAuthClient.target(CommonConstants.HTTPS_PREFIX + config.getKeyAuthHost() + ":" + config.getKeyAuthPort())
            .path(AuthEndpointConstants.KEY_AUTH_PATH_RSA)
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
                return;
            }
            kmAuthenticate();
        } else {
            kmToken = response.readEntity(Token.class).getToken();
        }
    }

    @Override
    public String getSessionToken() {
        return sessionToken;
    }

    @Override
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public String getKmToken() {
        return kmToken;
    }

    @Override
    public void setKmToken(String kmToken) {
        this.kmToken = kmToken;
    }

    @Override
    public void logout() {
        logger.info("Logging out");
        Client client = ClientBuilder.newClient();
        Response response = client.target(CommonConstants.HTTPS_PREFIX + config.getSessionAuthHost() + ":" + config.getSessionAuthPort())
            .path(AuthEndpointConstants.LOGOUT_PATH)
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