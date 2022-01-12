package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.http.api.ApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@API(status = API.Status.INTERNAL)
public
class OAuthSession {

  private final AuthSession authSession;

  public String getBearerToken() throws ApiException {
    try {
      refreshIfNeeded();
    } catch (AuthUnauthorizedException e) {
      throw new ApiException(401, e.getMessage());
    }
    return authSession.getAuthorizationToken();
  }

  /**
   * If the bearer token is expired this function is going to call the login endpoint and
   * update the auth session with new tokens.
   *
   * @throws AuthUnauthorizedException if the authentication fails
   */
  private void refreshIfNeeded() throws AuthUnauthorizedException {
    int interval = 5;
    if (this.authSession.getAuthorizationToken() == null || this.authSession.getAuthTokenExpirationDate() == null) {
      String errorMessage = "Common jwt feature is not available in your pod, SBE version should be at least 20.14.";
      log.error(errorMessage);
      throw new UnsupportedOperationException(errorMessage);
    }

    if (Instant.now().getEpochSecond() + interval >= this.authSession.getAuthTokenExpirationDate()) {
      this.authSession.refreshAuthToken();
    }
  }
}
