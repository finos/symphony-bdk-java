package com.symphony.bdk.core.util;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Predicate;

@Slf4j
public class RetryWithRecovery<T> {
  private SupplierWithApiException<T> supplier;
  private Predicate<ApiException> ignoreApiException;
  private Map<Predicate<ApiException>, ConsumerWithThrowable> recoveryStrategies;
  private Retry retry;

  public RetryWithRecovery(String name, BdkRetryConfig bdkRetryConfig, SupplierWithApiException<T> supplier,
      Predicate<Throwable> retryOnExceptionPredicate,
      Map<Predicate<ApiException>, ConsumerWithThrowable> recoveryStrategies) {
    this(name, bdkRetryConfig, supplier, retryOnExceptionPredicate, (e) -> false, recoveryStrategies);
  }

  public RetryWithRecovery(String name, BdkRetryConfig bdkRetryConfig, SupplierWithApiException<T> supplier,
      Predicate<Throwable> retryOnExceptionPredicate, Predicate<ApiException> ignoreApiException,
      Map<Predicate<ApiException>, ConsumerWithThrowable> recoveryStrategies) {
    this.supplier = supplier;
    this.ignoreApiException = ignoreApiException;
    this.recoveryStrategies = recoveryStrategies;
    this.retry = createRetry(name, bdkRetryConfig, retryOnExceptionPredicate);
  }

  public T execute() throws Throwable {
    log.debug("RetryWithRecovery::execute");
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
        entry.getValue().consume(e);
      }
    }

    if (!recoveryTriggered) {
      log.error("Error {}: {}", e.getCode(), e.getMessage());
    }
  }
}
