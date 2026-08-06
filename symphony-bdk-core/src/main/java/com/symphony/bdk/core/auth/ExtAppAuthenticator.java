package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;

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
  @Nonnull ExtAppAuthSession authenticateExtApp() throws AuthUnauthorizedException;
}
