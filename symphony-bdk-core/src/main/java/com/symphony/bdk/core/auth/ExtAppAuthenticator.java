package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import org.apiguardian.api.API;

/**
 * Extension App authenticator service.
 */
@API(status = API.Status.STABLE)
public interface ExtAppAuthenticator {

  /**
   * Authenticates an extension app.
   *
   * @return the authentication session.
   */
  ExtAppAuthSession authenticateExtApp() throws AuthUnauthorizedException;
}
