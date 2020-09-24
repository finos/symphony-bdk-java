package com.symphony.bdk.core.service;

import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.model.UserV2;

import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;

/**
 * {@link SessionApi} wrapper service.
 */
@RequiredArgsConstructor
@API(status = API.Status.EXPERIMENTAL)
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
