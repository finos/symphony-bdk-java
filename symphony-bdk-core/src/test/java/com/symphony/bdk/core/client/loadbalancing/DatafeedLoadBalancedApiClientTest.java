package com.symphony.bdk.core.client.loadbalancing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkLoadBalancingConfig;
import com.symphony.bdk.core.config.model.BdkLoadBalancingMode;
import com.symphony.bdk.core.config.model.BdkServerConfig;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.api.RegularApiClient;
import com.symphony.bdk.http.api.util.TypeReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class DatafeedLoadBalancedApiClientTest {

  private BdkConfig config;
  private ApiClientFactory apiClientFactory;
  private RegularApiClient apiClient;

  @BeforeEach
  public void setUp() {
    this.apiClient = mock(RegularApiClient.class);

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
    config.setAgentLoadBalancing(agentLoadBalancing);
    return config;
  }

  @Test
  public void testInvokeApiIsDelegated() throws ApiException {
    DatafeedLoadBalancedApiClient loadBalancedApiClient =
        spy(new DatafeedLoadBalancedApiClient(config, apiClientFactory));

    final String path = "path";
    final String method = "POST";
    final List<Pair> queryParams = Collections.singletonList(new Pair("param", "value"));
    final String body = "body";
    final Map<String, String> headerParams = Collections.singletonMap("header", "value");
    final Map<String, String> cookieParams = Collections.singletonMap("cookie", "value");
    final Map<String, Object> formParams = Collections.singletonMap("form", "value");
    final String accept = "accept";
    final String contentType = "content type";
    final String[] authNames = {"authNames"};
    final TypeReference<String> returnType = new TypeReference<String>() {};

    loadBalancedApiClient.invokeAPI(path, method, queryParams, body, headerParams, cookieParams, formParams, accept,
        contentType, authNames, returnType);

    verify(apiClientFactory).getRegularAgentClient(eq("https://agent-host:443"));
    verify(apiClient).invokeAPI(eq(path), eq(method), eq(queryParams), eq(body), eq(headerParams), eq(cookieParams),
        eq(formParams), eq(accept), eq(contentType), eq(authNames), eq(returnType));
    verify(loadBalancedApiClient, times(0)).rotate();
  }

  @Test
  public void testParameterToStringIsDelegated() {
    DatafeedLoadBalancedApiClient loadBalancedApiClient = new DatafeedLoadBalancedApiClient(config, apiClientFactory);
    final String param = "param";

    loadBalancedApiClient.parameterToString(param);

    verify(apiClient).parameterToString(eq(param));
  }

  @Test
  public void testParameterToPairsIsDelegated() {
    DatafeedLoadBalancedApiClient loadBalancedApiClient = new DatafeedLoadBalancedApiClient(config, apiClientFactory);

    final String format = "format";
    final String name = "name";
    final String value = "value";

    loadBalancedApiClient.parameterToPairs(format, name, value);

    verify(apiClient).parameterToPairs(eq(format), eq(name), eq(value));
  }

  @Test
  public void testSelectHeaderAcceptsIsDelegated() {
    DatafeedLoadBalancedApiClient loadBalancedApiClient = new DatafeedLoadBalancedApiClient(config, apiClientFactory);

    final String value = "value";
    loadBalancedApiClient.selectHeaderAccept(value);

    verify(apiClient).selectHeaderAccept(eq(value));
  }

  @Test
  public void testSelectHeaderContentTypeIsDelegated() {
    DatafeedLoadBalancedApiClient loadBalancedApiClient = new DatafeedLoadBalancedApiClient(config, apiClientFactory);

    final String value = "value";
    loadBalancedApiClient.selectHeaderContentType(value);

    verify(apiClient).selectHeaderContentType(eq(value));
  }

  @Test
  public void testEscapeStringIsDelegated() {
    DatafeedLoadBalancedApiClient loadBalancedApiClient = new DatafeedLoadBalancedApiClient(config, apiClientFactory);

    final String value = "value";
    loadBalancedApiClient.escapeString(value);

    verify(apiClient).escapeString(eq(value));
  }
}
