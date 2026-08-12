package com.symphony.bdk.http.jdk;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.symphony.bdk.core.util.ServiceLookup;
import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;

import org.junit.jupiter.api.Test;

class ApiClientBuilderProviderJdkTest {

  @Test
  void newInstance_returnsApiClientBuilderJdk() {
    ApiClientBuilder builder = new ApiClientBuilderProviderJdk().newInstance();

    assertInstanceOf(ApiClientBuilderJdk.class, builder);
  }

  /**
   * With only {@code symphony-bdk-http-jdk} on this module's own test classpath, {@link
   * ServiceLookup#lookupSingleService} resolves {@link ApiClientBuilderProviderJdk} via {@link
   * java.util.ServiceLoader} without any explicit configuration (spec: SPI Discoverability / Sole HTTP
   * implementation on the runtime classpath).
   */
  @Test
  void lookupSingleService_resolvesApiClientBuilderProviderJdk_whenSoleImplementationOnClasspath() {
    ApiClientBuilderProvider provider = ServiceLookup.lookupSingleService(ApiClientBuilderProvider.class);

    assertInstanceOf(ApiClientBuilderProviderJdk.class, provider);
  }
}
