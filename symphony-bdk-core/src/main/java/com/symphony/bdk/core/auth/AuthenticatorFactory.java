package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthInitializationException;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * Factory responsible for creating different authenticators.
 * <ul>
 *   <li>Bot Authenticator: for authenticating the main bot service account</li>
 *   <li>OBO Authenticator: for authenticating on behalf of a regular Symphony user</li>
 *   <li>Extension App Authenticator: for authenticating an extension application</li>
 * </ul>
 */
@API(status = API.Status.STABLE)
public interface AuthenticatorFactory {

  /**
   * Creates a new instance of a {@link BotAuthenticator}.
   *
   * @return a new {@link BotAuthenticator} instance.
   * @throws AuthInitializationException if the authenticator cannot be instantiated.
   */
  @Nonnull
  BotAuthenticator getBotAuthenticator() throws AuthInitializationException;

  /**
   * Creates a new instance of a {@link OboAuthenticator}.
   *
   * @return a new {@link OboAuthenticator} instance.
   * @throws AuthInitializationException if the authenticator cannot be instantiated.
   */
  @Nonnull
  OboAuthenticator getOboAuthenticator() throws AuthInitializationException;

  /**
   * Creates a new instance of a {@link ExtensionAppAuthenticator}.
   *
   * @return a new {@link ExtensionAppAuthenticator} instance.
   * @throws AuthInitializationException if the authenticator cannot be instantiated.
   */
  @Nonnull
  ExtensionAppAuthenticator getExtensionAppAuthenticator() throws AuthInitializationException;
}
