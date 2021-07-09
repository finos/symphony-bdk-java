package com.symphony.bdk.http.webclient;

import static org.apache.commons.io.IOUtils.toByteArray;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.util.ApiUtils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.io.IOException;
import java.util.List;

class ApiClientBuilderWebClientTest {

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
    Logger logger = (Logger) LoggerFactory.getLogger(ApiUtils.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    builder.withTrustStore(truststore, "changeit");
    builder.withKeyStore(keystore, "password");

    ApiClient apiClient = builder.build();

    assertEquals(apiClient.getClass(), ApiClientWebClient.class);
    assertEquals(apiClient.getBasePath(), "test-base-path");

    // assert logs about truststore entries
    List<ILoggingEvent> logsList = listAppender.list;
    assertFalse(logsList.isEmpty(), "The list of log entries should not be empty");
    assertEquals("Loading {} from truststore", logsList.get(0).getMessage(),
        "The list of logs should have at least on entry about one loaded cert");
    assertEquals(Level.DEBUG, logsList.get(0).getLevel(), "The entry level should be DEBUG");
  }

  @Test
  void buildTestWithKeyStoreWrongPassword() {
    builder.withTrustStore(truststore, "changeit");
    builder.withKeyStore(keystore, "wrongPassword");

    assertThrows(RuntimeException.class, this.builder::build);
  }

  @Test
  void addFilterWrongImplementation() {
    assertThrows(IllegalArgumentException.class, () -> builder.addFilter(new Object()));
  }

  @Test
  void addFilterCorrectImplementation() {
    builder.addFilter((ExchangeFilterFunction) (request, next) -> next.exchange(request));

    assertDoesNotThrow(this.builder::build);
  }
}
