package com.symphony.bdk.core.service.disclaimer;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.DisclaimerApi;
import com.symphony.bdk.gen.api.model.Disclaimer;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * Service class for managing disclaimers.
 * <p>
 * This service performs some actions related to
 * disclaimers like:
 * <p><ul>
 * <li>GET details of a disclaimer given by its id</li>
 * <li>GET the list of all disclaimers for a given pod</li>
 * <li>Get the list of all users for a given disclaimer</li>
 * </ul></p>
 */
@Slf4j
@API(status = API.Status.STABLE)
public class DisclaimerService {

  private final DisclaimerApi disclaimerApi;
  private final AuthSession authSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public DisclaimerService(DisclaimerApi disclaimerApi, AuthSession authSession, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.disclaimerApi = disclaimerApi;
    this.authSession = authSession;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
  }

  /**
   * Returns disclaimer's details by Id
   *
   * @param disclaimerId  Disclaimer id to get details
   * @return  {@link Disclaimer} with disclaimer details
   * @see <a href="https://developers.symphony.com/restapi/reference#disclaimer">Disclaimer</a>
   */
  public Disclaimer getDisclaimer(@Nonnull String disclaimerId) {
    return executeAndRetry("getDisclaimer",
        () -> this.disclaimerApi.v1AdminDisclaimerDidGet(this.authSession.getSessionToken(), disclaimerId));
  }

  /**
   * Lists all disclaimers for a given pod
   *
   * @return {@link List<Disclaimer>} with list of all pod's disclaimers
   * @see <a href="https://developers.symphony.com/restapi/reference#list-disclaimers">List Disclaimers</a>
   */
  public List<Disclaimer> listDisclaimers() {
    return executeAndRetry("listPodDisclaimers",
        () -> this.disclaimerApi.v1AdminDisclaimerListGet(this.authSession.getSessionToken()));
  }

  /**
   * Lists all users for a given disclaimer
   *
   * @param disclaimerId  disclaimer id to which return users
   * @return  {@link List} of users for the given disclaimer
   * @see <a href="https://developers.symphony.com/restapi/reference#disclaimer-users">List Disclaimer Users</a>
   */
  public List<Long> listDisclaimerUsers(@Nonnull String disclaimerId) {
    return executeAndRetry("getDisclaimer",
        () -> this.disclaimerApi.v1AdminDisclaimerDidUsersGet(this.authSession.getSessionToken(), disclaimerId));
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    return RetryWithRecovery.executeAndRetry(retryBuilder, name, supplier);
  }

}
