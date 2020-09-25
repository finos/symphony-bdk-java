package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.ExtensionAppTokensRepository;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.gen.api.model.ExtensionAppTokens;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Abstract class to factorize the {@link ExtensionAppAuthenticator} logic between RSA and certificate.
 */
@Slf4j
public abstract class AbstractExtensionAppAuthenticator implements ExtensionAppAuthenticator {
  protected final String appId;
  protected final ExtensionAppTokensRepository tokensRepository;

  public AbstractExtensionAppAuthenticator(String appId) {
    this(appId, new InMemoryTokensRepository());
  }

  public AbstractExtensionAppAuthenticator(String appId, ExtensionAppTokensRepository tokensRepository) {
    this.appId = appId;
    this.tokensRepository = tokensRepository;
  }

  protected abstract ExtensionAppTokens retrieveExtAppTokens(String appToken) throws ApiException;

  protected ExtensionAppTokens retrieveExtensionAppSession(String appToken) throws AuthUnauthorizedException {
    log.debug("Start authenticating extension app with id : {} ...", this.appId);

    try {
      final ExtensionAppTokens extensionAppTokens = retrieveExtAppTokens(appToken);
      log.debug("App with ID '{}' successfully authenticated.", this.appId);
      tokensRepository.save(appToken, extensionAppTokens.getSymphonyToken());
      return extensionAppTokens;
    } catch (ApiException e) {
      if (e.isUnauthorized()) {
        throw new AuthUnauthorizedException("Unable to authenticate app with ID : " + this.appId + ". "
            + "It usually happens when the app has not been configured or is not activated.", e);
      } else {
        throw new ApiRuntimeException(e);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validateTokens(String appToken, String symphonyToken) {
    final Optional<String> storedSymphonyToken = tokensRepository.get(appToken);
    return storedSymphonyToken.isPresent() && storedSymphonyToken.get().equals(symphonyToken);
  }
}
