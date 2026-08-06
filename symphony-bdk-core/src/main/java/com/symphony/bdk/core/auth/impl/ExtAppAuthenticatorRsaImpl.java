package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.ExtAppAuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.config.model.BdkRetryConfig;

import com.symphony.bdk.gen.api.AuthenticationApi;
import com.symphony.bdk.gen.api.model.AuthenticateRequest;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;
import org.jetbrains.annotations.NotNull;

import java.security.PrivateKey;

@API(status = API.Status.INTERNAL)
public class ExtAppAuthenticatorRsaImpl extends AbstractExtAppAuthenticator {

  private final AuthenticationApi authenticationApi;
  private final PrivateKey appPrivateKey;

  public ExtAppAuthenticatorRsaImpl(BdkRetryConfig retryConfig,
      String appId,
      PrivateKey appPrivateKey,
      ApiClient loginApiClient) {
    super(retryConfig, appId);
    this.appPrivateKey = appPrivateKey;
    this.authenticationApi = new AuthenticationApi(loginApiClient);
  }

  @Override
  protected String authenticateAndRetrieveAppSessionToken() throws ApiException {
    final String jwt = JwtHelper.createSignedJwt(appId, JwtHelper.JWT_EXPIRATION_MILLIS, appPrivateKey);
    final AuthenticateRequest req = new AuthenticateRequest();
    req.setToken(jwt);

    return this.authenticationApi.pubkeyAppAuthenticatePost(req).getToken();
  }

  @Override
  protected String getBasePath() {
    return authenticationApi.getApiClient().getBasePath();
  }

  @NotNull
  @Override
  public ExtAppAuthSession authenticateExtApp() throws AuthUnauthorizedException {
    ExtAppAuthSessionImpl extAppAuthSession = new ExtAppAuthSessionImpl(this);
    extAppAuthSession.refresh();
    return extAppAuthSession;
  }
}
