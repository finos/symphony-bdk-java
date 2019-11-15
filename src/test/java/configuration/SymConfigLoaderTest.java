package configuration;

import static org.junit.Assert.assertEquals;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import java.io.ByteArrayInputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SymConfigLoaderTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void loadConfigTest() throws Exception {
    // Arrange
    String configPath = "aaaaa";

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymConfigLoader.loadConfig(configPath);
  }

  @Test
  public void loadConfigTest2() throws Exception {
    // Arrange
    String configPath = "aaaaa";
    Class<SymConfig> clazz = null;

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymConfigLoader.<SymConfig>loadConfig(configPath, clazz);
  }

  @Test
  public void loadFromFileTest() throws Exception {
    // Arrange
    String path = "aaaaa";
    Class<SymConfig> clazz = null;

    // Act
    SymConfig actual = SymConfigLoader.<SymConfig>loadFromFile(path, clazz);

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void loadFromFileTest2() throws Exception {
    // Arrange
    String path = "aaaaa";

    // Act
    SymConfig actual = SymConfigLoader.loadFromFile(path);

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void loadLoadBalancerConfigTest() throws Exception {
    // Arrange
    String configPath = "aaaaa";

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymConfigLoader.loadLoadBalancerConfig(configPath);
  }

  @Test
  public void loadLoadBalancerFromFileTest() throws Exception {
    // Arrange
    String path = "aaaaa";

    // Act
    SymLoadBalancedConfig actual = SymConfigLoader.loadLoadBalancerFromFile(path);

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void loadLoadBalancerTest() throws Exception {
    // Arrange
    ByteArrayInputStream inputStream = new ByteArrayInputStream(
        new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

    // Act
    SymLoadBalancedConfig actual = SymConfigLoader.loadLoadBalancer(inputStream);

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void loadTest() throws Exception {
    // Arrange
    ByteArrayInputStream inputStream = new ByteArrayInputStream(
        new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    Class<SymConfig> clazz = null;

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymConfigLoader.<SymConfig>load(inputStream, clazz);
  }

  @Test
  public void loadTest2() throws Exception {
    // Arrange
    ByteArrayInputStream inputStream = new ByteArrayInputStream(
        new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

    // Act
    SymConfig actual = SymConfigLoader.load(inputStream);

    // Assert
    assertEquals(null, actual);
  }
}
