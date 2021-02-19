package com.symphony.bdk.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.config.exception.BdkConfigException;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

public class BdkConfigParserTest {

    @BeforeEach
    void tearDown() {
        System.clearProperty("my.property");
        System.clearProperty("recursive");
    }

    @Test
    void parseJsonConfigTest() throws BdkConfigException {
        String configPath = "/config/config.json";
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
        JsonNode jsonNode = BdkConfigParser.parse(inputStream);
        assertEquals("tibot", jsonNode.at("/bot/username").asText());
    }

    @Test
    void parseJsonConfigWithPropertyTest() throws BdkConfigException {
        System.setProperty("my.property", "propvalue");
        String configPath = "/config/config_properties.json";
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
        JsonNode jsonNode = BdkConfigParser.parse(inputStream);

        assertEquals("${escaped}", jsonNode.at("/host").asText());
        assertEquals("propvalue/privatekey.pem", jsonNode.at("/bot/privateKeyPath").asText());
        assertEquals("default/value/privatekey.pem", jsonNode.at("/app/privateKeyPath").asText());
    }

    @Test
    void parseJsonConfigWithPropertyContainingSpecialCharsTest() throws BdkConfigException {
        System.setProperty("my.property", "propvalue:\"\n");
        String configPath = "/config/config_properties.json";
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
        JsonNode jsonNode = BdkConfigParser.parse(inputStream);
        assertEquals("propvalue:\"\n/privatekey.pem", jsonNode.at("/bot/privateKeyPath").asText());
    }

    @Test
    void parseJsonConfigWithRecursivePropertyShouldNotWorkTest() throws BdkConfigException {
        System.setProperty("recursive", "my.property");
        System.setProperty("my.property", "propvalue");
        String configPath = "/config/config_recursive_props.json";
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
        JsonNode jsonNode = BdkConfigParser.parse(inputStream);
        assertEquals("${${recursive}}/privatekey.pem", jsonNode.at("/bot/privateKeyPath").asText());
    }

    @Test
    void parseJsonConfigWithUndefinedPropertyTest() throws BdkConfigException {
        String configPath = "/config/config_properties.json";
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
        JsonNode jsonNode = BdkConfigParser.parse(inputStream);
        assertEquals("${my.property}/privatekey.pem", jsonNode.at("/bot/privateKeyPath").asText());
    }

    @Test
    void parseYamlConfigTest() throws BdkConfigException {
        String configPath = "/config/config.yaml";
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
        JsonNode jsonNode = BdkConfigParser.parse(inputStream);
        assertEquals("tibot", jsonNode.at("/bot/username").asText());
    }

    @Test
    void parseYamlConfigWithPropertyTest() throws BdkConfigException {
        System.setProperty("my.property", "propvalue");
        String configPath = "/config/config_properties.yaml";
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
        JsonNode jsonNode = BdkConfigParser.parse(inputStream);
        assertEquals("propvalue/privatekey.pem", jsonNode.at("/bot/privateKey/path").asText());
    }

    @Test
    void parseYamlConfigWithPropertiesInArray() throws BdkConfigException {
        System.setProperty("my.property", "agent-lb.acme.org");
        String configPath = "/config/config_lb_properties.yaml";
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
        final JsonNode loadBalancing = BdkConfigParser.parse(inputStream).at("/agent/loadBalancing");

        assertEquals("roundRobin", loadBalancing.at("/mode").asText());
        assertTrue(loadBalancing.at("/stickiness").asBoolean());

        assertEquals("agent1.acme.org", loadBalancing.at("/nodes/0/host").asText());
        assertEquals(1234, loadBalancing.at("/nodes/0/port").asInt());

        assertEquals("agent-lb.acme.org", loadBalancing.at("/nodes/1/host").asText());
    }

    @Test
    void parseInvalidConfigTest() {
        String configPath = "/config/invalid_config.html";
        BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
            InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
            BdkConfigParser.parse(inputStream);
        });
        assertEquals("Given InputStream is not valid. Only YAML or JSON are allowed.", exception.getMessage());
    }

    @Test
    void parseInvalidYamlConfigTest() {
        String configPath = "/config/invalid_config.yaml";
        BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
            InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
            BdkConfigParser.parse(inputStream);
        });
        assertEquals("Given InputStream is not valid. Only YAML or JSON are allowed.", exception.getMessage());
    }
}
