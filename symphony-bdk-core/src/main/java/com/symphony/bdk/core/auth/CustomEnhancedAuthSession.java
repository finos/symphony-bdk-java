package com.symphony.bdk.core.auth;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import javax.annotation.Nullable;

@API(status = Status.EXPERIMENTAL)
public interface CustomEnhancedAuthSession extends AuthSession {

  @Override
  default String sessionId() {
    return "enhancedSession";
  }

  @Nullable
  String getEnhancedAuthToken();
}
