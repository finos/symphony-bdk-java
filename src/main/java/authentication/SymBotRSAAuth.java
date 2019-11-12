package authentication;

import clients.ISymClient;
import clients.symphony.api.APIClient;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import exceptions.AuthenticationException;
import exceptions.SymClientException;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Token;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpClientBuilderHelper;
import utils.JwtHelper;

public class SymBotRSAAuth extends APIClient implements ISymAuth {
    private final Logger logger = LoggerFactory.getLogger(SymBotRSAAuth.class);
    private String sessionToken = null;
    private String kmToken = null;
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
        ClientConfig kmClientConfig = HttpClientBuilderHelper.getKMClientConfig(config);

        this.sessionAuthClient = clientBuilder.withConfig(clientConfig).build();
        this.kmAuthClient = clientBuilder.withConfig(kmClientConfig).build();
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
    public void authenticate() throws AuthenticationException {
        PrivateKey privateKey = null;
        try {
            privateKey = JwtHelper.parseRSAPrivateKey(this.getRSAPrivateKeyFile(this.config));
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Error trying to parse RSA private key", e);
        }
        if (lastAuthTime == 0 || System.currentTimeMillis() - lastAuthTime > AuthEndpointConstants.WAIT_TIME) {
            logger.info("Last auth time was {}", lastAuthTime);
            logger.info("Now is {}", System.currentTimeMillis());
            jwt = JwtHelper.createSignedJwt(config.getBotUsername(), AuthEndpointConstants.JWT_EXPIRY_MS, privateKey);

            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<AuthenticationException> sessionAuthFuture = executor.submit(() -> {
                try {
                    sessionAuthenticate();
                    return null;
                } catch (AuthenticationException e) {
                    return e;
                }
            });
            Future<AuthenticationException> kmAuthFuture = executor.submit(() -> {
                try {
                    kmAuthenticate();
                    return null;
                } catch (AuthenticationException e) {
                    return e;
                }
            });
            executor.shutdown();

            try {
                int connectionTimeout = config.getConnectionTimeout();
                if (connectionTimeout == 0) {
                    connectionTimeout = 35000;
                }
                executor.awaitTermination(connectionTimeout, TimeUnit.MILLISECONDS);
                executor.shutdownNow();
                if (!executor.isTerminated()) {
                    throw new AuthenticationException(new Exception("Timeout"));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Termination Interrupted");
            }

            try {
                if (sessionAuthFuture.get() != null) {
                    throw sessionAuthFuture.get();
                }
                if (kmAuthFuture.get() != null) {
                    throw kmAuthFuture.get();
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Interrupted Exception");
            }

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
    public void sessionAuthenticate() throws AuthenticationException {
        Map<String, String> token = new HashMap<>();
        token.put("token", jwt);

        Invocation.Builder builder = this.sessionAuthClient
            .target(config.getPodUrl())
            .path(AuthEndpointConstants.SESSION_AUTH_PATH_RSA)
            .request(MediaType.APPLICATION_JSON);

        try (Response response = builder.post(Entity.entity(token, MediaType.APPLICATION_JSON))) {
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
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    protected void handleError(Response response, ISymClient botClient) throws SymClientException {
        super.handleError(response, botClient);
    }

    @Override
    public void kmAuthenticate() throws AuthenticationException {
        Map<String, String> token = new HashMap<>();
        token.put("token", jwt);

        Invocation.Builder builder = this.kmAuthClient
            .target(config.getKeyAuthUrl())
            .path(AuthEndpointConstants.KEY_AUTH_PATH_RSA)
            .request(MediaType.APPLICATION_JSON);

        try (Response response = builder.post(Entity.entity(token, MediaType.APPLICATION_JSON))) {
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
        } catch (Exception e) {
            throw new AuthenticationException(e);
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
        String target = CommonConstants.HTTPS_PREFIX + config.getSessionAuthHost() + ":" + config.getSessionAuthPort();
        Invocation.Builder builder = client.target(target)
            .path(AuthEndpointConstants.LOGOUT_PATH)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", getSessionToken());

        try (Response response = builder.post(null)) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, null);
                } catch (Exception e) {
                    logger.error("Unexpected error, retry logout in 30 seconds", e);
                }
            }
        }
    }
}
