package com.symphony.bdk.core.service.session;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.OboService;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.http.api.ApiException;

import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;

/**
 * Service class for exposing endpoints to get user session information.
 */
@RequiredArgsConstructor
@API(status = API.Status.STABLE)
public class SessionService implements OboSessionService, OboService<OboSessionService> {

  private final SessionApi sessionApi;
  private final AuthSession authSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  @Override
  public OboSessionService obo(AuthSession oboSession) {
    return new SessionService(sessionApi, oboSession, retryBuilder);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserV2 getSession() {
    return executeAndRetry("getSession",
        () -> sessionApi.v2SessioninfoGet(authSession.getSessionToken()), authSession);
  }

  protected <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier, AuthSession authSession) {
    final RetryWithRecoveryBuilder<?> retryBuilderWithAuthSession = RetryWithRecoveryBuilder.from(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
    return RetryWithRecovery.executeAndRetry(retryBuilderWithAuthSession, name, supplier);
  }
}
