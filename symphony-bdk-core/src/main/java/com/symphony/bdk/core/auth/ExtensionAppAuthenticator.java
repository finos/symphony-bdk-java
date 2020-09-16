package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * Extension App Authenticator Service.
 */
@API(status = API.Status.STABLE)
public interface ExtensionAppAuthenticator {

  /**
   * Authenticates an extension app.
   *
   * @return the extension app authentication session.
   */
  @Nonnull
  AuthSessionExtensionApp authenticateExtensionApp(String appToken) throws AuthUnauthorizedException;

}
