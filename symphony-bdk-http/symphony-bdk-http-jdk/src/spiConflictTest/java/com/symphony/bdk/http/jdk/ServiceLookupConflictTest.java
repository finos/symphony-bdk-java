package com.symphony.bdk.http.jdk;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.core.util.ServiceLookup;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;

import org.junit.jupiter.api.Test;

/**
 * Runs in a dedicated {@code spiConflictTest} source set/Test task (see {@code build.gradle}) whose classpath
 * has both {@code symphony-bdk-http-jdk} and {@code symphony-bdk-http-jersey} present, so that {@link
 * ServiceLookup#lookupSingleService} sees two {@link ApiClientBuilderProvider} implementations via {@link
 * java.util.ServiceLoader}. This must stay out of the main {@code test} task, whose classpath is relied upon by
 * {@link ApiClientBuilderProviderJdkTest} to have {@code symphony-bdk-http-jdk} as the sole implementation.
 */
class ServiceLookupConflictTest {

  @Test
  void lookupSingleService_throwsIllegalStateException_whenTwoProvidersOnClasspath() {
    assertThrows(IllegalStateException.class, () -> ServiceLookup.lookupSingleService(ApiClientBuilderProvider.class));
  }
}
