package com.symphony.bdk.core.retry;

import com.symphony.bdk.core.util.function.ConsumerWithThrowable;

import org.apiguardian.api.API;

import java.util.function.Predicate;

@API(status = API.Status.INTERNAL)
public class RecoveryStrategy {
  Class<? extends Exception> exceptionType;
  Predicate<Exception> condition;
  ConsumerWithThrowable recovery;

  public <T extends Exception> RecoveryStrategy(Class<? extends T> exceptionType, Predicate<T> condition, ConsumerWithThrowable recovery) {
    this.exceptionType = exceptionType;
    this.condition = e -> exceptionType.isAssignableFrom(e.getClass()) && condition.test(exceptionType.cast(e));
    this.recovery = recovery;
  }

  public boolean matches(Exception e) {
    return condition.test(e);
  }

  public void runRecovery() throws Throwable {
    recovery.consume();
  }
}
