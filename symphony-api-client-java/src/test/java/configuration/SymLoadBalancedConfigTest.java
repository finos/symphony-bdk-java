package configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import exceptions.SymClientException;
import it.commons.BaseTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static final Logger logger = LoggerFactory.getLogger(SymLoadBalancedConfig.class);

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
    final String loadBalancedUrl = "agent-1";

    final SymLoadBalancedConfig config = spy(getSymLoadBalancedConfig(Collections.singletonList("agent"), LoadBalancingMethod.external));
    doReturn(loadBalancedUrl).when(config).getActualAgentHost(anyString());

    assertEquals(loadBalancedUrl, config.getAgentHost());
  }

  @Test
  public void testGetAgentWithExternalLoadBalancingAndNoAgentServer() {
    final SymLoadBalancedConfig config = getSymLoadBalancedConfig(Collections.emptyList(), LoadBalancingMethod.external);

    assertThrows(SymClientException.class, () -> config.getAgentHost());
  }

  @Test
  public void testGetAgentWithExternalLoadBalancingAndSeveralAgentServers() {
    final SymLoadBalancedConfig config = getSymLoadBalancedConfig(Arrays.asList("agent-1", "agent-2"), LoadBalancingMethod.external);

    assertThrows(SymClientException.class, () -> config.getAgentHost());
  }

  @Test
  public void testGetAgentWithRandomLoadBalancing() {
    final SymLoadBalancedConfig config = getSymLoadBalancedConfig(Collections.singletonList("agent-1"), LoadBalancingMethod.random);

    assertEquals("agent-1", config.getAgentHost());
  }

  @Test
  public void testGetAgentWithRoundRobinLoadBalancing() {
    final SymLoadBalancedConfig config = getSymLoadBalancedConfig(Collections.singletonList("agent-1"), LoadBalancingMethod.roundrobin);

    assertEquals("agent-1", config.getAgentHost());
  }

  private SymLoadBalancedConfig getSymLoadBalancedConfig(List<String> agentServers, LoadBalancingMethod method) {
    final SymLoadBalancedConfig config = new SymLoadBalancedConfig();

    final LoadBalancing loadBalancing = new LoadBalancing();
    loadBalancing.setMethod(LoadBalancingMethod.valueOf(method.name()));

    config.setAgentServers(agentServers);
    config.setLoadBalancing(loadBalancing);
    return config;
  }

  @Test
  public void testRotateAgentExternalSizeNot1() {
    final SymLoadBalancedConfig config = getSymLoadBalancedConfig(Arrays.asList("agent-1", "agent-2", "agent-3"), LoadBalancingMethod.external);
    assertThrows(SymClientException.class, () -> config.rotateAgent());
  }

  @Test
  public void testRotateAgentExternalSize1() {
    final String agent = "agent-1";

    final SymLoadBalancedConfig config = spy(getSymLoadBalancedConfig(Collections.singletonList(agent), LoadBalancingMethod.external));
    doReturn(agent).when(config).getActualAgentHost(anyString());
    assertEquals(agent, config.getAgentHost());

    config.rotateAgent();
    assertEquals(agent, config.getAgentHost());
  }

  @Test
  public void testRotateAgentRandom() {
    final String agent = "agent-1";

    final SymLoadBalancedConfig config = spy(getSymLoadBalancedConfig(Collections.singletonList(agent), LoadBalancingMethod.random));
    doReturn(agent).when(config).getActualAgentHost(anyString());
    assertEquals(agent, config.getAgentHost());

    config.rotateAgent();
    assertEquals(agent, config.getAgentHost());
  }

  @Test
  public void testRotateAgentRoundRobin() {
    final String agent = "agent-1";

    final SymLoadBalancedConfig config = spy(getSymLoadBalancedConfig(Collections.singletonList(agent), LoadBalancingMethod. roundrobin));
    doReturn(agent).when(config).getActualAgentHost(anyString());
    assertEquals(agent, config.getAgentHost());

    config.rotateAgent();
    assertEquals(agent, config.getAgentHost());
  }
}
