package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.CustomEnhancedAuthAuthenticator;
import com.symphony.bdk.core.auth.CustomEnhancedAuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.http.api.ApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import javax.annotation.Nullable;

@Slf4j
@RequiredArgsConstructor
@API(status = Status.EXPERIMENTAL)
public class EnhancedAuthSession implements CustomEnhancedAuthSession {

  private final CustomEnhancedAuthAuthenticator authAuthenticator;

  private String authToken;

  @Override
  public boolean isSessionExpired(ApiException exception) {
    return authAuthenticator.isAuthTokenExpired(exception);
  }

  @Override
  public void refresh() throws AuthUnauthorizedException {
    this.authToken = authAuthenticator.authenticate();
  }

  @Nullable
  @Override
  public String getEnhancedAuthToken() {
    return this.authToken;
  }
}
