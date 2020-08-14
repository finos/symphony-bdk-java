package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.BotAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.gen.api.CertificateAuthenticationApi;
import com.symphony.bdk.gen.api.model.Token;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

/**
 * Bot authenticator certificate implementation.
 *
 * @see <a href="https://developers.symphony.com/symphony-developer/docs/bot-authentication-workflow-1">Bot Authentication Workflow</a>
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class BotAuthenticatorCertImpl implements BotAuthenticator {

  private final ApiClient sessionAuthClient;
  private final ApiClient keyAuthClient;

  public BotAuthenticatorCertImpl(@NonNull ApiClient sessionAuthClient, @NonNull ApiClient keyAuthClient) {
    this.sessionAuthClient = sessionAuthClient;
    this.keyAuthClient = keyAuthClient;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AuthSession authenticateBot() {
    return new AuthSessionImpl(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @NonNull String retrieveSessionToken() throws AuthUnauthorizedException {
    return doRetrieveToken(this.sessionAuthClient);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @NonNull String retrieveKeyManagerToken() throws AuthUnauthorizedException {
    return doRetrieveToken(this.keyAuthClient);
  }

  private String doRetrieveToken(ApiClient client) throws AuthUnauthorizedException {
    try {
      final Token token = new CertificateAuthenticationApi(client).v1AuthenticatePost();
      log.debug("{} successfully retrieved.", token.getName());
      return token.getToken();
    } catch (ApiException ex) {
      if (ex.getCode() == 401) {
        // usually happens when the certificate is not correct
        throw new AuthUnauthorizedException("Service account is not authorized to authenticate using certificate. " +
            "Please check if certificate is correct.", ex);
      } else {
        // we don't know what to do, let's forward the ApiException
        throw new ApiRuntimeException(ex);
      }
    }
  }

}
