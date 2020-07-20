package authentication;

import authentication.extensionapp.InMemoryTokensRepository;
import authentication.extensionapp.TokensRepository;
import clients.symphony.api.APIClient;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import exceptions.NoConfigException;
import model.AppAuthResponse;
import model.PodCert;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

abstract class AbstractSymExtensionAppAuth extends APIClient implements ISymExtensionAppAuth {
    private final Logger logger = LoggerFactory.getLogger(AbstractSymExtensionAppAuth.class);

    private final SecureRandom secureRandom = new SecureRandom();
    protected final SymConfig config;
    protected Client sessionAuthClient;
    protected final TokensRepository tokensRepository;
    private int authRetries = 0;

    protected AbstractSymExtensionAppAuth(SymConfig config) {
        this.config = config;
        this.tokensRepository = new InMemoryTokensRepository();
    }

    protected AbstractSymExtensionAppAuth(SymConfig config, TokensRepository tokensRepository) {
        this.config = config;
        this.tokensRepository = tokensRepository;
    }

    public Boolean validateTokens(String appToken, String symphonyToken) {
        return tokensRepository.get(appToken)
                .filter(token -> token.getSymphonyToken().equals(symphonyToken))
                .isPresent();
    }

    protected String generateToken() {
        byte[] randBytes = new byte[64];
        secureRandom.nextBytes(randBytes);
        return Hex.encodeHexString(randBytes);
    }

    protected AppAuthResponse handleSessionAppAuthFailure(Response response, String appToken, String... podSessionAuthUrl) {
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
        return sessionAppAuthenticate(appToken, podSessionAuthUrl);
    }

    protected String formattedPodSessionAuthUrl(String... podSessionAuthUrl) {
        String formattedUrl;
        if (podSessionAuthUrl.length == 0 || podSessionAuthUrl[0] == null
                || StringUtils.isBlank(podSessionAuthUrl[0])) {
            if (config == null) {
                throw new NoConfigException("Must provide a SymConfig object to authenticate");
            }
            formattedUrl = this.config.getSessionAuthUrl();
        } else {
            formattedUrl = CommonConstants.HTTPS_PREFIX + podSessionAuthUrl[0];
        }
        return formattedUrl;
    }

    protected String getPodCertificateFromCertPath(String podCertPath, String... podSessionAuthUrl) {
        String target = this.formattedPodSessionAuthUrl(podSessionAuthUrl);
        String podCertificate;
        do {
            Response response
                    = sessionAuthClient.target(target)
                    .path(podCertPath)
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
                break;
            }
        } while (true);
        return podCertificate;
    }
}
