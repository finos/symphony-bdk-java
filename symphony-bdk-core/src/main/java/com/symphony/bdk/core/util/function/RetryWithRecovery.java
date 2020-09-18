package com.symphony.bdk.core.util.function;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;

import com.symphony.bdk.core.util.BdkExponentialFunction;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Predicate;

/**
 * This class aims to implement a retry mechanism (on top of a{@link Retry})
 * with different recovery strategies based on predicates.
 * @param <T> the type of the object returned by {@link #execute()}
 */
@Slf4j
public class RetryWithRecovery<T> {
  private SupplierWithApiException<T> supplier;
  private Predicate<ApiException> ignoreApiException;
  private Map<Predicate<ApiException>, ConsumerWithThrowable> recoveryStrategies;
  private Retry retry;

  /**
   * Constructor with no predicate on when to ignore an {@link ApiException},
   * i.e. ApiExceptions will never be ignored.
   * @param name the name of the {@link Retry} service.
   * @param bdkRetryConfig the retry configuration to be used.
   * @param supplier the supplier responsible to provide the object of param type T and which may throw an {@link ApiException}.
   * @param retryOnExceptionPredicate predicate on a thrown {@link ApiException} to know if call should be retried.
   * @param recoveryStrategies mapping between {@link Predicate<ApiException>} and the corresponding recovery functions to be executed before retrying.
   *                           If several predicates match, all corresponding consumers will be executed.
   */
  public RetryWithRecovery(String name, BdkRetryConfig bdkRetryConfig, SupplierWithApiException<T> supplier,
      Predicate<Throwable> retryOnExceptionPredicate,
      Map<Predicate<ApiException>, ConsumerWithThrowable> recoveryStrategies) {
    this(name, bdkRetryConfig, supplier, retryOnExceptionPredicate, (e) -> false, recoveryStrategies);
  }

  /**
   * Constructor with a predicate on when to ignore an {@link ApiException},
   * i.e. ApiExceptions will be ignored if the predicate matches.
   * @param name the name of the {@link Retry} service.
   * @param bdkRetryConfig the retry configuration to be used.
   * @param supplier the supplier responsible to provide the object of param type T and which may throw an {@link ApiException}.
   * @param retryOnExceptionPredicate predicate on a thrown {@link ApiException} to know if call should be retried.
   * @param ignoreApiException predicate on a thrown {@link ApiException} to know if exception should be ignored,
   *                           which means no subsequent retry will be made and null value will be returned.
   * @param recoveryStrategies mapping between {@link Predicate<ApiException>} and the corresponding recovery functions to be executed before retrying.
   *                           If several predicates match, all corresponding consumers will be executed.
   */
  public RetryWithRecovery(String name, BdkRetryConfig bdkRetryConfig, SupplierWithApiException<T> supplier,
      Predicate<Throwable> retryOnExceptionPredicate, Predicate<ApiException> ignoreApiException,
      Map<Predicate<ApiException>, ConsumerWithThrowable> recoveryStrategies) {
    this.supplier = supplier;
    this.ignoreApiException = ignoreApiException;
    this.recoveryStrategies = recoveryStrategies;
    this.retry = createRetry(name, bdkRetryConfig, retryOnExceptionPredicate);
  }

  /**
   * Executes the retry mechanism by calling the {@link RetryWithRecovery#supplier},
   * executing the potential matching recovery functions and retrying if needed.
   * @return an object of param type T
   * @throws Throwable
   */
  public T execute() throws Throwable {
    return this.retry.executeCheckedSupplier(this::executeMainAndRecoveryStrategies);
  }

  private Retry createRetry(String name, BdkRetryConfig bdkRetryConfig,
      Predicate<Throwable> retryOnExceptionPredicate) {
    RetryConfig retryConfig = RetryConfig.custom()
        .maxAttempts(bdkRetryConfig.getMaxAttempts())
        .intervalFunction(BdkExponentialFunction.ofExponentialBackoff(bdkRetryConfig))
        .retryOnException(retryOnExceptionPredicate)
        .build();

    Retry retry = Retry.of(name, retryConfig);
    retry.getEventPublisher().onRetry(event -> {
      double interval = event.getWaitInterval().toMillis() / 1000.0;
      if (event.getLastThrowable() != null) {
        log.debug("{} service failed due to {}", name, event.getLastThrowable().getMessage());
      }
      log.info("Retry in {}s...", interval);
    });

    return retry;
  }

  private T executeMainAndRecoveryStrategies() throws Throwable {
    try {
      return supplier.get();
    } catch (ApiException e) {
      if (ignoreApiException.test(e)) {
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
        recoveryTriggered = true;
        entry.getValue().consume();
      }
    }

    if (!recoveryTriggered) {
      log.error("Error {}: {}", e.getCode(), e.getMessage());
    }
  }
}
