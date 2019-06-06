package it.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.InputStream;
import org.junit.Test;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import it.commons.BaseTest;

public class SymConfigLoaderTest {
  @Test
  public void loadConfig() {
    InputStream configFileStream = BaseTest.class.getResourceAsStream("/bot-config.json");
    SymConfig config = SymConfigLoader.load(configFileStream);

    assertNotNull(config);
    assertEquals("localhost", config.getSessionAuthHost());
  }
}
