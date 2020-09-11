package com.symphony.bdk.core.api.invoker.util;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public final class ApiUtils {

  /**
   * Creates a user agent string used for the User-Agent header
   *
   * @return a user agent string containing the current BDK version
   */
  public static String getUserAgent() {
    return "Symphony BDK/" + getBdkVersion() + "/java/" + System.getProperty("java.version");
  }

  private static String getBdkVersion() {
    String jarVersion = ApiUtils.class.getPackage().getImplementationVersion();
    String bdkVersion = jarVersion == null ? "2.0" : jarVersion;

    int dash = bdkVersion.indexOf("-");
    if (dash != -1) {
      // remove the potential "-SNAPSHOT" or "-RELEASE"
      bdkVersion = bdkVersion.substring(0, dash);
    }

    return bdkVersion;
  }

  private ApiUtils() {
    //to forbid class instantiation
  }
}
