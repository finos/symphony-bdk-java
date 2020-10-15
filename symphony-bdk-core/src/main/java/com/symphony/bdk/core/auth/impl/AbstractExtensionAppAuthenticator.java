package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.ExtensionAppTokensRepository;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.gen.api.model.ExtensionAppTokens;
import com.symphony.bdk.gen.api.model.PodCertificate;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.Optional;

/**
 * Abstract class to factorize the {@link ExtensionAppAuthenticator} logic between RSA and certificate,
 * especially the retry logic on top of HTTP calls.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public abstract class AbstractExtensionAppAuthenticator implements ExtensionAppAuthenticator {

  protected final String appId;
  protected final ExtensionAppTokensRepository tokensRepository;
  private AuthenticationRetry<ExtensionAppTokens> authenticationRetry;
  private RetryWithRecoveryBuilder<PodCertificate> podCertificateRetryBuilder;

  public AbstractExtensionAppAuthenticator(BdkRetryConfig retryConfig, String appId) {
    this(retryConfig, appId, new InMemoryTokensRepository());
  }

  public AbstractExtensionAppAuthenticator(BdkRetryConfig retryConfig, String appId,
      ExtensionAppTokensRepository tokensRepository) {
    this.appId = appId;
    this.tokensRepository = tokensRepository;
    this.authenticationRetry = new AuthenticationRetry<>(retryConfig);
    this.podCertificateRetryBuilder = AuthenticationRetry.getBaseRetryBuilder(retryConfig);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validateTokens(String appToken, String symphonyToken) {
    final Optional<String> storedSymphonyToken = tokensRepository.get(appToken);
    return storedSymphonyToken.isPresent() && storedSymphonyToken.get().equals(symphonyToken);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PodCertificate getPodCertificate() {
    return RetryWithRecovery.executeAndRetry(podCertificateRetryBuilder,
        "AbstractExtensionAppAuthenticator.getPodCertificate", this::callGetPodCertificate);
  }

  protected ExtensionAppTokens retrieveExtensionAppSession(String appToken) throws AuthUnauthorizedException {
    log.debug("Start authenticating extension app with id : {} ...", appId);

    final ExtensionAppTokens extensionAppTokens = retrieveExtAppTokens(appToken);
    log.debug("App with ID '{}' successfully authenticated.", appId);
    tokensRepository.save(appToken, extensionAppTokens.getSymphonyToken());
    return extensionAppTokens;
  }

  protected ExtensionAppTokens retrieveExtAppTokens(String appToken) throws AuthUnauthorizedException {
    final String unauthorizedErrorMessage = "Unable to authenticate app with ID : " + appId + ". "
        + "It usually happens when the app has not been configured or is not activated.";

    return authenticationRetry.executeAndRetry("AbstractExtensionAppAuthenticator.retrieveExtAppTokens",
        () -> authenticateAndRetrieveTokens(appToken), unauthorizedErrorMessage);
  }

  protected abstract PodCertificate callGetPodCertificate() throws ApiException;

  protected abstract ExtensionAppTokens authenticateAndRetrieveTokens(String appToken) throws ApiException;
}
