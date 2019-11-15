package configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import configuration.SymLoadBalancedConfig;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SymLoadBalancedConfigTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void SymLoadBalancedConfigTest() throws Exception {
    // Arrange and Act
    SymLoadBalancedConfig symLoadBalancedConfig = new SymLoadBalancedConfig();

    // Assert
    assertEquals(null, symLoadBalancedConfig.getAppCertName());
  }

  @Test
  public void cloneAttributesTest() throws Exception {
    // Arrange
    SymLoadBalancedConfig symLoadBalancedConfig = new SymLoadBalancedConfig();
    SymConfig config = new SymConfig();

    // Act
    symLoadBalancedConfig.cloneAttributes(config);

    // Assert
    String appCertName = symLoadBalancedConfig.getAppCertName();
    String truststorePath = symLoadBalancedConfig.getTruststorePath();
    String keyAuthHost = symLoadBalancedConfig.getKeyAuthHost();
    String appPrivateKeyName = symLoadBalancedConfig.getAppPrivateKeyName();
    int keyAuthPort = symLoadBalancedConfig.getKeyAuthPort();
    String podHost = symLoadBalancedConfig.getPodHost();
    assertEquals(null, appCertName);
    assertEquals("https://null:0", symLoadBalancedConfig.getPodUrl());
    assertEquals(null, podHost);
    assertEquals(0, keyAuthPort);
    assertEquals(null, appPrivateKeyName);
    assertEquals(null, keyAuthHost);
    assertEquals(null, truststorePath);
  }

  @Test
  public void getAgentHostTest() throws Exception {
    // Arrange
    SymLoadBalancedConfig symLoadBalancedConfig = new SymLoadBalancedConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    symLoadBalancedConfig.getAgentHost();
  }

  @Test
  public void getAgentPortTest() throws Exception {
    // Arrange
    SymLoadBalancedConfig symLoadBalancedConfig = new SymLoadBalancedConfig();

    // Act
    int actual = symLoadBalancedConfig.getAgentPort();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getAgentServersTest() throws Exception {
    // Arrange
    SymLoadBalancedConfig symLoadBalancedConfig = new SymLoadBalancedConfig();

    // Act
    List<String> actual = symLoadBalancedConfig.getAgentServers();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLoadBalancingTest() throws Exception {
    // Arrange
    SymLoadBalancedConfig symLoadBalancedConfig = new SymLoadBalancedConfig();

    // Act
    LoadBalancing actual = symLoadBalancedConfig.getLoadBalancing();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void rotateAgentTest() throws Exception {
    // Arrange
    SymLoadBalancedConfig symLoadBalancedConfig = new SymLoadBalancedConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    symLoadBalancedConfig.rotateAgent();
  }

  @Test
  public void setActualAgentHostTest() throws Exception {
    // Arrange
    SymLoadBalancedConfig symLoadBalancedConfig = new SymLoadBalancedConfig();
    String actualAgentHost = "aaaaa";

    // Act
    symLoadBalancedConfig.setActualAgentHost(actualAgentHost);

    // Assert
    assertEquals(null, symLoadBalancedConfig.getAppCertName());
  }

  @Test
  public void setAgentServersTest() throws Exception {
    // Arrange
    SymLoadBalancedConfig symLoadBalancedConfig = new SymLoadBalancedConfig();
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");

    // Act
    symLoadBalancedConfig.setAgentServers(arrayList);

    // Assert
    assertSame(arrayList, symLoadBalancedConfig.getAgentServers());
  }

  @Test
  public void setCurrentAgentIndexTest() throws Exception {
    // Arrange
    SymLoadBalancedConfig symLoadBalancedConfig = new SymLoadBalancedConfig();
    int currentAgentIndex = 1;

    // Act
    symLoadBalancedConfig.setCurrentAgentIndex(currentAgentIndex);

    // Assert
    assertEquals(null, symLoadBalancedConfig.getAppCertName());
  }

  @Test
  public void setLoadBalancingTest() throws Exception {
    // Arrange
    SymLoadBalancedConfig symLoadBalancedConfig = new SymLoadBalancedConfig();
    LoadBalancing loadBalancing = new LoadBalancing();

    // Act
    symLoadBalancedConfig.setLoadBalancing(loadBalancing);

    // Assert
    assertSame(loadBalancing, symLoadBalancedConfig.getLoadBalancing());
  }
}
