package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthInitializationException;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * Interface for authentication factory class to provide new instances for the main authenticators :
 * <ul>
 *   <li>{@link BotAuthenticator} : to authenticate the main Bot service account</li>
 *   <li>{@link OboAuthenticator} : to perform on-behalf-of authentication</li>
 * </ul>
 */
@API(status = API.Status.STABLE)
public interface AuthenticatorFactory {

  /**
   * Creates a new instance of a {@link BotAuthenticator} service.
   *
   * @return a new {@link BotAuthenticator} instance.
   */
  @Nonnull
  BotAuthenticator getBotAuthenticator() throws AuthInitializationException;

  /**
   * Creates a new instance of an {@link OboAuthenticator} service.
   *
   * @return a new {@link OboAuthenticator} instance.
   */
  @Nonnull
  OboAuthenticator getOboAuthenticator() throws AuthInitializationException;

  /**
   * Creates a new instance of an {@link ExtensionAppAuthenticator} service.
   *
   * @return a new {@link ExtensionAppAuthenticator} instance.
   */
  @Nonnull
  ExtensionAppAuthenticator getExtensionAppAuthenticator() throws AuthInitializationException;
}
