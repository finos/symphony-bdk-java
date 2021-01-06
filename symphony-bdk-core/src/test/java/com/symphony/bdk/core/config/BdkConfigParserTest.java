package com.symphony.bdk.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.core.config.exception.BdkConfigException;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

public class BdkConfigParserTest {

    @Test
    public void parseJsonConfigTest() throws BdkConfigException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config.json");
        JsonNode jsonNode = BdkConfigParser.parse(inputStream);
        assertEquals("tibot", jsonNode.at("/bot").at("/username").asText());
    }

    @Test
    public void parseJsonConfigWithPropertyTest() throws BdkConfigException {
        System.setProperty("my.property", "propvalue");

        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config_properties.json");
        JsonNode jsonNode = BdkConfigParser.parse(inputStream);
        assertEquals("propvalue/privatekey.pem", jsonNode.at("/bot").at("/privateKeyPath").asText());
    }

    @Test
    public void parseYamlConfigTest() throws BdkConfigException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config.yaml");
        JsonNode jsonNode = BdkConfigParser.parse(inputStream);
        assertEquals("tibot", jsonNode.at("/bot").at("/username").asText());
    }

    @Test
    public void parseYamlConfigWithPropertyTest() throws BdkConfigException {
        System.setProperty("my.property", "propvalue");

        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config_properties.yaml");
        JsonNode jsonNode = BdkConfigParser.parse(inputStream);
        assertEquals("propvalue/privatekey.pem", jsonNode.at("/bot").at("/privateKey").at("/path").asText());
    }

    @Test
    public void parseInvalidConfigTest() {
        BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
            InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/invalid_config.html");
            BdkConfigParser.parse(inputStream);
        });
        assertEquals("Config file is not in a valid format", exception.getMessage());
    }

    @Test
    public void parseInvalidYamlConfigTest() {
        BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
            InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/invalid_config.yaml");
            BdkConfigParser.parse(inputStream);
        });
        assertEquals("Config file is not in a valid format", exception.getMessage());
    }
}
