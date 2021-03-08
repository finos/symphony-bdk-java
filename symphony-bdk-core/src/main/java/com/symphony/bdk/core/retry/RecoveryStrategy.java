package com.symphony.bdk.core.retry;

import com.symphony.bdk.core.util.function.ConsumerWithThrowable;

import org.apiguardian.api.API;

import java.util.function.Predicate;

/**
 * Class used by {@link RetryWithRecovery} to store a recovery strategy,
 * i.e. a condition on the exception to check whether the recovery function must be executed
 * and a recovery function to be executed.
 */
@API(status = API.Status.INTERNAL)
public class RecoveryStrategy {

  private final Predicate<Exception> condition;
  private final ConsumerWithThrowable recovery;

  /**
   *
   * @param exceptionType the actual exception class
   * @param condition the predicate which checks if a given exception corresponds to the recovery strategy
   * @param recovery the recovery function to be executed when applicable
   * @param <T> the actual exception class
   */
  public <T extends Exception> RecoveryStrategy(Class<? extends T> exceptionType, Predicate<T> condition, ConsumerWithThrowable recovery) {
    this.condition = e -> exceptionType.isAssignableFrom(e.getClass()) && condition.test(exceptionType.cast(e));
    this.recovery = recovery;
  }

  /**
   * Function to test if an exception corresponds to the recovery strategy.
   *
   * @param e the exception to be tested
   * @return true if the provided exception corresponds to the recovery strategy
   */
  public boolean matches(Exception e) {
    return this.condition.test(e);
  }

  /**
   * Runs the recovery function.
   *
   * @throws Throwable can be thrown by {@link ConsumerWithThrowable#consume()}
   */
  public void runRecovery() throws Throwable {
    this.recovery.consume();
  }
}
