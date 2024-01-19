package com.symphony.bdk.core.auth;

import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import javax.annotation.Nullable;

@API(status = Status.EXPERIMENTAL)
public interface CustomEnhancedAuthSession extends AuthSession {

  boolean isSessionExpired(ApiException exception);

  @Nullable
  String getEnhancedAuthToken();
}
