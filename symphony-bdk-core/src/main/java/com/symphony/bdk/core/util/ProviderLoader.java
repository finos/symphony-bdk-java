package com.symphony.bdk.core.util;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@API(status = API.Status.INTERNAL)
@Slf4j
@Generated
public class ProviderLoader {

  /**
   * Load {@link ApiClient} implementation class using {@link ServiceLoader}.
   *
   * @return an {@link ApiClientBuilderProvider}.
   */
  public static ApiClientBuilderProvider findApiClientBuilderProvider() {

    final ServiceLoader<ApiClientBuilderProvider> apiClientServiceLoader =
        ServiceLoader.load(ApiClientBuilderProvider.class);

    final List<ApiClientBuilderProvider> apiClientProviders =
        StreamSupport.stream(apiClientServiceLoader.spliterator(), false)
            .collect(Collectors.toList());

    if (apiClientProviders.isEmpty()) {
      throw new IllegalStateException("No ApiClientProvider implementation found in classpath.");
    } else if (apiClientProviders.size() > 1) {
      log.warn("More than 1 ApiClientProvider implementation found in classpath, will use : {}",
          apiClientProviders.stream().findFirst().get());
    }

    return apiClientProviders.stream().findFirst().get();
  }
}
