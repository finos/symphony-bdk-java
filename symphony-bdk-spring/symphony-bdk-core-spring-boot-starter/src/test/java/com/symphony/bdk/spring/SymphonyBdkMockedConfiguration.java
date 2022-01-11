package com.symphony.bdk.spring;

import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.http.api.ApiClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;

/**
 *
 */
@Configuration
public class SymphonyBdkMockedConfiguration {

  @Bean
  public MockApiClient agentMockApiClient() {
    return new MockApiClient();
  }

  @Bean
  public MockApiClient podMockApiClient() {
    return new MockApiClient();
  }

  @Bean
  public MockApiClient relayMockApiClient() {
    return new MockApiClient();
  }

  @Bean
  public MockApiClient loginMockApiClient() {
    return new MockApiClient();
  }

  @Bean
  public ApiClientFactory apiClientFactory(SymphonyBdkCoreProperties properties) {
    return new ApiClientFactoryMock(properties);
  }

  public static class ApiClientFactoryMock extends ApiClientFactory {

    @Autowired
    @Qualifier("agentMockApiClient")
    private MockApiClient agentApiClient;

    @Autowired
    @Qualifier("podMockApiClient")
    private MockApiClient podApiClient;

    @Autowired
    @Qualifier("relayMockApiClient")
    private MockApiClient relayApiClient;

    @Autowired
    @Qualifier("loginMockApiClient")
    private MockApiClient loginApiClient;

    public ApiClientFactoryMock(@Nonnull BdkConfig config) {
      super(config);
    }

    @Override
    public ApiClient getAgentClient() {
      return this.agentApiClient.getApiClient("/agent");
    }

    @Override
    public ApiClient getPodClient() {

      this.podApiClient.onGet("/pod/v2/sessioninfo", toString(new UserV2().displayName("BotMention")));

      return this.podApiClient.getApiClient("/pod");
    }

    @Override
    public ApiClient getRelayClient() {

      this.relayApiClient.onPost("/relay/pubkey/authenticate", "{ \"token\":\"123456789\", \"name\":\"keyManagerToken\" }");

      return this.relayApiClient.getApiClient("/relay");
    }

    @Override
    public ApiClient getLoginClient() {

      this.loginApiClient.onPost("/login/pubkey/authenticate", "{ \"token\":\"123456789\", \"name\":\"sessionToken\" }");

      return this.loginApiClient.getApiClient("/login");
    }

    @SneakyThrows
    private static String toString(Object o) {
      final ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(o);
    }
  }
}
