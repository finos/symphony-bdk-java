package authentication;

import clients.symphony.api.APIClient;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

public final class SymOBOUserRSAAuth extends APIClient implements ISymAuth {
    private final Logger logger = LoggerFactory.getLogger(SymOBOUserRSAAuth.class);
    private String sessionToken;
    private SymConfig config;
    private Client sessionAuthClient;
    private Long uid;
    private String username;
    private ISymOBOAuth appAuth;
    private int authRetries = 0;

    public SymOBOUserRSAAuth(final SymConfig config,
                             final Client sessionAuthClient,
                             final Long uid, final ISymOBOAuth appAuth) {
        this.config = config;
        this.sessionAuthClient = sessionAuthClient;
        this.uid = uid;
        this.appAuth = appAuth;
    }

    public SymOBOUserRSAAuth(final SymConfig config,
                             final Client sessionAuthClient,
                             final String username, final ISymOBOAuth appAuth) {
        this.config = config;
        this.sessionAuthClient = sessionAuthClient;
        this.username = username;
        this.appAuth = appAuth;
    }

    @Override
    public void authenticate() {
        sessionAuthenticate();
    }

    @Override
    public void sessionAuthenticate() {
        Response response = null;
        if (uid != null) {
            response = sessionAuthClient.target(
                CommonConstants.HTTPS_PREFIX
                    + config.getSessionAuthHost()
                    + ":" + config.getSessionAuthPort())
                .path(AuthEndpointConstants.OBO_USER_ID_AUTH_PATH_RSA
                    .replace("{uid}", Long.toString(uid)))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken", appAuth.getSessionToken())
                .post(null);
        } else {
            response = sessionAuthClient.target(
                CommonConstants.HTTPS_PREFIX
                    + config.getSessionAuthHost()
                    + ":" + config.getSessionAuthPort())
                .path(AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH_RSA
                    .replace("{username}", username))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken", appAuth.getSessionToken())
                .post(null);
        }
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
                logger.error("Error with authentication", e);
            }
            if (authRetries++ > AuthEndpointConstants.MAX_AUTH_RETRY) {
                logger.error("Max retries reached. Giving up on auth.");
                return;
            }
            appAuth.sessionAppAuthenticate();
            sessionAuthenticate();
        } else {
            Token token = response.readEntity(Token.class);
            this.sessionToken = token.getToken();
        }
    }

    @Override
    public void kmAuthenticate() {
        logger.warn("method kmAuthenticate is invalid");
        throw new RuntimeException("this method is not supported");
    }

    @Override
    public String getSessionToken() {
        return sessionToken;
    }

    @Override
    public void setSessionToken(final String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public String getKmToken() {
        logger.warn("method kmAuthenticate is invalid");
        throw new RuntimeException("this method kmAuthenticate is not supported");
    }

    @Override
    public void setKmToken(final String kmToken) {
        logger.warn("method setKmToken is invalid");
        throw new RuntimeException("this method setKmToken is not supported");
    }

    @Override
    public void logout() {
    }
}
