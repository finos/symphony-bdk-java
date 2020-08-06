package com.symphony.bdk.core.config;

import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.exceptions.BdkConfigException;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BdkConfigLoaderTest {

    @Test
    public void loadFromYamlInputStreamTest() throws BdkConfigException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config.yaml");
        BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream);
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    public void loadFromJsonInputStreamTest() throws BdkConfigException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config.json");
        BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream);
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    public void loadFromYamlFileTest() throws BdkConfigException {
        String configPath = System.getProperty("user.dir") + "/src/test/resources/config/config.yaml";
        BdkConfig config = BdkConfigLoader.loadFromFile(configPath);
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    public void loadFromJsonFileTest() throws BdkConfigException {
        String configPath = System.getProperty("user.dir") + "/src/test/resources/config/config.json";
        BdkConfig config = BdkConfigLoader.loadFromFile(configPath);
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    public void loadFromJsonClasspathTest() throws BdkConfigException {
        BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config.json");
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    public void loadFromYamlClasspathTest() throws BdkConfigException {
        BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    public void loadFromFileNotFoundTest() throws BdkConfigException {
        BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
            String configPath = System.getProperty("user.dir") + "/wrong_path/config.yaml";
            BdkConfigLoader.loadFromFile(configPath);
        });
        assertEquals(exception.getMessage(), "Config file is not found");
    }

    @Test
    public void loadLegacyFromInputStreamTest() throws BdkConfigException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/legacy_config.json");
        BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream);
        assertEquals(config.getBot().getUsername(), "tibot");
        assertEquals(config.getBot().getPrivateKeyPath(), "/Users/thibault.pensec/local/conf/agent/privatekey.pem");
        assertEquals(config.getApp().getPrivateKeyPath(), "/Users/thibault.pensec/local/conf/agent/privatekey.pem");
    }

    @Test
    public void loadFromClasspathNotFoundTest() throws BdkConfigException {
        BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
            BdkConfigLoader.loadFromClasspath("/wrong_classpath/config.yaml");
        });
        assertEquals(exception.getMessage(), "Config file is not found");
    }
}
