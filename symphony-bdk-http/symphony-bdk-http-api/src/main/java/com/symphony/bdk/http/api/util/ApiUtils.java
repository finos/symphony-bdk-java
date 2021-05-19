package com.symphony.bdk.http.api.util;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Collections;

@API(status = API.Status.INTERNAL)
public final class ApiUtils {

  private static final Logger log = LoggerFactory.getLogger(ApiUtils.class);

  /**
   * Creates a user agent string used for the User-Agent header
   *
   * @return a user agent string containing the current BDK version
   */
  public static String getUserAgent() {
    return "Symphony-BDK-Java/" + getBdkVersion() + " Java/" + System.getProperty("java.version");
  }

  public static void logTrustStore(KeyStore trustStore) throws KeyStoreException {
    if (log.isDebugEnabled()) {
      for (String alias : Collections.list(trustStore.aliases())) {
        log.debug("Loading {} from truststore", alias);
      }
    }
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
