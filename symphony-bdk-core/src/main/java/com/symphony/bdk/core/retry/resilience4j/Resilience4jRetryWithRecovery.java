package com.symphony.bdk.core.retry.resilience4j;

import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.retry.RecoveryStrategy;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.util.BdkExponentialFunction;
import com.symphony.bdk.core.retry.function.SupplierWithApiException;
import com.symphony.bdk.http.api.ApiException;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.List;
import java.util.function.Predicate;

/**
 * This class aims to implement a retry mechanism (on top of a{@link Retry})
 * with different recovery strategies based on predicates.
 * @param <T> the type of the object returned by {@link #execute()}
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class Resilience4jRetryWithRecovery<T> extends RetryWithRecovery<T> {
  private final Retry retry;

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
  public Resilience4jRetryWithRecovery(String name, String address, BdkRetryConfig bdkRetryConfig, SupplierWithApiException<T> supplier,
      Predicate<Throwable> retryOnExceptionPredicate, List<RecoveryStrategy> recoveryStrategies) {
    this(name, address, bdkRetryConfig, supplier, retryOnExceptionPredicate, (e) -> false, recoveryStrategies);
  }

  /**
   * Constructor with a predicate on when to ignore an {@link ApiException},
   * i.e. ApiExceptions will be ignored if the predicate matches.
   * @param name the name of the {@link Retry} service.
   * @param bdkRetryConfig the retry configuration to be used.
   * @param supplier the supplier responsible to provide the object of param type T and which may throw an {@link ApiException}.
   * @param retryOnExceptionPredicate predicate on a thrown {@link ApiException} to know if call should be retried.
   * @param ignoreException predicate on a thrown {@link Exception} to know if exception should be ignored,
   *                           which means no subsequent retry will be made and null value will be returned.
   * @param recoveryStrategies mapping between {@link Predicate<ApiException>} and the corresponding recovery functions to be executed before retrying.
   *                           If several predicates match, all corresponding consumers will be executed.
   */
  public Resilience4jRetryWithRecovery(String name, String address, BdkRetryConfig bdkRetryConfig, SupplierWithApiException<T> supplier,
      Predicate<Throwable> retryOnExceptionPredicate, Predicate<Exception> ignoreException, List<RecoveryStrategy> recoveryStrategies) {
    super(supplier, ignoreException, recoveryStrategies, address);
    this.retry = createRetry(name, bdkRetryConfig, retryOnExceptionPredicate);
  }

  /**
   * {@inheritDoc}
   */
  public T execute() throws Throwable {
    return this.retry.executeCheckedSupplier(this::executeOnce);
  }

  private Retry createRetry(
      final String name,
      final BdkRetryConfig bdkRetryConfig,
      final Predicate<Throwable> retryOnExceptionPredicate
  ) {

    final RetryConfig retryConfig = RetryConfig.custom()
        .maxAttempts(bdkRetryConfig.getMaxAttempts())
        .intervalFunction(BdkExponentialFunction.ofExponentialBackoff(bdkRetryConfig))
        .retryOnException(retryOnExceptionPredicate)
        .build();

    final Retry retry = Retry.of(name, retryConfig);
    retry.getEventPublisher().onRetry(event -> {
      double interval = event.getWaitInterval().toMillis() / 1000.0;
      if (event.getLastThrowable() != null) {
        log.debug("{} service failed due to {}", name, event.getLastThrowable().getMessage());
      }
      log.info("Retry in {}s...", interval);
    });

    return retry;
  }
}
