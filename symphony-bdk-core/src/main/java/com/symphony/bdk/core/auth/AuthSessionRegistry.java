package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import java.util.HashMap;
import java.util.Map;

@API(status = Status.EXPERIMENTAL)
public class AuthSessionRegistry {
  private final Map<String, AuthSession> sessionRegistry = new HashMap<>();

  public void register(AuthSession authSession) {
    this.sessionRegistry.put(authSession.sessionId(), authSession);
  }

  public void refresh(ApiException exception) throws ApiException {
    for (AuthSession authSession : sessionRegistry.values()) {
      try {
        authSession.refresh();
      } catch (AuthUnauthorizedException e) {
        throw new ApiException(401, e);
      }
    }
  }

  public void refresh(String id) throws ApiException {
    try {
      sessionRegistry.get(id).refresh();
    } catch (AuthUnauthorizedException e) {
      throw new ApiException(401, e);
    }
  }
}
