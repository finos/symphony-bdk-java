package com.symphony.bdk.http.jersey2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.socket.tls.KeyStoreFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.client.ClientRequestFilter;

class ApiClientBuilderJersey2Test {

  private static final String KEY_STORE_PWD = "changeit";

  private ClientAndServer mockServer;

  @BeforeEach
  void setUp() {
    // enforce client certificates usage
    ConfigurationProperties.tlsMutualAuthenticationRequired(true);
    mockServer = ClientAndServer.startClientAndServer();
  }

  @AfterEach
  void tearDown() {
    mockServer.stop();
  }

  @Test
  void sslContextIsUsed()
      throws ApiException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
    ByteArrayOutputStream keyStoreData = getMockServerKeyStore();
    ApiClient client = new ApiClientBuilderJersey2()
        .withBasePath("https://localhost:" + mockServer.getPort())
        .withKeyStore(keyStoreData.toByteArray(), "changeit")
        .withTrustStore(keyStoreData.toByteArray(), "changeit")
        .build();

    mockServer.withSecure(true)
        .when(HttpRequest.request().withMethod("GET").withPath("/test"))
        .respond(HttpResponse.response().withStatusCode(200));

    ApiResponse<Object> response =
        client.invokeAPI("/test", "GET", Collections.emptyList(), null, Collections.emptyMap(), Collections.emptyMap(),
            null, "application/json", "", null, null);

    assertEquals(200, response.getStatusCode());
  }

  @Test
  void addFilter() {
    AtomicBoolean filterHasBeenCalled = new AtomicBoolean(false);
    ApiClient client = new ApiClientBuilderJersey2()
        .withBasePath("http://localhost:" + mockServer.getPort())
        .addFilter((ClientRequestFilter) requestContext -> filterHasBeenCalled.set(true))
        .build();

    try {
      client.invokeAPI("/test", "GET", Collections.emptyList(), null, Collections.emptyMap(), Collections.emptyMap(),
          null, "application/json", "", null, null);
    } catch (ApiException e) {
      // nothing implemented, calls fail but filter should be called before
    }

    assertTrue(filterHasBeenCalled.get());
  }

  private ByteArrayOutputStream getMockServerKeyStore()
      throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
    KeyStore mockServerKeyStore = new KeyStoreFactory(new MockServerLogger()).loadOrCreateKeyStore();
    ByteArrayOutputStream keyStoreData = new ByteArrayOutputStream();
    mockServerKeyStore.store(keyStoreData, KEY_STORE_PWD.toCharArray());
    return keyStoreData;
  }
}
