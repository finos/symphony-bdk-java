package com.symphony.bdk.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.config.exception.BdkConfigException;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

public class BdkConfigParserTest {

  //this is a disgusting hack that should only be allowed in unit tests
  void hackEnvVar(String key, String value) throws NoSuchFieldException, IllegalAccessException {
    Map<String, String> env = System.getenv();
    Field field = env.getClass().getDeclaredField("m");
    field.setAccessible(true);
    if (value == null) {
      ((Map<String, String>) field.get(env)).remove(key);
    } else {
      ((Map<String, String>) field.get(env)).put(key, value);
    }
  }

  @BeforeEach
  void tearDown() {
    System.clearProperty("my.property");
    System.clearProperty("recursive");
    try {
      hackEnvVar("SYMPHONY_BDK_UNIT_TEST_TEMP", null);
      hackEnvVar("my.property", null);
    } catch (Exception e) {
      throw new IllegalStateException("Couldn't hack environment variable", e);
    }
  }

  @Test
  void parseJsonConfigTest() throws BdkConfigException {
    String configPath = "/config/config.json";
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
    BdkConfigParser parser = new BdkConfigParser();
    JsonNode jsonNode = parser.parse(inputStream);
    assertEquals("tibot", jsonNode.at("/bot/username").asText());
  }

  @Test
  void parseJsonConfigWithPropertyTest() throws BdkConfigException, NoSuchFieldException, IllegalAccessException {
    System.setProperty("my.property", "propvalue");
    hackEnvVar("SYMPHONY_BDK_UNIT_TEST_TEMP", "envvalue");
    //java vars should take precedence over env vars
    hackEnvVar("my.property", "invalid_value");
    String configPath = "/config/config_properties.json";
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
    BdkConfigParser parser = new BdkConfigParser();
    JsonNode jsonNode = parser.parse(inputStream);
    assertEquals("${escaped}", jsonNode.at("/host").asText());
    assertEquals("envvalue/propvalue/privatekey.pem", jsonNode.at("/bot/privateKeyPath").asText());
    assertEquals("default/value/privatekey.pem", jsonNode.at("/app/privateKeyPath").asText());
  }

  @Test
  void parseJsonConfigWithPropertyContainingSpecialCharsTest()
      throws BdkConfigException, NoSuchFieldException, IllegalAccessException {
    System.setProperty("my.property", "propvalue:\"\n");
    hackEnvVar("SYMPHONY_BDK_UNIT_TEST_TEMP", "envvalue");
    String configPath = "/config/config_properties.json";
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
    BdkConfigParser parser = new BdkConfigParser();
    JsonNode jsonNode = parser.parse(inputStream);
    assertEquals("envvalue/propvalue:\"\n/privatekey.pem", jsonNode.at("/bot/privateKeyPath").asText());
  }

  @Test
  void parseJsonConfigWithRecursivePropertyShouldNotWorkTest() throws BdkConfigException {
    System.setProperty("recursive", "my.property");
    System.setProperty("my.property", "propvalue");
    String configPath = "/config/config_recursive_props.json";
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
    BdkConfigParser parser = new BdkConfigParser();
    JsonNode jsonNode = parser.parse(inputStream);
    assertEquals("${${recursive}}/privatekey.pem", jsonNode.at("/bot/privateKeyPath").asText());
  }

  @Test
  void parseJsonConfigWithUndefinedPropertyTest() throws BdkConfigException {
    String configPath = "/config/config_properties.json";
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
    BdkConfigParser parser = new BdkConfigParser();
    JsonNode jsonNode = parser.parse(inputStream);
    assertEquals("${SYMPHONY_BDK_UNIT_TEST_TEMP}/${my.property}/privatekey.pem",
        jsonNode.at("/bot/privateKeyPath").asText());
  }

  @Test
  void parseYamlConfigTest() throws BdkConfigException {
    String configPath = "/config/config.yaml";
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
    BdkConfigParser parser = new BdkConfigParser();
    JsonNode jsonNode = parser.parse(inputStream);
    assertEquals("tibot", jsonNode.at("/bot/username").asText());
  }

  @Test
  void parseYamlConfigWithPropertyTest() throws BdkConfigException, NoSuchFieldException, IllegalAccessException {
    System.setProperty("my.property", "propvalue");
    hackEnvVar("SYMPHONY_BDK_UNIT_TEST_TEMP", "envvalue");
    //java vars should take precedence over env vars
    hackEnvVar("my.property", "invalid_value");
    String configPath = "/config/config_properties.yaml";
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
    BdkConfigParser parser = new BdkConfigParser();
    JsonNode jsonNode = parser.parse(inputStream);
    assertEquals("envvalue/propvalue/privatekey.pem", jsonNode.at("/bot/privateKey/path").asText());
    ;
    assertEquals("envvalue/propvalue/privatekey.pem", jsonNode.at("/bot/privateKey/path").asText());
  }

  @Test
  void parseYamlConfigWithPropertiesInArrayTest()
      throws BdkConfigException, NoSuchFieldException, IllegalAccessException {
    System.setProperty("my.property", "agent-lb.acme.org");
    hackEnvVar("SYMPHONY_BDK_UNIT_TEST_TEMP", "valid_value");
    //java vars should take precedence over env vars
    hackEnvVar("my.property", "invalid_value");
    String configPath = "/config/config_lb_properties.yaml";
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
    BdkConfigParser parser = new BdkConfigParser();
    final JsonNode loadBalancing = parser.parse(inputStream).at("/agent/loadBalancing");

    assertEquals("roundRobin", loadBalancing.at("/mode").asText());
    assertTrue(loadBalancing.at("/stickiness").asBoolean());
    assertEquals("agent1.acme.org", loadBalancing.at("/nodes/0/host").asText());
    assertEquals(1234, loadBalancing.at("/nodes/0/port").asInt());
    assertEquals("valid_value/agent-lb.acme.org", loadBalancing.at("/nodes/1/host").asText());
    assertEquals("valid_value/hello", loadBalancing.at("/nodes/2/host").asText());
    ;
  }

  @Test
  void parseYamlConfigWithUndefinedPropertyTest() throws BdkConfigException {
    String configPath = "/config/config_properties.yaml";
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
    BdkConfigParser parser = new BdkConfigParser();
    JsonNode jsonNode = parser.parse(inputStream);
    assertEquals("${SYMPHONY_BDK_UNIT_TEST_TEMP}/${my.property}/privatekey.pem",
        jsonNode.at("/bot/privateKey/path").asText());
  }

  @Test
  void parseInvalidConfigTest() {
    String configPath = "/config/invalid_config.html";
    BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
      InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
      BdkConfigParser parser = new BdkConfigParser();
      parser.parse(inputStream);
    });
    assertEquals("Given InputStream is not valid. Only YAML or JSON are allowed.", exception.getMessage());
  }

  @Test
  void parseInvalidYamlConfigTest() {
    String configPath = "/config/invalid_config.yaml";
    BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
      InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
      BdkConfigParser parser = new BdkConfigParser();
      parser.parse(inputStream);
    });
    assertEquals("Given InputStream is not valid. Only YAML or JSON are allowed.", exception.getMessage());
  }
}
