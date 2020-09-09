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

    return "Symphony BDK/" + getBdkVersion() + "/java/" + getJavaVersion();
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

  private static String getJavaVersion() {
    String version = System.getProperty("java.version");
    if (version.startsWith("1.")) {
      // if java.version = 1.x.y_z: will return 1.x
      // applicable for java version <= 8
      version = version.substring(0, 3);
    } else {
      // if java.version = x.y.z, will return x
      // applicable for java version >= 9
      int dot = version.indexOf(".");
      if (dot != -1) {
        version = version.substring(0, dot);
      }
    }
    return version;
  }
}
