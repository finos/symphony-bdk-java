package it.configuration;

import configuration.SymConfig;
import configuration.SymConfigLoader;
import it.commons.BaseTest;

import java.io.ByteArrayInputStream;
import java.io.File;
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

  @Test
  public void nullDatafeedIdFilePathShouldReturnCurrentFolder() {
    assertInputProducesDatafeedIdPath("{}", "." + File.separator);
  }

  @Test
  public void emptyDatafeedIdFilePathShouldReturnCurrentFolder() {
    assertInputProducesDatafeedIdPath("{ \"datafeedIdFilePath\": \"\"}", "." + File.separator);
  }

  @Test
  public void nonEmptyDatafeedIdFilePathShouldAppendSeparator() {
    String folder = "folder";
    assertInputProducesDatafeedIdPath("{ \"datafeedIdFilePath\": \"" + folder + "\"}", folder + File.separator);
  }

  @Test
  public void nonEmptyDatafeedIdFilePathWithSeparatorShouldReturnTheSame() {
    String folder = "folder/";
    assertInputProducesDatafeedIdPath("{ \"datafeedIdFilePath\": \"" + folder + "\"}", folder);
  }

  private void assertInputProducesDatafeedIdPath(String configFileContent, String expected) {
    InputStream configStream = new ByteArrayInputStream(configFileContent.getBytes());
    SymConfig config = SymConfigLoader.load(configStream);

    assertNotNull(config);
    assertEquals(expected, config.getDatafeedIdFilePath());
  }
}
