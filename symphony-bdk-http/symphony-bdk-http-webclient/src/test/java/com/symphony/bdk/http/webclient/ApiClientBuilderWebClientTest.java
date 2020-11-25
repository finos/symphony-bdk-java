package com.symphony.bdk.http.webclient;

import static org.apache.commons.io.IOUtils.toByteArray;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.http.api.ApiClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ApiClientBuilderWebClientTest {

  private ApiClientBuilderWebClient builder;
  private byte[] truststore;
  private byte[] keystore;

  @BeforeEach
  void setUp() throws IOException {
    this.builder = new ApiClientBuilderWebClient();
    this.builder.withBasePath("test-base-path");
    this.builder.withDefaultHeader("sessionToken", "default-session-token");
    this.builder.withUserAgent("test-user-agent");
    this.builder.withTemporaryFolderPath("temp-path");
    this.builder.withConnectionTimeout(20_000);
    this.builder.withReadTimeout(30_000);
    this.builder.withProxy("proxy.symphony.com", 1234);
    this.builder.withProxyCredentials("user", "password");

    this.truststore = toByteArray(ApiClientBuilderWebClientTest.class
        .getResourceAsStream("/certs/all_symphony_certs_truststore"));
    this.keystore = toByteArray(ApiClientBuilderWebClient.class
        .getResourceAsStream("/certs/identity.p12"));
  }

  @Test
  void buildTest() {
    builder.withTrustStore(truststore, "changeit");
    builder.withKeyStore(keystore, "password");

    ApiClient apiClient = builder.build();

    assertEquals(apiClient.getClass(), ApiClientWebClient.class);
    assertEquals(apiClient.getBasePath(), "test-base-path");
  }

  @Test
  void buildTestWithKeyStoreWrongPassword() {
    builder.withTrustStore(truststore, "changeit");
    builder.withKeyStore(keystore, "wrongPassword");

    assertThrows(RuntimeException.class, this.builder::build);
  }
}
