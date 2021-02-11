package com.symphony.bdk.core.auth.impl;

import static com.symphony.bdk.core.util.DeprecationLogger.logDeprecation;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import com.symphony.bdk.core.auth.BotAuthenticator;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkAuthenticationConfig;
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
 * Implementation of authentication factory class that provides new instances for the main authenticators :
 * <ul>
 *   <li>{@link BotAuthenticator} : to authenticate the main Bot service account</li>
 *   <li>{@link OboAuthenticator} : to perform on-behalf-of authentication</li>
 * </ul>
 */
@Slf4j
@API(status = API.Status.STABLE)
public class AuthenticatorFactoryImpl implements AuthenticatorFactory {

  private final BdkConfig config;
  private final ApiClientFactory apiClientFactory;

  public AuthenticatorFactoryImpl(@Nonnull BdkConfig bdkConfig, @Nonnull ApiClientFactory apiClientFactory) {
    this.config = bdkConfig;
    this.apiClientFactory = apiClientFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nonnull
  BotAuthenticator getBotAuthenticator() throws AuthInitializationException {
    if (this.config.getBot().isBothCertificateAndRsaConfigured()) {
      throw new AuthInitializationException(
          "Both of certificate and rsa authentication are configured. Only one of them should be provided.");
    }
    if (this.config.getBot().isCertificateAuthenticationConfigured()) {
      if (!this.config.getBot().isCertificateConfigurationValid()) {
        throw new AuthInitializationException(
            "Only one of certificate path or content should be configured for bot authentication.");
      }
      return new BotAuthenticatorCertImpl(
          this.config.getRetry(),
          this.config.getBot().getUsername(),
          this.apiClientFactory.getSessionAuthClient(),
          this.apiClientFactory.getKeyAuthClient()
      );
    }
    if (this.config.getBot().isRsaAuthenticationConfigured()) {
      if (!this.config.getBot().isRsaConfigurationValid()) {
        throw new AuthInitializationException(
            "Only one of private key path or content should be configured for bot authentication.");
      }
      return new BotAuthenticatorRsaImpl(
          this.config.getRetry(),
          this.config.getBot().getUsername(),
          this.loadPrivateKeyFromAuthenticationConfig(this.config.getBot()),
          this.apiClientFactory.getLoginClient(),
          this.apiClientFactory.getRelayClient()
      );
    }
    throw new AuthInitializationException("Neither RSA private key nor certificate is configured.");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nonnull
  OboAuthenticator getOboAuthenticator() throws AuthInitializationException {
    if (this.config.getApp().isBothCertificateAndRsaConfigured()) {
      throw new AuthInitializationException(
          "Both of certificate and rsa authentication are configured. Only one of them should be provided.");
    }
    if (this.config.getApp().isCertificateAuthenticationConfigured()) {
      if (!this.config.getApp().isCertificateConfigurationValid()) {
        throw new AuthInitializationException(
            "Only one of certificate path or content should be configured for app authentication.");
      }
      return new OboAuthenticatorCertImpl(
          this.config.getRetry(),
          this.config.getApp().getAppId(),
          this.apiClientFactory.getExtAppSessionAuthClient()
      );
    }
    if (this.config.getApp().isRsaAuthenticationConfigured()) {
      if (!this.config.getApp().isRsaConfigurationValid()) {
        throw new AuthInitializationException(
            "Only one of private key path or content should be configured for app authentication.");
      }
      return new OboAuthenticatorRsaImpl(
          this.config.getRetry(),
          this.config.getApp().getAppId(),
          this.loadPrivateKeyFromAuthenticationConfig(this.config.getApp()),
          this.apiClientFactory.getLoginClient()
      );
    }
    throw new AuthInitializationException("Neither RSA private key nor certificate is configured.");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nonnull
  ExtensionAppAuthenticator getExtensionAppAuthenticator() throws AuthInitializationException {
    if (this.config.getApp().isBothCertificateAndRsaConfigured()) {
      throw new AuthInitializationException(
          "Both of certificate and rsa authentication are configured. Only one of them should be provided.");
    }
    if (this.config.getApp().isCertificateAuthenticationConfigured()) {
      if (!this.config.getApp().isCertificateConfigurationValid()) {
        throw new AuthInitializationException(
            "Only one of certificate path or content should be configured for app authentication.");
      }
      return new ExtensionAppAuthenticatorCertImpl(
          this.config.getRetry(),
          this.config.getApp().getAppId(),
          this.apiClientFactory.getExtAppSessionAuthClient());
    }
    if (this.config.getApp().isRsaAuthenticationConfigured()) {
      if (!this.config.getApp().isRsaConfigurationValid()) {
        throw new AuthInitializationException(
            "Only one of private key path or content should be configured for app authentication.");
      }
      return new ExtensionAppAuthenticatorRsaImpl(
          this.config.getRetry(),
          this.config.getApp().getAppId(),
          this.loadPrivateKeyFromAuthenticationConfig(this.config.getApp()),
          this.apiClientFactory.getLoginClient(),
          this.apiClientFactory.getPodClient()
      );
    }
    throw new AuthInitializationException("Neither RSA private key nor certificate is configured.");
  }

  private PrivateKey loadPrivateKeyFromAuthenticationConfig(BdkAuthenticationConfig config)
      throws AuthInitializationException {
    String privateKeyPath = "";
    try {
      String privateKey;
      if (config.getPrivateKey() != null && config.getPrivateKey().isConfigured()) {
        if (isNotEmpty(config.getPrivateKey().getContent())) {
          privateKey = new String(config.getPrivateKey().getContent(), StandardCharsets.UTF_8);
        } else {
          privateKeyPath = config.getPrivateKey().getPath();
          log.debug("Loading RSA privateKey from path : {}", privateKeyPath);
          privateKey = loadPrivateKey(privateKeyPath);
        }
      } else {
        logDeprecation("RSA private key should be configured under \"privateKey\" field");
        if (isNotEmpty(config.getPrivateKeyContent())) {
          privateKey = new String(config.getPrivateKeyContent(), StandardCharsets.UTF_8);
        } else {
          privateKeyPath = config.getPrivateKeyPath();
          log.debug("Loading RSA privateKey from path : {}", privateKeyPath);
          privateKey = loadPrivateKey(privateKeyPath);
        }
      }
      return JwtHelper.parseRsaPrivateKey(privateKey);
    } catch (GeneralSecurityException e) {
      final String message = String.format("Unable to parse RSA Private Key from path %s. Check if the format is "
          + "correct.", privateKeyPath);
      throw new AuthInitializationException(message, e);
    } catch (IOException e) {
      final String message = "Unable to read or find RSA Private Key from path " + privateKeyPath;
      throw new AuthInitializationException(message, e);
    }
  }

  private static String loadPrivateKey(String privateKeyPath) throws IOException, AuthInitializationException {
    InputStream is;

    // useful for testing when private key is located into resources
    if (privateKeyPath.startsWith("classpath:")) {
      log.warn("Warning: Keeping RSA private keys into project resources is dangerous. "
          + "You should consider another location for production.");
      is = AuthenticatorFactoryImpl.class.getResourceAsStream(privateKeyPath.replace("classpath:", ""));
      if (is == null) {
        throw new AuthInitializationException("Unable to find RSA private key as classpath resource from: " + privateKeyPath);
      }
    } else {
      is = new FileInputStream(privateKeyPath);
    }

    return IOUtils.toString(is, StandardCharsets.UTF_8);
  }
}
