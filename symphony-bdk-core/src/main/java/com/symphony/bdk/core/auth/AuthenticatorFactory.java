package com.symphony.bdk.core.auth;

import static com.symphony.bdk.core.util.DeprecationLogger.logDeprecation;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.impl.BotAuthenticatorCertImpl;
import com.symphony.bdk.core.auth.impl.BotAuthenticatorRsaImpl;
import com.symphony.bdk.core.auth.impl.ExtensionAppAuthenticatorCertImpl;
import com.symphony.bdk.core.auth.impl.ExtensionAppAuthenticatorRsaImpl;
import com.symphony.bdk.core.auth.impl.InMemoryTokensRepository;
import com.symphony.bdk.core.auth.impl.OboAuthenticatorCertImpl;
import com.symphony.bdk.core.auth.impl.OboAuthenticatorRsaImpl;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkAuthenticationConfig;
import com.symphony.bdk.core.config.model.BdkConfig;

import com.symphony.bdk.core.service.version.AgentVersionService;

import com.symphony.bdk.gen.api.SignalsApi;

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
  private final ExtensionAppTokensRepository extensionAppTokensRepository;

  public AuthenticatorFactory(@Nonnull BdkConfig bdkConfig, @Nonnull ApiClientFactory apiClientFactory) {
    this(bdkConfig, apiClientFactory, new InMemoryTokensRepository());
  }

  public AuthenticatorFactory(@Nonnull BdkConfig bdkConfig, @Nonnull ApiClientFactory apiClientFactory,
      @Nonnull ExtensionAppTokensRepository extensionAppTokensRepository) {
    this.config = bdkConfig;
    this.apiClientFactory = apiClientFactory;
    this.extensionAppTokensRepository = extensionAppTokensRepository;
  }

  /**
   * Creates a new instance of a {@link BotAuthenticator} service.
   *
   * @return a new {@link BotAuthenticator} instance.
   */
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
          this.config.getCommonJwt(),
          this.apiClientFactory.getLoginClient(),
          this.apiClientFactory.getSessionAuthClient(),
          this.apiClientFactory.getKeyAuthClient(),
          new AgentVersionService(new SignalsApi(this.apiClientFactory.getAgentClient()))
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
          this.config.getCommonJwt(),
          this.loadPrivateKeyFromAuthenticationConfig(this.config.getBot()),
          this.apiClientFactory.getLoginClient(),
          this.apiClientFactory.getRelayClient(),
          new AgentVersionService(new SignalsApi(this.apiClientFactory.getAgentClient()))
      );
    }
    throw new AuthInitializationException("Neither RSA private key nor certificate is configured.");
  }

  /**
   * Creates a new instance of an {@link OboAuthenticator} service.
   *
   * @return a new {@link OboAuthenticator} instance.
   */
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
   * Creates a new instance of an {@link ExtensionAppAuthenticator} service.
   *
   * @return a new {@link ExtensionAppAuthenticator} instance.
   */
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
          this.apiClientFactory.getExtAppSessionAuthClient(),
          extensionAppTokensRepository);
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
          this.apiClientFactory.getPodClient(),
          extensionAppTokensRepository
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
      is = AuthenticatorFactory.class.getResourceAsStream(privateKeyPath.replace("classpath:", ""));
      if (is == null) {
        throw new AuthInitializationException(
            "Unable to find RSA private key as classpath resource from: " + privateKeyPath);
      }
      try (InputStream resourceStream = is) {
        return IOUtils.toString(resourceStream, StandardCharsets.UTF_8);
      }
    } else {
      try (InputStream fileStream = new FileInputStream(privateKeyPath)) {
        return IOUtils.toString(fileStream, StandardCharsets.UTF_8);
      }
    }
  }
}
