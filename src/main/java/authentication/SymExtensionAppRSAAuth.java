package authentication;

import authentication.extensionapp.InMemoryTokensRepository;
import authentication.extensionapp.TokensRepository;
import clients.symphony.api.APIClient;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import exceptions.NoConfigException;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
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
import org.apache.commons.codec.binary.Hex;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CertificateUtils;
import utils.HttpClientBuilderHelper;
import utils.JwtHelper;
import static utils.JwtHelper.validateJwt;

public final class SymExtensionAppRSAAuth extends APIClient {
    private final Logger logger = LoggerFactory.getLogger(SymExtensionAppRSAAuth.class);
    private final SecureRandom secureRandom = new SecureRandom();
    private SymConfig config;
    private Client sessionAuthClient;
    private int authRetries = 0;
    private TokensRepository tokensRepository;
    private String podCertificate;
    private PrivateKey appPrivateKey;

    /**
     * Create an instance initialized with provided Symphony configuration.
     *
     * @param config the Symphony configuration
     */
    public SymExtensionAppRSAAuth(SymConfig config) {
        this(config, null);
    }

    /**
     * Create an instance initialized with provided Symphony configuration and app RSA private key. The parts of the
     * configuration related to app RSA private key will be ignored, e.g. SymConfig#getAppPrivateKeyPath() and
     * SymConfig#getAppPrivateKeyName(). If given private key is null, then the initialization will only use the
     * configuration, see {@link SymExtensionAppRSAAuth#SymExtensionAppRSAAuth(SymConfig)}.
     *
     * @param config        the Symphony configuration
     * @param appPrivateKey the RSA private key
     */
    public SymExtensionAppRSAAuth(final SymConfig config, PrivateKey appPrivateKey) {
        this.config = config;
        this.appPrivateKey = appPrivateKey;

        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config);
        this.sessionAuthClient = clientBuilder.build();

        ClientConfig clientConfig = HttpClientBuilderHelper.getPodClientConfig(config);
        if (clientConfig != null) {
            this.sessionAuthClient = clientBuilder.withConfig(clientConfig).build();
        }

        this.tokensRepository = new InMemoryTokensRepository();
        setupPodCertificate();
    }

    public SymExtensionAppRSAAuth(SymConfig config, ClientConfig sessionAuthClientConfig, TokensRepository tokensRepository) {
        this.config = config;
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config);
        if (sessionAuthClientConfig != null) {
            this.sessionAuthClient = clientBuilder.withConfig(sessionAuthClientConfig).build();
        } else {
            this.sessionAuthClient = clientBuilder.build();
        }

        this.tokensRepository = tokensRepository;
        setupPodCertificate();
    }

    public AppAuthResponse appAuthenticate() {
        if (config == null) {
            throw new NoConfigException("Must provide a SymConfig object to authenticate");
        }

        logger.info("RSA extension app auth");
        PrivateKey appPrivateKey = getAppPrivateKey();
        String jwt = JwtHelper.createSignedJwt(config.getAppId(), AuthEndpointConstants.JWT_EXPIRY_MS, appPrivateKey);
        Map<String, String> token = new HashMap<>();
        token.put("appToken", generateToken());
        token.put("authToken", jwt);

        String urlTarget = CommonConstants.HTTPS_PREFIX + config.getSessionAuthHost();
        if (config.getSessionAuthPort() != 443) {
            urlTarget += ":" + config.getSessionAuthPort();
        }
        Invocation.Builder builder = sessionAuthClient.target(urlTarget)
            .path(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH_RSA)
            .request(MediaType.APPLICATION_JSON);

        try (Response response = builder.post(Entity.entity(token, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                AppAuthResponse appAuthResponse = response.readEntity(AppAuthResponse.class);
                return tokensRepository.save(appAuthResponse);
            } else {
                return handleSessionAppAuthFailure(response);
            }
        }
    }

    private AppAuthResponse handleSessionAppAuthFailure(Response response) {
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
        return appAuthenticate();
    }

    private PrivateKey getAppPrivateKey() {
        if (appPrivateKey == null) {
            try {
                String privateKeyFilePath = config.getAppPrivateKeyPath() + config.getAppPrivateKeyName();
                appPrivateKey = JwtHelper.parseRSAPrivateKey(new File(privateKeyFilePath));
            } catch (IOException | GeneralSecurityException e) {
                logger.error("Failed to obtain app RSA private key. An exception occurred parsing app RSA file", e);
            }
        }
        return appPrivateKey;
    }

    private String generateToken() {
        byte[] randBytes = new byte[64];
        secureRandom.nextBytes(randBytes);
        return Hex.encodeHexString(randBytes);
    }

    public Boolean validateTokens(String appToken, String symphonyToken) {
        return tokensRepository.get(appToken)
            .filter(token -> token.getSymphonyToken().equals(symphonyToken))
            .isPresent();
    }

    public UserInfo verifyJWT(final String jwt) {
        return validateJwt(jwt, podCertificate);
    }

    public PublicKey getPodPublicKey() throws CertificateException {
        return CertificateUtils.parseX509Certificate(podCertificate).getPublicKey();
    }

    private void setupPodCertificate() {
        String authUrl = config.getSessionAuthHost() + ":" + config.getSessionAuthPort();
        Response response
            = sessionAuthClient.target(CommonConstants.HTTPS_PREFIX + authUrl)
            .path(AuthEndpointConstants.POD_CERT_RSA_PATH)
            .request(MediaType.APPLICATION_JSON)
            .get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
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
        } else {
            podCertificate = response.readEntity(PodCert.class).getCertificate();
        }
    }
}
