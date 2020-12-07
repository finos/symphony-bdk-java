package com.symphony.bdk.core.retry;


import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.List;
import java.util.function.Predicate;

/**
 * Abstract class to implement a retry mechanism with recovery strategies,
 * e.g. refresh a session in a case of session expiration.
 *
 * @param <T> the type of the object to be eventually returned by the {@link #supplier}
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public abstract class RetryWithRecovery<T> {
  private SupplierWithApiException<T> supplier;
  private Predicate<Exception> ignoreException;
  private List<RecoveryStrategy> recoveryStrategies;

  /**
   * This is a helper function designed to cover most of the retry cases.
   * It retries on the conditions defined by {@link RetryWithRecoveryBuilder#isNetworkOrMinorError}
   * and refreshes the authSession if we get an unauthorized error.
   *
   * @param baseRetryBuilder the {@link RetryWithRecoveryBuilder} containing the base settings for the retry mechanism.
   * @param name             the name of the retry, can be any string but should specific to the function being retried.
   * @param supplier         the supplier returning the desired object which may fail with an exception.
   * @param <T>              the type of the object to be returned by the supplier.
   * @return the object returned by the supplier
   * @throws ApiRuntimeException if a non-handled {@link ApiException} thrown or if the max number of retries has been reached.
   * @throws RuntimeException    if any other exception thrown.
   */
  public static <T> T executeAndRetry(RetryWithRecoveryBuilder baseRetryBuilder, String name,
      SupplierWithApiException<T> supplier) {
    RetryWithRecovery<T> retry = RetryWithRecoveryBuilder.<T>from(baseRetryBuilder)
        .name(name)
        .supplier(supplier)
        .build();

    try {
      return retry.execute();
    } catch (ApiException e) {
      throw new ApiRuntimeException(e);
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  public RetryWithRecovery(SupplierWithApiException<T> supplier, Predicate<Exception> ignoreException,
      List<RecoveryStrategy> recoveryStrategies) {
    this.supplier = supplier;
    this.ignoreException = ignoreException;
    this.recoveryStrategies = recoveryStrategies;
  }

  /**
   * Method called by client which should implement the retry.
   * This should call {@link #executeOnce()} which executes one actual call to the supplier, runs potential recovery
   * actions and potentially throws an exception.
   *
   * @return the object returned by the supplier.
   * @throws Throwable in case the max number of retries exhausted
   *                   or if any other exception thrown by the supplier or the recovery functions.
   */
  public abstract T execute() throws Throwable;

  /**
   * This implements the logic corresponding to one retry:
   * calls the {@link #supplier}, catches the potential {@link Exception},
   * return null if it satisfies {@link #ignoreException}
   * and runs the recovery functions if it matches its corresponding condition.
   * This should be called by any implementation of {@link #execute()}.
   *
   * @return the object returned by the {@link #supplier}.
   * @throws Throwable in case an exception has been thrown by the {@link #supplier} or by the recovery functions.
   */
  protected T executeOnce() throws Throwable {
    try {
      return supplier.get();
    } catch (Exception e) {
      if (ignoreException.test(e)) {
        log.debug("{} ignored: {}", e.getClass().getCanonicalName(), e.getMessage());
        return null;
      }

      handleRecovery(e);
      throw e;
    }
  }

  private void handleRecovery(Exception e) throws Throwable {
    boolean recoveryTriggered = false;

    for (RecoveryStrategy recoveryStrategy : recoveryStrategies) {
      if (recoveryStrategy.matches(e)) {
        log.debug("Exception recovered", e);
        recoveryTriggered = true;
        recoveryStrategy.runRecovery();
      }
    }

    if (!recoveryTriggered) {
      log.error("Exception of type {} not recovered: {}", e.getClass().getCanonicalName(), e.getMessage());
    }
  }
}
