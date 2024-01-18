package com.symphony.bdk.core.service.session;

import com.symphony.bdk.core.auth.BotAuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.OboService;
import com.symphony.bdk.core.retry.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

/**
 * Service class for exposing endpoints to get user session information.
 */
@API(status = API.Status.STABLE)
public class SessionService implements OboSessionService, OboService<OboSessionService> {

  private final SessionApi sessionApi;
  private final BotAuthSession authSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public SessionService(SessionApi sessionApi, BotAuthSession authSession, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.sessionApi = sessionApi;
    this.authSession = authSession;
    this.retryBuilder = RetryWithRecoveryBuilder.from(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
  }

  public SessionService(SessionApi sessionApi, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.sessionApi = sessionApi;
    this.authSession = null;
    this.retryBuilder = RetryWithRecoveryBuilder.from(retryBuilder);
  }

  @Override
  public OboSessionService obo(BotAuthSession oboSession) {
    return new SessionService(sessionApi, oboSession, retryBuilder);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserV2 getSession() {
    return executeAndRetry("getSession",
        () -> sessionApi.v2SessioninfoGet(authSession.getSessionToken()));
  }

  protected <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    checkAuthSession(authSession);
    return RetryWithRecovery.executeAndRetry(retryBuilder, name, sessionApi.getApiClient().getBasePath(), supplier);
  }
}
