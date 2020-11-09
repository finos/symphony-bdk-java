package com.symphony.bdk.core.client.loadbalancing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.client.exception.ApiClientInitializationException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkLoadBalancingConfig;
import com.symphony.bdk.core.config.model.BdkLoadBalancingMode;
import com.symphony.bdk.core.config.model.BdkServerConfig;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.api.util.TypeReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class RegularLoadBalancedApiClientTest {

  private BdkConfig config;
  private ApiClientFactory apiClientFactory;
  private ApiClient apiClient;

  private String path = "path";
  private String method = "POST";
  private List<Pair> queryParams = Collections.singletonList(new Pair("param", "value"));
  private String body = "body";
  private Map<String, String> headerParams = Collections.singletonMap("header", "value");
  private Map<String, String> cookieParams = Collections.singletonMap("cookie", "value");
  private Map<String, Object> formParams = Collections.singletonMap("form", "value");
  private String accept = "accept";
  private String contentType = "content type";
  private String[] authNames = new String[] {"authNames"};
  private TypeReference<String> returnType = new TypeReference<String>() {};

  @BeforeEach
  public void setUp() {
    this.apiClient = mock(ApiClient.class);

    this.apiClientFactory = mock(ApiClientFactory.class);
    when(this.apiClientFactory.getRegularAgentClient(any())).thenReturn(this.apiClient);

    this.config = getConfig();
  }

  private BdkConfig getConfig() {
    final BdkServerConfig serverConfig = new BdkServerConfig();
    serverConfig.setHost("agent-host");

    final BdkLoadBalancingConfig agentLoadBalancing = new BdkLoadBalancingConfig();
    agentLoadBalancing.setMode(BdkLoadBalancingMode.ROUND_ROBIN);
    agentLoadBalancing.setNodes(Collections.singletonList(serverConfig));

    final BdkConfig config = new BdkConfig();
    config.setAgents(agentLoadBalancing);
    return config;
  }

  @Test
  public void configWithLbAndAgentDefinedShouldFail() {
    config.getAgent().setHost("global-host");
    ApiClientInitializationException exception = assertThrows(ApiClientInitializationException.class,
        () -> new RegularLoadBalancedApiClient(config, apiClientFactory));

    assertEquals("Both agent and agents fields are defined", exception.getMessage());
  }

  @Test
  public void configWithMissingModeShouldFail() {
    config.getAgents().setMode(null);
    ApiClientInitializationException exception = assertThrows(ApiClientInitializationException.class,
        () -> new RegularLoadBalancedApiClient(config, apiClientFactory));

    assertEquals("Field \"mode\" in agents is mandatory", exception.getMessage());
  }

  @Test
  public void configWithMissingNodesShouldFail() {
    config.getAgents().setNodes(null);
    ApiClientInitializationException exception = assertThrows(ApiClientInitializationException.class,
        () -> new RegularLoadBalancedApiClient(config, apiClientFactory));

    assertEquals("Field \"nodes\" in agents is mandatory and must contain at least one element",
        exception.getMessage());
  }

  @Test
  public void configWithEmptyNodesListShouldFail() {
    config.getAgents().setNodes(Collections.emptyList());
    ApiClientInitializationException exception = assertThrows(ApiClientInitializationException.class,
        () -> new RegularLoadBalancedApiClient(config, apiClientFactory));

    assertEquals("Field \"nodes\" in agents is mandatory and must contain at least one element",
        exception.getMessage());
  }

  @Test
  public void testInvokeApiIsDelegatedAndRotateNotCalledWhenSticky() throws ApiException {
    config.getAgents().setStickiness(true);
    RegularLoadBalancedApiClient loadBalancedApiClient =
        spy(new RegularLoadBalancedApiClient(config, apiClientFactory));

    loadBalancedApiClient.invokeAPI(path, method, queryParams, body, headerParams, cookieParams, formParams, accept,
        contentType, authNames, returnType);

    verify(apiClientFactory).getRegularAgentClient(eq("https://agent-host:443"));
    verify(apiClient).invokeAPI(eq(path), eq(method), eq(queryParams), eq(body), eq(headerParams), eq(cookieParams),
        eq(formParams), eq(accept), eq(contentType), eq(authNames), eq(returnType));
    verify(loadBalancedApiClient, times(0)).rotate();
  }

  @Test
  public void testInvokeApiIsDelegatedAndRotateCalledWhenNonSticky() throws ApiException {
    config.getAgents().setStickiness(false);
    RegularLoadBalancedApiClient loadBalancedApiClient =
        spy(new RegularLoadBalancedApiClient(config, apiClientFactory));

    loadBalancedApiClient.invokeAPI(path, method, queryParams, body, headerParams, cookieParams, formParams, accept,
        contentType, authNames, returnType);

    //getRegularAgentClient called by loadBalancedApiClient.rotate() in constructor and in invokeApi
    verify(apiClientFactory, times(2)).getRegularAgentClient(eq("https://agent-host:443"));
    verify(apiClient).invokeAPI(eq(path), eq(method), eq(queryParams), eq(body), eq(headerParams), eq(cookieParams),
        eq(formParams), eq(accept), eq(contentType), eq(authNames), eq(returnType));
    verify(loadBalancedApiClient, times(1)).rotate();
  }
}
