package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.ExtAppAuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.gen.api.CertificateAuthenticationApi;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;
import org.jetbrains.annotations.NotNull;

@API(status = API.Status.INTERNAL)
public class ExtAppAuthenticatorCertImpl extends AbstractExtAppAuthenticator {
  private final CertificateAuthenticationApi authenticationApi;

  public ExtAppAuthenticatorCertImpl(BdkRetryConfig retryConfig,
      String appId,
      ApiClient loginApiClient) {
    super(retryConfig, appId);
    this.authenticationApi = new CertificateAuthenticationApi(loginApiClient);
  }

  @NotNull
  @Override
  public ExtAppAuthSession authenticateExtApp() throws AuthUnauthorizedException {
    ExtAppAuthSession session = new ExtAppAuthSessionCertImpl(this);
    session.refresh();
    return session;
  }

  @Override
  protected String authenticateAndRetrieveAppSessionToken() throws ApiException {
    return this.authenticationApi.v1AppAuthenticatePost().getToken();
  }

  @Override
  protected String getBasePath() {
    return authenticationApi.getApiClient().getBasePath();
  }
}
