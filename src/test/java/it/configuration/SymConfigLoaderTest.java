package it.configuration;

import configuration.SymConfig;
import configuration.SymConfigLoader;
import it.commons.BaseTest;
import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SymConfigLoaderTest {
  @Test
  public void loadConfig() {
    InputStream configFileStream = BaseTest.class.getResourceAsStream("/bot-config.json");
    SymConfig config = SymConfigLoader.load(configFileStream);

    assertNotNull(config);
    assertEquals("localhost", config.getSessionAuthHost());
  }
}
