package com.symphony.bdk.core.retry;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.retry.resilience4j.Resilience4jRetryWithRecovery;
import com.symphony.bdk.core.util.function.ConsumerWithThrowable;
import com.symphony.bdk.core.util.function.SupplierWithApiException;

import org.apiguardian.api.API;

import javax.ws.rs.ProcessingException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Builder class to facilitate the instantiation of a {@link RetryWithRecovery}.
 *
 * @param <T> the type to be returned by {@link RetryWithRecovery#execute()}.
 */
@API(status = API.Status.INTERNAL)
public class RetryWithRecoveryBuilder<T> {
  private String name;
  private BdkRetryConfig retryConfig;
  private SupplierWithApiException<T> supplier;
  private Predicate<Throwable> retryOnExceptionPredicate;
  private Predicate<ApiException> ignoreException;
  private Map<Predicate<ApiException>, ConsumerWithThrowable> recoveryStrategies;

  /**
   * Copies all fields of an existing builder except the {@link #supplier}.
   *
   * @param from the {@link RetryWithRecovery} to be copied.
   * @param <T> the target parametrized type.
   * @return a copy of the builder passed as parameter.
   */
  public static <T> RetryWithRecoveryBuilder<T> from(RetryWithRecoveryBuilder<?> from) {
    RetryWithRecoveryBuilder<T> copy = new RetryWithRecoveryBuilder();
    copy.name = from.name;
    copy.retryConfig = from.retryConfig;
    copy.retryOnExceptionPredicate = from.retryOnExceptionPredicate;
    copy.ignoreException = from.ignoreException;
    copy.recoveryStrategies = new HashMap<>(from.recoveryStrategies);

    return copy;
  }

  /**
   * Checks if a throwable is a {@link ProcessingException} or a {@link ApiException} minor error.
   * This is the default function used in {@link RetryWithRecovery}
   * to check if a given exception thrown should lead to a retry.
   *
   * @param t the throwable to be checked.
   * @return true if passed throwable is a {@link ProcessingException} (e.g. in case of a temporary network exception)
   * or if it is a {@link ApiException} which {@link ApiException#isMinorError()}.
   */
  public static boolean isNetworkOrMinorError(Throwable t) {
    if (t instanceof ApiException) {
      return ((ApiException) t).isMinorError();
    }
    return t instanceof ProcessingException;
  }

  /**
   * Checks if a throwable is a {@link ProcessingException} or a {@link ApiException} minor error or client error.
   *
   * @param t the throwable to be checked.
   * @return true if passed throwable is a {@link ProcessingException} (e.g. in case of a temporary network exception)
   * or if it is a {@link ApiException} which {@link ApiException#isMinorError()} or {@link ApiException#isClientError()}.
   */
  public static boolean isNetworkOrMinorErrorOrClientError(Throwable t) {
    if (t instanceof ApiException) {
      ApiException apiException = (ApiException) t;
      return apiException.isMinorError() || apiException.isClientError();
    }
    return t instanceof ProcessingException;
  }

  /**
   * Default constructor which ignores no exception
   * and retries exceptions fulfilling {@link RetryWithRecoveryBuilder#isNetworkOrMinorError}.
   */
  public RetryWithRecoveryBuilder() {
    this.recoveryStrategies = new HashMap<>();
    this.ignoreException = e -> false;
    this.retryOnExceptionPredicate = RetryWithRecoveryBuilder::isNetworkOrMinorError;
    this.retryConfig = new BdkRetryConfig();
  }

  /**
   * Sets the name and returns the modified builder.
   *
   * @param name the name of the {@link RetryWithRecovery}
   * @return the modified builder instance.
   */
  public RetryWithRecoveryBuilder<T> name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the retry configuration and returns the modified builder.
   *
   * @param retryConfig the retry configuration to be used.
   * @return the modified builder instance.
   */
  public RetryWithRecoveryBuilder<T> retryConfig(BdkRetryConfig retryConfig) {
    this.retryConfig = retryConfig;
    return this;
  }

  /**
   * Sets the retry configuration and returns the modified builder.
   *
   * @param supplier the function to be called by the {@link RetryWithRecovery}
   *                 which returns the desired object and which may fail.
   * @return the modified builder instance.
   */
  public RetryWithRecoveryBuilder<T> supplier(SupplierWithApiException<T> supplier) {
    this.supplier = supplier;
    return this;
  }

  /**
   * Sets the conditions on which we should retry the call to the provided {@link #supplier}.
   *
   * @param retryOnExceptionPredicate the condition when we should retry the call
   *                                  when the {@link #supplier} throws an exception.
   * @return the modified builder instance.
   */
  public RetryWithRecoveryBuilder<T> retryOnException(Predicate<Throwable> retryOnExceptionPredicate) {
    this.retryOnExceptionPredicate = retryOnExceptionPredicate;
    return this;
  }

  /**
   * Sets the condition on which we should ignore an exception thrown by the {@link #supplier}
   * and return null in {@link RetryWithRecovery#execute()}.
   *
   * @param ignoreException the condition when we should ignore a given exception
   * @return the modified builder instance.
   */
  public RetryWithRecoveryBuilder<T> ignoreException(Predicate<ApiException> ignoreException) {
    this.ignoreException = ignoreException;
    return this;
  }

  /**
   * Sets one recovery strategy which consists in a predicate on the thrown {@link ApiException}
   * and in a corresponding recovery function to be executed when condition is met.
   *
   * @param condition the predicate to check if the exception should lead to the execution of the recovery function.
   * @param recovery the recovery function to be executed when condition is fulfilled.
   * @return
   */
  public RetryWithRecoveryBuilder<T> recoveryStrategy(Predicate<ApiException> condition, ConsumerWithThrowable recovery) {
    this.recoveryStrategies.put(condition, recovery);
    return this;
  }

  public RetryWithRecoveryBuilder<T> clearRecoveryStrategies() {
    this.recoveryStrategies.clear();
    return this;
  }

  /**
   * Builds a {@link RetryWithRecovery} based on provided fields.
   *
   * @return a new instance of {@link RetryWithRecovery} based on the provided fields.
   */
  public RetryWithRecovery<T> build() {
    return new Resilience4jRetryWithRecovery<>(name, retryConfig, supplier, retryOnExceptionPredicate, ignoreException,
        recoveryStrategies);
  }
}
