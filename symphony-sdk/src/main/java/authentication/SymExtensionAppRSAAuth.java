package authentication;

import authentication.extensionapp.TokensRepository;
import configuration.SymConfig;
import model.AppAuthResponse;
import model.UserInfo;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CertificateUtils;
import utils.HttpClientBuilderHelper;
import utils.JwtHelper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import static utils.JwtHelper.validateJwt;

public final class SymExtensionAppRSAAuth extends AbstractSymExtensionAppAuth {
    private final Logger logger = LoggerFactory.getLogger(SymExtensionAppRSAAuth.class);
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
        super(config);
        this.appPrivateKey = appPrivateKey;
        ClientConfig clientConfig = HttpClientBuilderHelper.getPodClientConfig(config);
        this.initSessionAuthClient(config, clientConfig);
    }

    public SymExtensionAppRSAAuth(SymConfig config, ClientConfig sessionAuthClientConfig, TokensRepository tokensRepository) {
        super(config, tokensRepository);
        this.initSessionAuthClient(config, sessionAuthClientConfig);
    }

    private void initSessionAuthClient(SymConfig config, ClientConfig clientConfig) {
        ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(config);
        if (clientConfig != null) {
            this.sessionAuthClient = clientBuilder.withConfig(clientConfig).build();
        } else {
            this.sessionAuthClient = clientBuilder.build();
        }
    }

    @Override
    public AppAuthResponse appAuthenticate() {
        String appToken = this.generateToken();
        return sessionAppAuthenticate(appToken);
    }

    @Override
    public AppAuthResponse sessionAppAuthenticate(String appToken, String... podSessionAuthUrl) {
        String target = this.formattedPodSessionAuthUrl(podSessionAuthUrl);
        logger.info("RSA extension app auth");
        PrivateKey appPrivateKey = getAppPrivateKey();
        String jwt = JwtHelper.createSignedJwt(config.getAppId(), AuthEndpointConstants.JWT_EXPIRY_MS, appPrivateKey);
        Map<String, String> token = new HashMap<>();
        token.put("appToken", appToken);
        token.put("authToken", jwt);
        Invocation.Builder builder = sessionAuthClient.target(target)
                .path(AuthEndpointConstants.SESSION_EXT_APP_AUTH_PATH_RSA)
                .request(MediaType.APPLICATION_JSON);
        try (Response response = builder.post(Entity.entity(token, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                AppAuthResponse appAuthResponse = response.readEntity(AppAuthResponse.class);
                return tokensRepository.save(appAuthResponse);
            } else {
                return handleSessionAppAuthFailure(response, appToken, podSessionAuthUrl);
            }
        }
    }

    @Override
    public UserInfo verifyJWT(String jwt, String... podSessionAuthUrl) {
        String podCertificate = this.getPodCertificate(podSessionAuthUrl);
        return validateJwt(jwt, podCertificate);
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

    public PublicKey getPodPublicKey() throws CertificateException {
        String podCertificate = this.getPodCertificate();
        return CertificateUtils.parseX509Certificate(podCertificate).getPublicKey();
    }

    private String getPodCertificate(String... podSessionAuthUrl) {
        return this.getPodCertificateFromCertPath(AuthEndpointConstants.POD_CERT_RSA_PATH, podSessionAuthUrl);
    }

}
