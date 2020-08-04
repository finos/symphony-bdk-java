package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthenticationException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.gen.api.AuthenticationApi;
import com.symphony.bdk.gen.api.model.AuthenticateRequest;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.security.PrivateKey;

import javax.annotation.Nonnull;

/**
 *
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class OboAuthenticatorRSAImpl implements OboAuthenticator {

  private final AuthenticationApi authenticationApi;
  private final String appId;
  private final PrivateKey appPrivateKey;

  public OboAuthenticatorRSAImpl(ApiClient loginApiClient, String appId, PrivateKey appPrivateKey) {
    this.appId = appId;
    this.appPrivateKey = appPrivateKey;
    this.authenticationApi = new AuthenticationApi(loginApiClient);
  }

  @Override
  public AuthSession authenticateByUsername(@Nonnull String username) {
    return new AuthSessionOboImpl(this, username);
  }

  @Override
  public AuthSession authenticateByUserID(@Nonnull Long userId) {
    return new AuthSessionOboImpl(this, userId);
  }

  public String retrieveOboSessionTokenByUserID(@Nonnull Long userId) throws AuthenticationException {
    final String appSessionToken = this.retrieveAppSessionToken();
    try {
      return this.authenticationApi.pubkeyAppUserUserIdAuthenticatePost(appSessionToken, userId).getToken();
    } catch (ApiException e) {
      throw new AuthenticationException("Unable to authenticate user with ID : " + userId, e);
    }
  }

  public String retrieveOboSessionTokenByUsername(@Nonnull String username) throws AuthenticationException {
    final String appSessionToken = this.retrieveAppSessionToken();
    try {
      return this.authenticationApi.pubkeyAppUsernameUsernameAuthenticatePost(appSessionToken, username).getToken();
    } catch (ApiException e) {
      throw new AuthenticationException("Unable to authenticate user with username : " + username, e);
    }
  }

  private String retrieveAppSessionToken() throws AuthenticationException {
    log.debug("Start authenticating app with id : {} ...", this.appId);

    final String jwt = JwtHelper.createSignedJwt(this.appId, 30_000, this.appPrivateKey);
    final AuthenticateRequest req = new AuthenticateRequest();
    req.setToken(jwt);

    try {
      return this.authenticationApi.pubkeyAppAuthenticatePost(req).getToken();
    } catch (ApiException e) {
      throw new AuthenticationException("Unable to authenticate app with ID : " + this.appId, e);
    }
  }
}
