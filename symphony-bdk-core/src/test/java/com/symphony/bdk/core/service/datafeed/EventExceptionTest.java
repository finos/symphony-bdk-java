package com.symphony.bdk.core.service.datafeed;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EventExceptionTest {

  @Test
  void coverage() {
    assertEquals("error", new EventException("error").getMessage());

    Exception cause = new Exception();
    EventException exception = new EventException("error", cause);
    assertEquals("error", exception.getMessage());
    assertEquals(cause, exception.getCause());

    Exception cause2 = new Exception("error");
    EventException exception2 = new EventException(cause2);
    assertTrue(exception2.getMessage().contains("error"));
    assertEquals(cause2, exception2.getCause());
  }
}
