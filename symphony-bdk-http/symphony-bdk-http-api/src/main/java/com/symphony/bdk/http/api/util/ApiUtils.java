package com.symphony.bdk.http.api.util;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
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

  public static KeyStore createAndLogTrustStore(String keyStoreType, InputStream keyStoreBytes, char[] keyStorePassword)
      throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
    final KeyStore trustStore = KeyStore.getInstance(keyStoreType);
    trustStore.load(keyStoreBytes, keyStorePassword);

    if (log.isDebugEnabled()) {
      for (String alias : Collections.list(trustStore.aliases())) {
        log.debug("Loading {} from truststore", alias);
      }
    }

    return trustStore;
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
