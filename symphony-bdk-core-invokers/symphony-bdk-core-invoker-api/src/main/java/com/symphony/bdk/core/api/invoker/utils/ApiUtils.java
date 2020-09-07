package com.symphony.bdk.core.api.invoker.utils;

public class ApiUtils {
  public static String getUserAgent() {
    String jarVersion = ApiUtils.class.getPackage().getImplementationVersion();
    String bdkVersion = jarVersion == null ? "2.0" : jarVersion;

    return "Symphony BDK/" + bdkVersion + "/java/1.8";
  }
}
