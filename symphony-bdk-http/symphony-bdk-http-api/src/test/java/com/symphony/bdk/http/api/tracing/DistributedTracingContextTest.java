package com.symphony.bdk.http.api.tracing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DistributedTracingContextTest {

  @Test
  void shouldSetAndClearMDCWithSuccess() {

    DistributedTracingContext.setTraceId();

    final String baseTraceId = DistributedTracingContext.getTraceId();

    assertEquals(6, baseTraceId.length());

    DistributedTracingContext.setTraceId(baseTraceId);

    final String updatedTraceId = DistributedTracingContext.getTraceId();

    assertEquals(13, updatedTraceId.length());
    assertTrue(updatedTraceId.startsWith(baseTraceId + ":"));

    DistributedTracingContext.clear();

    assertTrue(DistributedTracingContext.getTraceId().isEmpty());
  }
}
