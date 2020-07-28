package configuration;

import static org.junit.Assert.assertEquals;

import it.commons.BaseTest;
import org.junit.Test;

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
}