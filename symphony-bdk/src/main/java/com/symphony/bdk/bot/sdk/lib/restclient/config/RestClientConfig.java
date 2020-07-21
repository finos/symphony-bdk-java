package com.symphony.bdk.bot.sdk.lib.restclient.config;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.symphony.bdk.bot.sdk.lib.restclient.RestClient;
import com.symphony.bdk.bot.sdk.lib.restclient.RestClientConnectionException;
import com.symphony.bdk.bot.sdk.lib.restclient.RestClientImpl;

import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

/**
 * Creates and configures an instance of the RestTemplate-based implementation
 * of the {@link RestClient} if no other implementation is provided.
 *
 * @author Marcus Secato
 *
 */
@Configuration
public class RestClientConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(RestClientConfig.class);

  @Value("${restclient.proxy.address:}")
  private String proxyAddress;

  @Value("${restclient.proxy.port:-1}")
  private Integer proxyPort;

  @Value("${restclient.timeout:-1}")
  private Integer connectionTimeout;

  @Bean
  //@ConditionalOnProperty(value = "restclient.disabled", havingValue="false", matchIfMissing=true)
  @ConditionalOnMissingBean
  public RestClient initDefaultRestClient() {
    LOGGER.info("Initializing REST client");
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

    if (proxyAddress.length() > 0 && proxyPort > 0) {
      LOGGER.info("Proxy configuration detected: {} (address) {} (port)",
          proxyAddress, proxyPort);

      requestFactory.setProxy(new Proxy(Proxy.Type.HTTP,
          new InetSocketAddress(proxyAddress, proxyPort)));
    }

    if (connectionTimeout > 0) {
      LOGGER.info("Setting read and connect timeouts to: {} ms", connectionTimeout);
      requestFactory.setReadTimeout(connectionTimeout);
      requestFactory.setConnectTimeout(connectionTimeout);
    }

    CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
        .slidingWindowSize(20)
        .waitDurationInOpenState(Duration.ofSeconds(30))
        .recordExceptions(RestClientConnectionException.class)
        .build();

    BulkheadConfig bhConfig = BulkheadConfig.custom()
        .maxConcurrentCalls(20)
        .maxWaitDuration(Duration.ofMillis(500))
        .build();

    return new RestClientImpl(new RestTemplate(requestFactory),
        cbConfig, bhConfig);
  }
}

