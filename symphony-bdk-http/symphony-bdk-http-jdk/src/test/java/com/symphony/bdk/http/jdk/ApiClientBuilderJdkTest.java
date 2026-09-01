package com.symphony.bdk.http.jdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBuilder;
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
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.http.HttpClient;
import java.net.http.HttpRequest.Builder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

class ApiClientBuilderJdkTest {

  private static final String KEY_STORE_PWD = "changeit";

  private ClientAndServer mockServer;

  @BeforeEach
  void setUp() {
    this.mockServer = ClientAndServer.startClientAndServer();
  }

  @AfterEach
  void tearDown() {
    this.mockServer.stop();
  }

  @Test
  void sslContextIsUsed()
      throws ApiException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
    // ConfigurationProperties is process-wide global state (not scoped to a ClientAndServer instance), so it
    // is scoped to this test only via try/finally, or other tests/classes in the same JVM would unexpectedly
    // require mutual TLS too.
    ConfigurationProperties.tlsMutualAuthenticationRequired(true);
    try {
      ByteArrayOutputStream keyStoreData = this.getMockServerKeyStore();
      ApiClient client = new ApiClientBuilderJdk()
          .withBasePath("https://localhost:" + this.mockServer.getPort())
          .withKeyStore(keyStoreData.toByteArray(), "changeit")
          .withTrustStore(keyStoreData.toByteArray(), "changeit")
          .build();

      this.mockServer.withSecure(true)
          .when(HttpRequest.request().withMethod("GET").withPath("/test"))
          .respond(HttpResponse.response().withStatusCode(200));

      ApiResponse<Object> response =
          client.invokeAPI("/test", "GET", Collections.emptyList(), null, Collections.emptyMap(),
              Collections.emptyMap(), null, "application/json", "", null, null);

      assertEquals(200, response.getStatusCode());
    } finally {
      ConfigurationProperties.tlsMutualAuthenticationRequired(false);
    }
  }

  @Test
  void addFilter_appliesFilterToOutgoingRequest() {
    AtomicBoolean filterCalled = new AtomicBoolean(false);
    ApiClient client = new ApiClientBuilderJdk()
        .withBasePath("http://localhost:" + this.mockServer.getPort())
        .addFilter((java.util.function.Function<Builder, Builder>) builder -> {
          filterCalled.set(true);
          return builder;
        })
        .build();

    this.mockServer
        .when(HttpRequest.request().withMethod("GET").withPath("/test"))
        .respond(HttpResponse.response().withStatusCode(200));

    try {
      client.invokeAPI("/test", "GET", Collections.emptyList(), null, Collections.emptyMap(),
          Collections.emptyMap(), null, "application/json", "", null, null);
    } catch (ApiException e) {
      // the filter's invocation is what's under test here; a downstream failure is irrelevant
    }

    assertTrue(filterCalled.get());
  }

  @Test
  void withAuthentication_registersAuthenticationOnBuiltClient() {
    ApiClient client = new ApiClientBuilderJdk()
        .withBasePath("http://localhost:" + this.mockServer.getPort())
        .withAuthentication("myAuth", headers -> headers.put("Authorization", "Bearer token"))
        .build();

    assertTrue(client.getAuthentications().containsKey("myAuth"));
  }

  @Test
  void addFilter_throwsIllegalArgumentException_forNonFunctionFilter() {
    ApiClientBuilder builder = new ApiClientBuilderJdk();

    assertThrows(IllegalArgumentException.class, () -> builder.addFilter("not a function"));
  }

  @Test
  void proxyIsUsed_routesRequestThroughConfiguredProxy() throws IOException, ApiException {
    try (FakeHttpProxy proxy = new FakeHttpProxy(false)) {
      ApiClient client = new ApiClientBuilderJdk()
          .withBasePath("http://symphony-fake-target.invalid")
          .withProxy("localhost", proxy.getPort())
          .build();

      ApiResponse<Object> response =
          client.invokeAPI("/test", "GET", Collections.emptyList(), null, Collections.emptyMap(),
              Collections.emptyMap(), null, "application/json", "", null, null);

      assertEquals(200, response.getStatusCode());
      assertTrue(proxy.getLastRequestLine().contains("http://symphony-fake-target.invalid"),
          "expected the request line to use the absolute-URI form sent to a forward proxy, got: "
              + proxy.getLastRequestLine());
    }
  }

  @Test
  void proxyCredentials_answer407ChallengeSuccessfully() throws IOException, ApiException {
    try (FakeHttpProxy proxy = new FakeHttpProxy(true)) {
      ApiClient client = new ApiClientBuilderJdk()
          .withBasePath("http://symphony-fake-target.invalid")
          .withProxy("localhost", proxy.getPort())
          .withProxyCredentials("proxyUser", "proxyPassword")
          .build();

      ApiResponse<Object> response =
          client.invokeAPI("/test", "GET", Collections.emptyList(), null, Collections.emptyMap(),
              Collections.emptyMap(), null, "application/json", "", null, null);

      assertEquals(200, response.getStatusCode());
      assertTrue(proxy.getRequestCount() >= 2, "expected a 407 challenge followed by an authenticated retry");
      String expectedAuthHeader =
          "Basic " + Base64.getEncoder().encodeToString("proxyUser:proxyPassword".getBytes());
      assertEquals(expectedAuthHeader, proxy.getLastProxyAuthorizationHeader());
    }
  }

  /**
   * Directly exercises the {@code Authenticator} anonymous inner class {@link ApiClientBuilderJdk#configureProxy}
   * registers, using {@link Authenticator#requestPasswordAuthentication} (the public dispatch entry point the
   * JDK's own {@code HttpClient} internals use) to simulate both a proxy challenge and a non-proxy (server)
   * challenge, without needing a real 401/407 round-trip.
   */
  @Test
  void proxyAuthenticator_answersProxyChallengeAndIgnoresServerChallenge() throws IOException {
    ApiClientJdk client = (ApiClientJdk) new ApiClientBuilderJdk()
        .withBasePath("http://localhost:" + this.mockServer.getPort())
        .withProxy("localhost", 12345)
        .withProxyCredentials("proxyUser", null)
        .build();

    HttpClient httpClient = client.httpClient;
    Authenticator authenticator = httpClient.authenticator().orElseThrow();

    PasswordAuthentication proxyAuth = Authenticator.requestPasswordAuthentication(
        authenticator, "localhost", InetAddress.getLoopbackAddress(), 12345, "http", "prompt", "basic", null,
        Authenticator.RequestorType.PROXY);
    assertEquals("proxyUser", proxyAuth.getUserName());
    assertEquals(0, proxyAuth.getPassword().length);

    PasswordAuthentication serverAuth = Authenticator.requestPasswordAuthentication(
        authenticator, "localhost", InetAddress.getLoopbackAddress(), 80, "http", "prompt", "basic", null,
        Authenticator.RequestorType.SERVER);
    assertNull(serverAuth);
  }

  private ByteArrayOutputStream getMockServerKeyStore()
      throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
    KeyStore mockServerKeyStore = new KeyStoreFactory(new MockServerLogger()).loadOrCreateKeyStore();
    ByteArrayOutputStream keyStoreData = new ByteArrayOutputStream();
    mockServerKeyStore.store(keyStoreData, KEY_STORE_PWD.toCharArray());
    return keyStoreData;
  }
}
