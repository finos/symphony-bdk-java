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
public class ServiceLookup {

  /**
   * Load a service implementation class using {@link ServiceLoader}.
   *
   * @return a service implementation class.
   * @throws IllegalStateException if no implementation or several implementations found in classpath.
   */
  public static <T> T lookupSingleService(Class<T> clz) {
    final ServiceLoader<T> classServiceLoader = ServiceLoader.load(clz);

    final List<T> services = StreamSupport.stream(classServiceLoader.spliterator(), false)
        .collect(Collectors.toList());

    if (services.isEmpty()) {
      throw new IllegalStateException(
          "No service implementation found in classpath for " + clz.getCanonicalName() + ". "
              + "Likely cause is a missing dependency to one of the symphony-bdk-http-* (jersey2 or webclient) modules.");
    } else if (services.size() > 1) {
      throw new IllegalStateException("More than 1 service implementation found in classpath for "
          + clz.getCanonicalName() + ". Try to remove a dependency to one of the symphony-bdk-http-* modules.");
    }

    return services.get(0);
  }
}
