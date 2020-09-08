package com.symphony.bdk.core.api.invoker.util;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public class ApiUtils {

  /**
   * Creates a user agent string used for the User-Agent header
   *
   * @return a user agent string containing the current BDK version
   */
  public static String getUserAgent() {
    String jarVersion = ApiUtils.class.getPackage().getImplementationVersion();
    String bdkVersion = jarVersion == null ? "2.0" : jarVersion;

    return "Symphony BDK/" + bdkVersion + "/java/1.8";
  }
}
