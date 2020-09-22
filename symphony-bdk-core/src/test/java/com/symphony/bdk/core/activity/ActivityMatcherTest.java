package com.symphony.bdk.core.activity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link ActivityMatcher}.
 */
class ActivityMatcherTest {

  @Test
  void testAlways() {
    assertTrue(ActivityMatcher.always().matches(mock(ActivityContext.class)));
  }
}
