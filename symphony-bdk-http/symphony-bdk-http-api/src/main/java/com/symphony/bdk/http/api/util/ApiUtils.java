package com.symphony.bdk.http.api.util;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
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

  /**
   * Used to load the system truststore to be used in the ssl context
   */
  public static void addDefaultRootCaCertificates(KeyStore trustStore) throws GeneralSecurityException {
    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    // Loads default Root CA certificates (generally, from JAVA_HOME/lib/cacerts)
    trustManagerFactory.init((KeyStore)null);
    for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
      if (trustManager instanceof X509TrustManager) {
        for (X509Certificate acceptedIssuer : ((X509TrustManager) trustManager).getAcceptedIssuers()) {
          trustStore.setCertificateEntry(acceptedIssuer.getSubjectDN().getName(), acceptedIssuer);
        }
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
