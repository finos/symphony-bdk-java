package com.symphony.bdk.core.client.lb;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkLoadBalancingConfig;
import com.symphony.bdk.core.config.model.BdkLoadBalancingMode;
import com.symphony.bdk.core.config.model.BdkServerConfig;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.gen.api.model.AgentInfo;
import com.symphony.bdk.http.api.ApiException;

import com.symphony.bdk.http.api.ApiRuntimeException;

import com.symphony.bdk.http.api.RegularApiClient;

import com.symphony.bdk.http.api.util.TypeReference;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoadBalancingStrategyTest {

  @Test
  public void testNewInstanceRandomLB() {
    final LoadBalancingStrategy loadBalancingStrategy = getLoadBalancingStrategy(BdkLoadBalancingMode.RANDOM);
    assertEquals(RandomLoadBalancingStrategy.class, loadBalancingStrategy.getClass());
  }

  @Test
  public void testNewInstanceRoundRobinLB() {
    final LoadBalancingStrategy loadBalancingStrategy = getLoadBalancingStrategy(BdkLoadBalancingMode.ROUND_ROBIN);
    assertEquals(RoundRobinLoadBalancingStrategy.class, loadBalancingStrategy.getClass());
  }

  @Test
  public void testNewInstanceExternalLB() {
    final LoadBalancingStrategy loadBalancingStrategy =
        getLoadBalancingStrategy(BdkLoadBalancingMode.EXTERNAL, Arrays.asList(""));
    assertEquals(ExternalLoadBalancingStrategy.class, loadBalancingStrategy.getClass());
  }

  @Test
  public void testRoundRobinLbStrategy() {
    final LoadBalancingStrategy loadBalancingStrategy = getLoadBalancingStrategy(BdkLoadBalancingMode.ROUND_ROBIN,
        Arrays.asList("agent1", "agent2", "agent3"));

    List<String> basePaths = Stream.generate(() -> loadBalancingStrategy.getNewBasePath()).limit(4)
        .collect(Collectors.toList());

    assertEquals(Arrays.asList("https://agent1:443", "https://agent2:443", "https://agent3:443", "https://agent1:443"),
        basePaths);
  }

  @Test
  public void testRandomLbStrategy() {
    final LoadBalancingStrategy loadBalancingStrategy = getLoadBalancingStrategy(BdkLoadBalancingMode.RANDOM,
        Arrays.asList("agent1", "agent2", "agent3"));

    Map<String, Long> basePaths = Stream.generate(() -> loadBalancingStrategy.getNewBasePath()).limit(1000)
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    assertTrue(basePaths.get("https://agent1:443") > 200);
    assertTrue(basePaths.get("https://agent2:443") > 200);
    assertTrue(basePaths.get("https://agent3:443") > 200);
  }

  @Test
  public void testExternalLbWithApiClientMock() {
    BdkServerConfig serverConfig = new BdkServerConfig();
    serverConfig.setHost("agent-lb");

    final BdkLoadBalancingConfig loadBalancingConfig = new BdkLoadBalancingConfig();
    loadBalancingConfig.setNodes(Arrays.asList(serverConfig));
    loadBalancingConfig.setMode(BdkLoadBalancingMode.EXTERNAL);

    MockApiClient mockApiClient = new MockApiClient();
    mockApiClient.onGet("/agent/v1/info", "{ \"serverFqdn\": \"https://agent1:443/context\" }");

    ApiClientFactory apiClientFactory = mock(ApiClientFactory.class);
    when(apiClientFactory.getRegularAgentClient(eq("https://agent-lb:443")))
        .thenReturn(mockApiClient.getApiClient("/agent"));

    final LoadBalancingStrategy instance = LoadBalancingStrategy.getInstance(loadBalancingConfig, apiClientFactory);

    assertEquals("https://agent1:443/context", instance.getNewBasePath());
  }

  @Test
  public void testExternalLbStrategy() throws ApiException {
    SignalsApi signalsApi = mock(SignalsApi.class);
    when(signalsApi.v1InfoGet())
        .thenReturn(new AgentInfo().serverFqdn("https://agent1:443"))
        .thenReturn(new AgentInfo().serverFqdn("https://agent2:443"))
        .thenReturn(new AgentInfo().serverFqdn("https://agent3:443"));

    ExternalLoadBalancingStrategy loadBalancingStrategy = new ExternalLoadBalancingStrategy(signalsApi);

    List<String> basePaths = Stream.generate(() -> loadBalancingStrategy.getNewBasePath()).limit(3)
        .collect(Collectors.toList());

    assertEquals(Arrays.asList("https://agent1:443", "https://agent2:443", "https://agent3:443"), basePaths);
  }

  @Test
  public void testExternalLbStrategyWithError() throws ApiException {
    SignalsApi signalsApi = mock(SignalsApi.class);
    when(signalsApi.v1InfoGet())
        .thenThrow(new ApiException(500, "error"));

    ExternalLoadBalancingStrategy loadBalancingStrategy = new ExternalLoadBalancingStrategy(signalsApi);

    assertThrows(ApiRuntimeException.class, () -> loadBalancingStrategy.getNewBasePath());
  }

  private LoadBalancingStrategy getLoadBalancingStrategy(BdkLoadBalancingMode mode) {
    return getLoadBalancingStrategy(mode, Arrays.asList());
  }

  private LoadBalancingStrategy getLoadBalancingStrategy(BdkLoadBalancingMode mode, List<String> hosts) {
    final List<BdkServerConfig> servers = hosts.stream().map(s -> {
      BdkServerConfig serverConfig = new BdkServerConfig();
      serverConfig.setHost(s);
      return serverConfig;
    }).collect(Collectors.toList());

    final BdkLoadBalancingConfig loadBalancingConfig = new BdkLoadBalancingConfig();
    loadBalancingConfig.setNodes(servers);
    loadBalancingConfig.setMode(mode);

    return LoadBalancingStrategy.getInstance(loadBalancingConfig, new ApiClientFactory(new BdkConfig()));
  }
}
