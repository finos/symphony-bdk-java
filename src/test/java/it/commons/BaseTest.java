package it.commons;

import java.io.InputStream;
import org.junit.BeforeClass;
import configuration.SymConfig;
import configuration.SymConfigLoader;

public class BaseTest {
  protected static SymConfig config;

  @BeforeClass
  public static void setUp() {
    InputStream configFileStream = BaseTest.class.getResourceAsStream("/bot-config.json");
    config = SymConfigLoader.load(configFileStream);
  }
}
