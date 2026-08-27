package com.symphony.bdk.http.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CancellationException;

class InterruptionUtilTest {

  @Test
  void testGetInterruptionTypeNull() {
    assertEquals(InterruptionUtil.InterruptionType.NONE, InterruptionUtil.getInterruptionType(null));
  }

  @Test
  void testGetInterruptionTypeNone() {
    assertEquals(InterruptionUtil.InterruptionType.NONE, InterruptionUtil.getInterruptionType(new RuntimeException("Ordinary error")));
  }

  @Test
  void testGetInterruptionTypeDirectThreadInterruption() {
    assertEquals(InterruptionUtil.InterruptionType.THREAD_INTERRUPTION, InterruptionUtil.getInterruptionType(new InterruptedException("Interrupted!")));
  }

  @Test
  void testGetInterruptionTypeDirectCancellation() {
    assertEquals(InterruptionUtil.InterruptionType.INTERRUPTION, InterruptionUtil.getInterruptionType(new CancellationException("Cancelled!")));
  }

  @Test
  void testGetInterruptionTypeNestedThreadInterruption() {
    RuntimeException wrapped = new RuntimeException("wrapped", new InterruptedException("Interrupted!"));
    assertEquals(InterruptionUtil.InterruptionType.THREAD_INTERRUPTION, InterruptionUtil.getInterruptionType(wrapped));
  }

  @Test
  void testGetInterruptionTypeNestedCancellation() {
    RuntimeException wrapped = new RuntimeException("wrapped", new CancellationException("Cancelled!"));
    assertEquals(InterruptionUtil.InterruptionType.INTERRUPTION, InterruptionUtil.getInterruptionType(wrapped));
  }

  @Test
  void testGetInterruptionTypeCircularCauseChainNone() {
    CircularException e1 = new CircularException("e1");
    CircularException e2 = new CircularException("e2");
    e1.setCustomCause(e2);
    e2.setCustomCause(e1);

    assertEquals(InterruptionUtil.InterruptionType.NONE, InterruptionUtil.getInterruptionType(e1));
  }

  @Test
  void testGetInterruptionTypeCircularCauseChainWithThreadInterruption() {
    CircularException e1 = new CircularException("e1");
    CircularException e2 = new CircularException("e2");
    e1.setCustomCause(e2);

    CircularException e3 = new CircularException("e3");
    e3.setCustomCause(new InterruptedException("Interrupted!"));
    e2.setCustomCause(e3);

    assertEquals(InterruptionUtil.InterruptionType.THREAD_INTERRUPTION, InterruptionUtil.getInterruptionType(e1));
  }

  private static class CircularException extends Exception {
    private Throwable customCause;

    public CircularException(String message) {
      super(message);
    }

    public void setCustomCause(Throwable cause) {
      this.customCause = cause;
    }

    @Override
    public synchronized Throwable getCause() {
      return this.customCause;
    }
  }
}
