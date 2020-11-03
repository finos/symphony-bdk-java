package com.symphony.bdk.core.retry;

import com.symphony.bdk.core.util.function.ConsumerWithThrowable;

import org.apiguardian.api.API;

import java.util.function.Predicate;

@API(status = API.Status.INTERNAL)
public class RecoveryStrategy<T extends Exception> {
  Class<? extends T> exceptionType;
  Predicate<T> condition;
  ConsumerWithThrowable recovery;

  public RecoveryStrategy(Class<? extends T> exceptionType, Predicate<T> condition, ConsumerWithThrowable recovery) {
    this.exceptionType = exceptionType;
    this.condition = condition;
    this.recovery = recovery;
  }

  public boolean matches(Exception e) {
    return exceptionType.isAssignableFrom(e.getClass()) && condition.test(exceptionType.cast(e));
  }

  public void runRecovery() throws Throwable {
    recovery.consume();
  }
}
