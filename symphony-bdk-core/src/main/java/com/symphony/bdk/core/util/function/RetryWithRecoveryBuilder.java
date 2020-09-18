package com.symphony.bdk.core.util.function;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class RetryWithRecoveryBuilder<T> {
  private String name;
  private BdkRetryConfig retryConfig;
  private SupplierWithApiException<T> supplier;
  private Predicate<Throwable> retryOnExceptionPredicate;
  private Predicate<ApiException> ignoreException;
  private Map<Predicate<ApiException>, ConsumerWithThrowable> recoveryStrategies;

  public static <T> RetryWithRecoveryBuilder<T> from(RetryWithRecoveryBuilder<?> from) {
    RetryWithRecoveryBuilder<T> copy = new RetryWithRecoveryBuilder();
    copy.name = from.name;
    copy.retryConfig = from.retryConfig;
    copy.retryOnExceptionPredicate = from.retryOnExceptionPredicate;
    copy.ignoreException = from.ignoreException;
    copy.recoveryStrategies = new HashMap<>(from.recoveryStrategies);

    return copy;
  }

  public RetryWithRecoveryBuilder() {
    this.ignoreException = (e) -> false;
    this.recoveryStrategies = new HashMap<>();
  }

  public RetryWithRecoveryBuilder<T> name(String name) {
    this.name = name;
    return this;
  }

  public RetryWithRecoveryBuilder<T> retryConfig(BdkRetryConfig retryConfig) {
    this.retryConfig = retryConfig;
    return this;
  }

  public RetryWithRecoveryBuilder<T> supplier(SupplierWithApiException<T> supplier) {
    this.supplier = supplier;
    return this;
  }

  public RetryWithRecoveryBuilder<T> retryOnException(Predicate<Throwable> retryOnExceptionPredicate) {
    this.retryOnExceptionPredicate = retryOnExceptionPredicate;
    return this;
  }

  public RetryWithRecoveryBuilder<T> ignoreException(Predicate<ApiException> ignoreException) {
    this.ignoreException = ignoreException;
    return this;
  }

  public RetryWithRecoveryBuilder<T> recoveryStrategy(Predicate<ApiException> condition, ConsumerWithThrowable recovery) {
    this.recoveryStrategies.put(condition, recovery);
    return this;
  }

  public RetryWithRecovery<T> build() {
    return new Resilience4jRetryWithRecovery<>(name, retryConfig, supplier, retryOnExceptionPredicate, ignoreException,
        recoveryStrategies);
  }
}
