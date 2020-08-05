package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.auth.impl.BotAuthenticatorRSAImpl;
import com.symphony.bdk.core.auth.impl.OboAuthenticatorRSAImpl;
import com.symphony.bdk.core.config.BdkConfig;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 *
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
   * Directly returns the (not refreshed) Bot's {@link AuthSession} handle.
   *
   * @return a not-refreshed {@link AuthSession} handle.
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
   *
   * @return
   */
  public @Nonnull OboAuthenticator getOboAuthenticator() {

    return new OboAuthenticatorRSAImpl(
        this.config.getAppId(),
        this.config.getAppPrivateKey(),
        this.loginApiClient
    );
  }
}
