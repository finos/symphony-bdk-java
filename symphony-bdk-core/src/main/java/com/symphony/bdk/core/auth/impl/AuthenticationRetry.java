package com.symphony.bdk.core.auth.impl;

import static com.symphony.bdk.core.retry.RetryWithRecovery.networkIssueMessageError;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import javax.ws.rs.ProcessingException;

/**
 * Class used to implement the specific logic for authentication calls.
 * Delegates the retry mechanism to {@link RetryWithRecovery}.
 *
 * @param <T> the type returned by the authentication call.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
class AuthenticationRetry<T> {

  private RetryWithRecoveryBuilder<T> baseRetryBuilder;

  /**
   * Creates a {@link RetryWithRecoveryBuilder} with the retry condition specific to authentication calls.
   *
   * @param retryConfig the retry configuration to be used.
   * @param <T> the type returned by the authentication call.
   * @return a {@link RetryWithRecoveryBuilder} instance initialized with the provided retry configuration
   * and the retry condition {@link #canAuthenticationBeRetried(Throwable)}.
   */
  public static <T> RetryWithRecoveryBuilder<T> getBaseRetryBuilder(BdkRetryConfig retryConfig) {
    return new RetryWithRecoveryBuilder<T>()
        .retryConfig(retryConfig)
        .retryOnException(AuthenticationRetry::canAuthenticationBeRetried);
  }

  /**
   * Predicate to check if authentication call be retried,
   * i.e. the throwable is a {@link ProcessingException} (e.g. network issues)
   * or is an {@link ApiException} with status code 429 or strictly greater than 500.
   *
   * @param t the {@link Throwable} to be checked.
   * @return true if call should be retried.
   */
  public static boolean canAuthenticationBeRetried(Throwable t) {
    if (t instanceof ApiException) {
      ApiException apiException = (ApiException) t;
      return apiException.isServerError() || apiException.isTooManyRequestsError();
    }
    return t instanceof ProcessingException;
  }

  public AuthenticationRetry(BdkRetryConfig retryConfig) {
    baseRetryBuilder = getBaseRetryBuilder(retryConfig);
  }

  /**
   * Executes and retries the authentication call.
   * If the call throws an {@link ApiException} with 401 unauthorized, an {@link AuthUnauthorizedException} is thrown,
   * for other status codes an {@link ApiRuntimeException} is thrown.
   * In case of other exceptions, a {@link RuntimeException} is thrown.
   *
   * @param name the name of the retry, can be any string but should specific to the function being retried.
   * @param supplier the authentication API call
   * @param unauthorizedErrorMessage the message put in the {@link AuthUnauthorizedException} in case of unauthorized error.
   * @return output of the call in case of success.
   * @throws AuthUnauthorizedException in case of unauthorized error.
   */
  public T executeAndRetry(String name, String address, SupplierWithApiException<T> supplier, String unauthorizedErrorMessage)
      throws AuthUnauthorizedException {
    final RetryWithRecovery<T> retry = RetryWithRecoveryBuilder.<T>from(baseRetryBuilder)
        .name(name)
        .basePath(address)
        .supplier(supplier).build();

    try {
      return retry.execute();
    } catch (ApiException e) {
      if (e.isUnauthorized()) {
        throw new AuthUnauthorizedException(unauthorizedErrorMessage, e);
      }
      throw new ApiRuntimeException(e);
    } catch (Throwable t) {
      throw new RuntimeException(networkIssueMessageError(t,address), t);
    }
  }

}
