package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.impl.BotAuthenticatorCertImpl;
import com.symphony.bdk.core.auth.impl.BotAuthenticatorRsaImpl;
import com.symphony.bdk.core.auth.impl.ExtensionAppAuthenticatorCertImpl;
import com.symphony.bdk.core.auth.impl.ExtensionAppAuthenticatorRsaImpl;
import com.symphony.bdk.core.auth.impl.OboAuthenticatorCertImpl;
import com.symphony.bdk.core.auth.impl.OboAuthenticatorRsaImpl;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apiguardian.api.API;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
@Slf4j
@API(status = API.Status.STABLE)
public class AuthenticatorFactory {

  private final BdkConfig config;
  private final ApiClientFactory apiClientFactory;

  private final JwtHelper jwtHelper = new JwtHelper();

  public AuthenticatorFactory(@Nonnull BdkConfig bdkConfig, @Nonnull ApiClientFactory apiClientFactory) {
    this.config = bdkConfig;
    this.apiClientFactory = apiClientFactory;
  }

  /**
   * Creates a new instance of a {@link BotAuthenticator} service.
   *
   * @return a new {@link BotAuthenticator} instance.
   */
  public @Nonnull
  BotAuthenticator getBotAuthenticator() throws AuthInitializationException {
    if (this.config.getBot().isCertificateAuthenticationConfigured()) {
      return new BotAuthenticatorCertImpl(
          this.apiClientFactory.getSessionAuthClient(),
          this.apiClientFactory.getKeyAuthClient()
      );
    }
    return new BotAuthenticatorRsaImpl(
        this.config.getBot().getUsername(),
        this.loadPrivateKeyFromPath(this.config.getBot().getPrivateKeyPath()),
        this.apiClientFactory.getLoginClient(),
        this.apiClientFactory.getRelayClient()
    );
  }

  /**
   * Creates a new instance of an {@link OboAuthenticator} service.
   *
   * @return a new {@link OboAuthenticator} instance.
   */
  public @Nonnull
  OboAuthenticator getOboAuthenticator() throws AuthInitializationException {
    if (this.config.getApp().isCertificateAuthenticationConfigured()) {
      return new OboAuthenticatorCertImpl(
          this.config.getApp().getAppId(),
          this.apiClientFactory.getExtAppSessionAuthClient()
      );
    }
    return new OboAuthenticatorRsaImpl(
        this.config.getApp().getAppId(),
        this.loadPrivateKeyFromPath(this.config.getApp().getPrivateKeyPath()),
        this.apiClientFactory.getLoginClient()
    );
  }

  /**
   * Creates a new instance of an {@link ExtensionAppAuthenticator} service.
   *
   * @return a new {@link ExtensionAppAuthenticator} instance.
   */
  public @Nonnull
  ExtensionAppAuthenticator getExtensionAppAuthenticator() throws AuthInitializationException {
    if (this.config.getApp().isCertificateAuthenticationConfigured()) {
      return new ExtensionAppAuthenticatorCertImpl(
          this.config.getApp().getAppId(),
          this.apiClientFactory.getExtAppSessionAuthClient());
    }
    return new ExtensionAppAuthenticatorRsaImpl(
        this.config.getApp().getAppId(),
        this.loadPrivateKeyFromPath(this.config.getApp().getPrivateKeyPath()),
        this.apiClientFactory.getLoginClient(),
        this.apiClientFactory.getPodClient()
    );
  }

  private PrivateKey loadPrivateKeyFromPath(String privateKeyPath) throws AuthInitializationException {
    log.debug("Loading RSA privateKey from path : {}", privateKeyPath);
    try {
      return this.jwtHelper.parseRsaPrivateKey(loadPrivateKey(privateKeyPath));
    } catch (GeneralSecurityException e) {
      final String message = "Unable to parse RSA Private Key located at " + privateKeyPath;
      log.error(message, e);
      throw new AuthInitializationException(message, e);
    } catch (IOException e) {
      final String message = "Unable to read or find RSA Private Key from path " + privateKeyPath;
      log.error(message, e);
      throw new AuthInitializationException(message, e);
    }
  }

  private static String loadPrivateKey(String privateKeyPath) throws IOException {
    log.debug("Loading private key from : {}", privateKeyPath);
    InputStream is;

    // useful for testing when private key is located into resources
    if (privateKeyPath.startsWith("classpath:")) {
      log.warn("Warning: Keeping RSA private keys into project resources is dangerous. "
          + "You should consider another location for production.");
      is = AuthenticatorFactory.class.getResourceAsStream(privateKeyPath.replace("classpath:", ""));
    } else {
      is = new FileInputStream(privateKeyPath);
    }

    return IOUtils.toString(is, StandardCharsets.UTF_8);
  }
}
