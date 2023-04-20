package com.symphony.bdk.core.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadFactory;

class ThreadFactoryBuilderTest {

  @Test
  void builderNameNull() {
    assertThatThrownBy(() -> new ThreadFactoryBuilder().setName(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void builderPriorityTooBig() {
    assertThatThrownBy(() -> new ThreadFactoryBuilder().setPriority(Thread.MAX_PRIORITY + 1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(String.format("Thread priority %s must be <= %s", Thread.MAX_PRIORITY + 1, Thread.MAX_PRIORITY));
  }

  @Test
  void builderPriorityTooSmall() {
    assertThatThrownBy(() -> new ThreadFactoryBuilder().setPriority(Thread.MIN_PRIORITY - 1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(String.format("Thread priority %s must be >= %s", Thread.MIN_PRIORITY - 1, Thread.MIN_PRIORITY));
  }

  @Test
  void builderSuccessful() {
    ThreadFactory threadFactory = new ThreadFactoryBuilder().setName("NAME").setPriority(Thread.MAX_PRIORITY).build();
    Thread thread = threadFactory.newThread(null);
    assertThat(thread.getName()).isEqualTo("NAME-0");
    assertThat(thread.getPriority()).isEqualTo(Thread.MAX_PRIORITY);
  }
}
