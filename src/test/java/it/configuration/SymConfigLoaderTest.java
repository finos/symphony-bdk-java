package it.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import it.commons.BaseTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SymConfigLoaderTest {

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Test
  public void loadConfig() {
    InputStream configFileStream = BaseTest.class.getResourceAsStream("/bot-config.json");
    SymConfig config = SymConfigLoader.load(configFileStream);

    assertNotNull(config);
    assertEquals("localhost", config.getSessionAuthHost());
  }

  @Test
  public void nullDatafeedIdFilePathShouldReturnCurrentFolder() throws JsonProcessingException {
    assertInputProducesDatafeedIdPath(null, "." + File.separator);
  }

  @Test
  public void emptyDatafeedIdFilePathShouldReturnCurrentFolder() throws JsonProcessingException {
    assertInputProducesDatafeedIdPath("", "." + File.separator);
  }

  @Test
  public void nonEmptyDatafeedIdFilePathShouldAppendSeparator() throws JsonProcessingException {
    String folder = "folder";
    assertInputProducesDatafeedIdPath(folder, folder + File.separator);
  }

  @Test
  public void nonEmptyDatafeedIdFilePathWithSeparatorShouldReturnTheSame() throws JsonProcessingException {
    String folder = "folder" + File.separator;
    assertInputProducesDatafeedIdPath(folder, folder);
  }

  private void assertInputProducesDatafeedIdPath(String inputFolder, String expected) throws JsonProcessingException {
    Map<String, Object> params = new HashMap<>();
    params.put("datafeedIdFilePath", inputFolder);

    String payload = OBJECT_MAPPER.writeValueAsString(params);

    InputStream configStream = new ByteArrayInputStream(payload.getBytes());
    SymConfig config = SymConfigLoader.load(configStream);

    assertNotNull(config);
    assertEquals(expected, config.getDatafeedIdFilePath());
  }
}
