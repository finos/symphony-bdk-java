package com.symphony.bdk.core.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class BdkConfigParserTest {

    @Test
    public void parseJsonConfigTest() throws BdkConfigException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config.json");
        JsonNode jsonNode = BdkConfigParser.parse(inputStream);
        assertEquals(jsonNode.at("/bot").at("/username").asText(), "tibot");
    }

    @Test
    public void parseYamlConfigTest() throws BdkConfigException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config.yaml");
        JsonNode jsonNode = BdkConfigParser.parse(inputStream);
        assertEquals(jsonNode.at("/bot").at("/username").asText(), "tibot");
    }

    @Test
    public void parseInvalidConfigTest() {
        BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
            InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/invalid_config.html");
            BdkConfigParser.parse(inputStream);
        });
        assertEquals(exception.getMessage(), "Config file is not in a valid format");
    }

    @Test
    public void parseInvalidYamlConfigTest() {
        BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
            InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/invalid_config.yaml");
            BdkConfigParser.parse(inputStream);
        });
        assertEquals(exception.getMessage(), "Config file is not in a valid format");
    }
}
