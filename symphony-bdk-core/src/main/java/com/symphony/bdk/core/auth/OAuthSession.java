package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

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

  public void refresh() throws AuthUnauthorizedException {
    int interval = 5;
    if (this.authSession.getAuthorizationToken() == null || this.authSession.getAuthTokenExpirationDate() == null) {
      log.debug("Common jwt feature is not available in your pod, version should be greater than 20.13. "
          + "Switching back to the session token authentication.");
      return;
    }

    if (Instant.now().getEpochSecond() + interval >= this.authSession.getAuthTokenExpirationDate()) {
      this.authSession.refreshAuthToken();
    }
  }

  public String getBearerToken() {
    return authSession.getAuthorizationToken();
  }
}
