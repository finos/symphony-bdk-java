package authentication;

import clients.symphony.api.APIClient;
import configuration.SymConfig;
import model.SessionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public final class SymOBOUserAuth extends APIClient implements ISymAuth {
  private final Logger logger = LoggerFactory.getLogger(SymOBOUserAuth.class);
  private String sessionToken;
  private SymConfig config;
  private Client sessionAuthClient;
  private Long uid;
  private String username;
  private ISymOBOAuth appAuth;
  private int authRetries = 0;
  private int timeout = AuthEndpointConstants.TIMEOUT;
  private int maxRetry = AuthEndpointConstants.MAX_AUTH_RETRY;

  public SymOBOUserAuth(final SymConfig config,
      final Client sessionAuthClient,
      final Long uid, final ISymOBOAuth appAuth) {
    this.config = config;
    this.sessionAuthClient = sessionAuthClient;
    this.uid = uid;
    this.appAuth = appAuth;
  }

  public SymOBOUserAuth(final SymConfig config,
      final Client sessionAuthClient,
      final Long uid, final ISymOBOAuth appAuth, int timeout) {
    this.config = config;
    this.sessionAuthClient = sessionAuthClient;
    this.uid = uid;
    this.appAuth = appAuth;
    this.timeout = timeout;
  }

  public SymOBOUserAuth(final SymConfig config,
      final Client sessionAuthClient,
      final String username, final ISymOBOAuth appAuth) {
    this.config = config;
    this.sessionAuthClient = sessionAuthClient;
    this.username = username;
    this.appAuth = appAuth;
  }

  public SymOBOUserAuth(final SymConfig config,
      final Client sessionAuthClient,
      final String username, final ISymOBOAuth appAuth, int timeout) {
    this.config = config;
    this.sessionAuthClient = sessionAuthClient;
    this.username = username;
    this.appAuth = appAuth;
    this.timeout = timeout;
  }

  /** Constructor for testing purpose only */
  protected SymOBOUserAuth(final SymConfig config, final Client sessionAuthClient, final String username,
      final ISymOBOAuth appAuth, int timeout, int maxRetry) {
    this.config = config;
    this.sessionAuthClient = sessionAuthClient;
    this.username = username;
    this.appAuth = appAuth;
    this.maxRetry = maxRetry;
    this.timeout = timeout;
  }

  @Override
  public void authenticate() {
    sessionAuthenticate();
  }

  @Override
  public void sessionAuthenticate() {
    String target = this.config.getSessionAuthUrl();
    String path = (uid != null) ?
        AuthEndpointConstants.OBO_USER_ID_AUTH_PATH.replace("{uid}", Long.toString(uid)) :
        AuthEndpointConstants.OBO_USER_NAME_AUTH_PATH.replace("{username}", username);

    Invocation.Builder builder = sessionAuthClient.target(target)
        .path(path)
        .request(MediaType.APPLICATION_JSON)
        .header("sessionToken", appAuth.getSessionToken());

    try (Response response = builder.post(null)) {
      if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
        try {
          handleError(response, null);
        } catch (Exception e) {
          logger.error("Unexpected error, "
              + "retry authentication in 30 seconds");
        }
        try {
          TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
          logger.error("Error with authentication", e);
        }
        if (authRetries++ > maxRetry) {
          logger.error("Max retries reached. Giving up on auth.");
          return;
        }
        appAuth.sessionAppAuthenticate();
        sessionAuthenticate();
      } else {
        SessionToken session = response.readEntity(SessionToken.class);
        this.sessionToken = session.getSessionToken();
      }
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
