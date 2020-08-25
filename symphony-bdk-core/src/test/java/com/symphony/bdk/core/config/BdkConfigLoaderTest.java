package com.symphony.bdk.core.config;

import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
    public void loadFromYamlFileTest(@TempDir Path tempDir) throws BdkConfigException, IOException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config.yaml");
        Path configPath = tempDir.resolve("config.yaml");
        Files.copy(inputStream, configPath);
        BdkConfig config = BdkConfigLoader.loadFromFile(configPath.toString());
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    public void loadFromJsonFileTest(@TempDir Path tempDir) throws BdkConfigException, IOException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config.json");
        Path configPath = tempDir.resolve("config.json");
        Files.copy(inputStream, configPath);
        BdkConfig config = BdkConfigLoader.loadFromFile(configPath.toString());
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
            String configPath = "/wrong_path/config.yaml";
            BdkConfigLoader.loadFromFile(configPath);
        });
        assertEquals(exception.getMessage(), "Config file is not found");
    }

    @Test
    public void loadLegacyFromInputStreamTest() throws BdkConfigException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/legacy_config.json");
        BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream);
        assertEquals(config.getBot().getUsername(), "tibot");
        assertEquals(config.getBot().getPrivateKeyPath(), "/Users/local/conf/agent/privatekey.pem");
        assertEquals(config.getApp().getPrivateKeyPath(), "/Users/local/conf/agent/privatekey.pem");
    }

    @Test
    public void loadFromClasspathNotFoundTest() throws BdkConfigException {
        BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
            BdkConfigLoader.loadFromClasspath("/wrong_classpath/config.yaml");
        });
        assertEquals(exception.getMessage(), "Config file is not found");
    }
}
