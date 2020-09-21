package com.symphony.bdk.core.service;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.util.function.RetryWithRecovery;
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
  private final BdkRetryConfig retryConfig;

  /**
   * Retrieves the {@link UserV2} session from the pod using an {@link AuthSession} holder.
   *
   * @param authSession Authentication session holder.
   * @return Bot session info.
   */
  public UserV2 getSession(AuthSession authSession) {
    return RetryWithRecovery.executeAndRetry("getSession",
        () -> sessionApi.v2SessioninfoGet(authSession.getSessionToken()), retryConfig, authSession);
  }
}
