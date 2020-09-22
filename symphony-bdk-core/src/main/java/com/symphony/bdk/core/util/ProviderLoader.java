package com.symphony.bdk.core.util;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Generated
@API(status = API.Status.INTERNAL)
public class ProviderLoader {

  /**
   * Load a service implementation class using {@link ServiceLoader}.
   *
   * @return a service implementation class.
   */
  public static <T> T lookupSingleService(Class<T> clz) {

    final ServiceLoader<T> classServiceLoader = ServiceLoader.load(clz);

    final List<T> services = StreamSupport.stream(classServiceLoader.spliterator(), false)
            .collect(Collectors.toList());

    if (services.isEmpty()) {
      throw new IllegalStateException("No service implementation found in classpath.");
    } else if (services.size() > 1) {
      log.warn("More than 1 service implementation found in classpath, will use : {}",
          services.stream().findFirst().get());
    }

    return services.stream().findFirst().get();
  }
}
