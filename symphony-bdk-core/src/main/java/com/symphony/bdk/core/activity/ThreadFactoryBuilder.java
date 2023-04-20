package com.symphony.bdk.core.activity;

import com.symphony.bdk.http.api.tracing.MDCUtils;

import org.apiguardian.api.API;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

@API(status = API.Status.INTERNAL)
class ThreadFactoryBuilder {
  private String name = null;
  private int priority = Thread.NORM_PRIORITY;

  public ThreadFactoryBuilder setName(String name) {
    Objects.requireNonNull(name);
    this.name = name;
    return this;
  }

  public ThreadFactoryBuilder setPriority(int priority) {
    if (priority > Thread.MAX_PRIORITY) {
      throw new IllegalArgumentException(
          String.format("Thread priority %s must be <= %s", priority, Thread.MAX_PRIORITY));
    }

    if (priority < Thread.MIN_PRIORITY) {
      throw new IllegalArgumentException(
          String.format("Thread priority %s must be >= %s", priority, Thread.MIN_PRIORITY));
    }

    this.priority = priority;
    return this;
  }

  public ThreadFactory build() {
    return build(this);
  }

  private static ThreadFactory build(ThreadFactoryBuilder builder) {
    final String name = builder.name;
    final int priority = builder.priority;
    final ThreadFactory factory = Executors.defaultThreadFactory();

    final AtomicLong count = new AtomicLong(0);
    return runnable -> {
      Thread thread = factory.newThread(MDCUtils.wrap(runnable));
      thread.setPriority(priority);

      if (name != null) {
        thread.setName(name + "-" + count.getAndIncrement());
      }

      return thread;
    };
  }
}
