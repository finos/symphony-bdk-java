package com.symphony.bdk.core.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.exceptions.BdkConfigException;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class BdkConfigLoaderTest {

    @Test
    public void loadFromYamlInputStreamTest() throws JsonProcessingException, BdkConfigException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config.yaml");
        BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream);
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    public void loadFromJsonInputStreamTest() throws JsonProcessingException, BdkConfigException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config.json");
        BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream);
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    public void loadFromYamlFileTest() throws JsonProcessingException, BdkConfigException {
        String configPath = System.getProperty("user.dir") + "/src/test/resources/config.yaml";
        BdkConfig config = BdkConfigLoader.loadFromFile(configPath);
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    public void loadFromJsonFileTest() throws JsonProcessingException, BdkConfigException {
        String configPath = System.getProperty("user.dir") + "/src/test/resources/config.json";
        BdkConfig config = BdkConfigLoader.loadFromFile(configPath);
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    public void loadFromJsonClasspathTest() throws JsonProcessingException, BdkConfigException {
        BdkConfig config = BdkConfigLoader.loadFromClasspath("/config.json");
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    public void loadFromYamlClasspathTest() throws JsonProcessingException, BdkConfigException {
        BdkConfig config = BdkConfigLoader.loadFromClasspath("/config.yaml");
        assertEquals(config.getBot().getUsername(), "tibot");
    }
}
