package com.symphony.bdk.core.service;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.model.UserV2;

import lombok.RequiredArgsConstructor;

/**
 * TODO: add description here
 */
@RequiredArgsConstructor
public class SessionService {

  private final SessionApi sessionApi;

  /**
   * Retrieves the {@link UserV2} session from the pod using an {@link AuthSession} holder.
   *
   * @param authSession Authentication session holder.
   * @return Bot session info.
   */
  public UserV2 getSession(AuthSession authSession) {
    try {
      return this.sessionApi.v2SessioninfoGet(authSession.getSessionToken()) ;
    } catch (ApiException ex) {
      throw new ApiRuntimeException(ex);
    }
  }
}
