package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.impl.BotAuthenticatorRSAImpl;
import com.symphony.bdk.core.auth.impl.OboAuthenticatorRSAImpl;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.config.model.BdkConfig;

import org.apache.commons.io.IOUtils;
import org.apiguardian.api.API;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

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
  public @Nonnull BotAuthenticator getBotAuthenticator() throws AuthInitializationException {

    return new BotAuthenticatorRSAImpl(
        this.config.getBot().getUsername(),
        loadPrivateKeyFromPath(this.config.getBot().getPrivateKeyPath()),
        this.loginApiClient,
        this.relayApiClient
    );
  }

  /**
   * Creates a new instance of an {@link OboAuthenticator} service.
   *
   * @return a new {@link OboAuthenticator} instance.
   */
  public @Nonnull OboAuthenticator getOboAuthenticator() throws AuthInitializationException {

    return new OboAuthenticatorRSAImpl(
        this.config.getApp().getAppId(),
        loadPrivateKeyFromPath(this.config.getApp().getPrivateKeyPath()),
        this.loginApiClient
    );
  }

  private static PrivateKey loadPrivateKeyFromPath(String privateKeyPath) throws AuthInitializationException {
    try {
      return JwtHelper.parseRSAPrivateKey(IOUtils.toString(new FileInputStream(privateKeyPath), StandardCharsets.UTF_8));
    } catch (GeneralSecurityException e) {
      throw new AuthInitializationException("Unable to parse RSA Private Key located at " + privateKeyPath, e);
    } catch (IOException e) {
      throw new AuthInitializationException("Unable to read or find RSA Private Key from path " + privateKeyPath, e);
    }
  }
}
