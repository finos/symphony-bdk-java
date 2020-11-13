package com.symphony.bdk.core.service.session;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.http.api.ApiException;

import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;

/**
 * {@link SessionApi} wrapper service.
 */
@RequiredArgsConstructor
@API(status = API.Status.EXPERIMENTAL)
public class SessionService {

  private final SessionApi sessionApi;
  private final RetryWithRecoveryBuilder retryBuilder;

  /**
   * Retrieves the {@link UserV2} session from the pod using an {@link AuthSession} holder.
   *
   * @param authSession Authentication session holder.
   * @return Bot session info.
   */
  public UserV2 getSession(AuthSession authSession) {
    return executeAndRetry("getSession",
        () -> sessionApi.v2SessioninfoGet(authSession.getSessionToken()), authSession);
  }

  protected <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier, AuthSession authSession) {
    final RetryWithRecoveryBuilder retryBuilderWithAuthSession = RetryWithRecoveryBuilder.from(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
    return RetryWithRecovery.executeAndRetry(retryBuilderWithAuthSession, name, supplier);
  }
}
