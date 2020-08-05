package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.auth.impl.BotAuthenticatorRSAImpl;
import com.symphony.bdk.core.auth.impl.OboAuthenticatorRSAImpl;
import com.symphony.bdk.core.config.BdkConfig;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * Factory class that provides new instances for the main authenticators :
 * <ul>
 *   <li>{@link BotAuthenticator} : to authenticate the main Bot service account</li>
 *   <li>{@link OboAuthenticator} : to perform on-behalf-of authentication</li>
 * </ul>
 */
@API(status = API.Status.STABLE)
public class AuthenticatorFactory {

  private final BdkConfig config;
  private final ApiClient loginApiClient;
  private final ApiClient relayApiClient;

  public AuthenticatorFactory(@Nonnull BdkConfig bdkConfig, @Nonnull ApiClient loginClient, @Nonnull ApiClient relayClient) {
    this.config = bdkConfig;
    this.loginApiClient = loginClient;
    this.relayApiClient = relayClient;
  }

  /**
   * Creates a new instance of a {@link BotAuthenticator} service.
   *
   * @return a new {@link BotAuthenticator} instance.
   */
  public @Nonnull BotAuthenticator getBotAuthenticator() {

    return new BotAuthenticatorRSAImpl(
        this.config.getUsername(),
        this.config.getBotPrivateKey(),
        this.loginApiClient,
        this.relayApiClient
    );
  }

  /**
   * Creates a new instance of an {@link OboAuthenticator} service.
   *
   * @return a new {@link OboAuthenticator} instance.
   */
  public @Nonnull OboAuthenticator getOboAuthenticator() {

    return new OboAuthenticatorRSAImpl(
        this.config.getAppId(),
        this.config.getAppPrivateKey(),
        this.loginApiClient
    );
  }
}
