package com.symphony.bdk.core.client.loadbalancing;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.gen.api.model.AgentInfo;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExternalLoadBalancingStrategyTest {

  private SignalsApi signalsApi;
  private ExternalLoadBalancingStrategy loadBalancingStrategy;

  @BeforeEach
  public void setUp() {
    signalsApi = mock(SignalsApi.class);
    loadBalancingStrategy = new ExternalLoadBalancingStrategy(ofMinimalInterval(1), signalsApi);
    ApiClient apiClient = mock(ApiClient.class);
    when(signalsApi.getApiClient()).thenReturn(apiClient);
    when(apiClient.getBasePath()).thenReturn("pathToTheAgent");
  }

  @Test
  public void testNominalCase() throws ApiException {
    when(signalsApi.v1InfoGet()).thenReturn(new AgentInfo().serverFqdn("https://agent1.symphony.com"));

    assertEquals("https://agent1.symphony.com", loadBalancingStrategy.getNewBasePath());
  }

  @Test
  public void testCaseWithTrailingSlash() throws ApiException {
    when(signalsApi.v1InfoGet()).thenReturn(new AgentInfo().serverFqdn("https://agent1.symphony.com/"));

    assertEquals("https://agent1.symphony.com", loadBalancingStrategy.getNewBasePath());
  }

  @Test
  public void testCaseWithoutScheme() throws ApiException {
    when(signalsApi.v1InfoGet()).thenReturn(new AgentInfo().serverFqdn("agent1.symphony.com/"));

    assertEquals("https://agent1.symphony.com", loadBalancingStrategy.getNewBasePath());
  }

}
