package com.symphony.bdk.core.util.function;

import com.symphony.bdk.core.api.invoker.ApiException;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Predicate;

@Slf4j
public abstract class RetryWithRecovery<T> {
  private SupplierWithApiException<T> supplier;
  private Predicate<ApiException> ignoreApiException;
  private Map<Predicate<ApiException>, ConsumerWithThrowable> recoveryStrategies;

  public RetryWithRecovery(SupplierWithApiException<T> supplier, Predicate<ApiException> ignoreApiException,
      Map<Predicate<ApiException>, ConsumerWithThrowable> recoveryStrategies) {
    this.supplier = supplier;
    this.ignoreApiException = ignoreApiException;
    this.recoveryStrategies = recoveryStrategies;
  }

  /**
   * Method called by client which should implement the retry.
   * This should call {@link #executeOnce()} which executes one actual call to the supplier, runs potential recovery
   * actions and potentially throws an exception.
   *
   * @return
   * @throws Throwable
   */
  public abstract T execute() throws Throwable;

  protected T executeOnce() throws Throwable {
    try {
      return supplier.get();
    } catch (ApiException e) {
      if (ignoreApiException.test(e)) {
        log.debug("Exception ignored: {}", e);
        return null;
      }

      handleRecovery(e);
      throw e;
    }
  }

  private void handleRecovery(ApiException e) throws Throwable {
    boolean recoveryTriggered = false;

    for (Map.Entry<Predicate<ApiException>, ConsumerWithThrowable> entry : recoveryStrategies.entrySet()) {
      if (entry.getKey().test(e)) {
        log.debug("Exception recovered: {}", e);
        recoveryTriggered = true;
        entry.getValue().consume();
      }
    }

    if (!recoveryTriggered) {
      log.error("Error {}: {}", e.getCode(), e.getMessage());
    }
  }
}
