package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.http.api.ApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

@Slf4j
@RequiredArgsConstructor
@API(status = API.Status.INTERNAL)
public
class OAuthSession {

  private final AuthSession authSession;

  public String getBearerToken() throws ApiException {
    try {
      return authSession.getAuthorizationToken();
    } catch (AuthUnauthorizedException e) {
      throw new ApiException(401, e);
    }
  }
}
