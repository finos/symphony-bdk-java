package configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import exceptions.SymClientException;
import it.commons.BaseTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Test class for the {@link SymLoadBalancedConfig}.
 *
 * @author Thibault Pensec
 * @since 10/03/2020
 */
public class SymLoadBalancedConfigTest extends BaseTest {

  @Test
  public void testCloneAttributes() {

    final SymConfig other = new SymConfig();
    other.setPodHost("localhost");
    other.setPodPort(8080);

    final SymLoadBalancedConfig loadBalancedConfig = new SymLoadBalancedConfig();
    loadBalancedConfig.cloneAttributes(other);

    assertEquals("localhost", loadBalancedConfig.getPodHost());
    assertEquals(8080, loadBalancedConfig.getPodPort());
  }

  @Test
  public void testGetAgentWithExternalLoadBalancing() {
    String loadBalancedUrl = "agent-1";

    final SymLoadBalancedConfig config = spy(getSymLoadBalancedConfig(Collections.singletonList("agent")));
    doReturn(loadBalancedUrl).when(config).getActualAgentHost(anyString());

    assertEquals(loadBalancedUrl, config.getAgentHost());
  }

  @Test
  public void testGetAgentWithExternalLoadBalancingAndNoAgentServer() {
    final SymLoadBalancedConfig config = getSymLoadBalancedConfig(Collections.emptyList());

    assertThrows(SymClientException.class, () -> config.getAgentHost());
  }

  @Test
  public void testGetAgentWithExternalLoadBalancingAndSeveralAgentServers() {
    final SymLoadBalancedConfig config = getSymLoadBalancedConfig(Arrays.asList("agent-1", "agent-2"));

    assertThrows(SymClientException.class, () -> config.getAgentHost());
  }

  private SymLoadBalancedConfig getSymLoadBalancedConfig(List<String> agentServers) {
    final SymLoadBalancedConfig config = new SymLoadBalancedConfig();

    final LoadBalancing loadBalancing = new LoadBalancing();
    loadBalancing.setMethod(LoadBalancingMethod.external);

    config.setAgentServers(agentServers);
    config.setLoadBalancing(loadBalancing);
    return config;
  }
}
