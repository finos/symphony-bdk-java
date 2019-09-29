package com.symphony.ms.songwriter.internal.lib.restclient.config;

import java.net.InetSocketAddress;
import java.net.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import com.symphony.ms.songwriter.internal.lib.restclient.RestClient;
import com.symphony.ms.songwriter.internal.lib.restclient.RestClientImpl;

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

    return new RestClientImpl(new RestTemplate(requestFactory));
  }
}

