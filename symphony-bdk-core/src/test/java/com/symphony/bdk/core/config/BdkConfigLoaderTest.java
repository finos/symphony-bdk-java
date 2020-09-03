package com.symphony.bdk.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class BdkConfigLoaderTest {

    @Test
    void loadFromYamlInputStreamTest() throws BdkConfigException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config.yaml");
        BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream);
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    void loadFromJsonInputStreamTest() throws BdkConfigException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config.json");
        BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream);
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    void loadFromYamlFileTest(@TempDir Path tempDir) throws BdkConfigException, IOException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config.yaml");
        Path configPath = tempDir.resolve("config.yaml");
        Files.copy(inputStream, configPath);
        BdkConfig config = BdkConfigLoader.loadFromFile(configPath.toString());
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    void loadFromJsonFileTest(@TempDir Path tempDir) throws BdkConfigException, IOException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/config.json");
        Path configPath = tempDir.resolve("config.json");
        Files.copy(inputStream, configPath);
        BdkConfig config = BdkConfigLoader.loadFromFile(configPath.toString());
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    void loadFromJsonClasspathTest() throws BdkConfigException {
        BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config.json");
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    void loadFromYamlClasspathTest() throws BdkConfigException {
        BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
        assertEquals(config.getBot().getUsername(), "tibot");
    }

    @Test
    void loadFromFileNotFoundTest() throws BdkConfigException {
        BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
            String configPath = "/wrong_path/config.yaml";
            BdkConfigLoader.loadFromFile(configPath);
        });
        assertEquals(exception.getMessage(), "Config file is not found");
    }

    @Test
    void loadLegacyFromInputStreamTest() throws BdkConfigException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/config/legacy_config.json");
        BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream);
        assertEquals(config.getBot().getUsername(), "tibot");
        assertEquals(config.getBot().getPrivateKeyPath(), "/Users/local/conf/agent/privatekey.pem");
        assertEquals(config.getApp().getPrivateKeyPath(), "/Users/local/conf/agent/privatekey.pem");
    }

    @Test
    void loadFromClasspathNotFoundTest() throws BdkConfigException {
        BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
            BdkConfigLoader.loadFromClasspath("/wrong_classpath/config.yaml");
        });
        assertEquals(exception.getMessage(), "Config file is not found");
    }

    @Test
    void loadClientGlobalConfig() throws BdkConfigException {
        BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config_client_global.yaml");

        assertEquals(config.getPod().getScheme(), "https");
        assertEquals(config.getPod().getHost(), "diff-pod.symphony.com");
        assertEquals(config.getPod().getPort(), 8443);
        assertEquals(config.getPod().getContext(), "context");

        assertEquals(config.getScheme(), "https");

        assertEquals(config.getAgent().getScheme(), "https");
        assertEquals(config.getAgent().getHost(), "devx1.symphony.com");
        assertEquals(config.getAgent().getPort(), 443);
        assertEquals(config.getAgent().getFormattedContext(), "/context");

        assertEquals(config.getKeyManager().getScheme(), "https");
        assertEquals(config.getKeyManager().getHost(), "devx1.symphony.com");
        assertEquals(config.getKeyManager().getPort(), 8443);
        assertEquals(config.getKeyManager().getFormattedContext(), "/diff-context");

        assertEquals(config.getSessionAuth().getScheme(), "http");
        assertEquals(config.getSessionAuth().getHost(), "devx1.symphony.com");
        assertEquals(config.getSessionAuth().getPort(), 8443);
        assertEquals(config.getSessionAuth().getContext(), "context");
    }

    //@Test
    // CircleCI does not allow to create file in the home directory
    void testLoadConfigFromSymphonyDirectory() throws Exception {

      final String tmpConfigFileName = UUID.randomUUID().toString() + "-config.yaml";
      final Path tmpConfigPath = Paths.get(System.getProperty("user.home"), ".symphony", tmpConfigFileName);
      final InputStream configInputStream = this.getClass().getResourceAsStream("/config/config.yaml");
      IOUtils.copy(configInputStream, new FileOutputStream(tmpConfigPath.toFile()));

      final BdkConfig config = BdkConfigLoader.loadFromSymphonyDir(tmpConfigFileName);
      assertNotNull(config);

      Files.delete(tmpConfigPath);
    }
}
