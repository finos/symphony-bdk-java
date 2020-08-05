package com.symphony.bdk.core.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.symphony.bdk.core.config.legacy.model.LegacySymConfig;
import com.symphony.bdk.core.config.model.BdkConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class BdkConfigLoader {

    private static final JsonMapper JSON_MAPPER = new JsonMapper();
    private static final YAMLMapper YAML_MAPPER = new YAMLMapper();

    /**
     * Load BdkConfig from a file path
     *
     * @param configPath Path of the config file
     *
     * @return Symphony Bot Configuration
     */
    public static BdkConfig loadFromFile(String configPath) {
        try {
            return JSON_MAPPER.readValue(new File(configPath), BdkConfig.class);
        } catch (IOException e) {
            log.debug("Config is not in JSON format");
        }

        try {
            return YAML_MAPPER.readValue(new File(configPath), BdkConfig.class);
        } catch (IOException e) {
            log.debug("Config is not in Yaml format");
        }

        log.error("Config is not in a valid format");
        return null;
    }

    /**
     * Load BdkConfig from an InputStream
     *
     * @param inputStream InputStream
     *
     * @return Symphony Bot Configuration
     */
    public static BdkConfig loadFromInputStream(InputStream inputStream) {
        try {
            return JSON_MAPPER.readValue(inputStream, BdkConfig.class);
        } catch (IOException e) {
            log.debug("Config is not in JSON format");
        }

        try {
            return YAML_MAPPER.readValue(inputStream, BdkConfig.class);
        } catch (IOException e) {
            log.debug("Config is not in Yaml format");
        }

        log.error("Config is not in a valid format");
        return null;
    }

    /**
     * Load BdkConfig from a classpath
     *
     * @param configPath Classpath to config file
     *
     * @return Symphony Bot Configuration
     */
    public static BdkConfig loadFromClasspath(String configPath) {
        if (!configPath.startsWith(File.separator)) {
            configPath = File.separator + configPath;
        }
        String externalUrlPath = System.getProperty("user.dir") + configPath;

        BdkConfig config;
        if ((new File(externalUrlPath)).exists()) {
            config = loadFromFile(externalUrlPath);
        } else {
            InputStream is = BdkConfigLoader.class.getResourceAsStream(configPath);
            if (is == null) {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(configPath);
            }
            config = loadFromInputStream(is);
        }

        return config;
    }

    /**
     * Load LegacySymConfig from a config file
     *
     * @param configPath Path to config file
     *
     * @return Symphony Bot Legacy Configuration
     */
    public static LegacySymConfig loadLegacyConfigFromFile(String configPath) {
        try {
            return JSON_MAPPER.readValue(new File(configPath), LegacySymConfig.class);
        } catch (IOException e) {
            log.debug("Config is not in JSON format");
        }

        log.error("Config is not in a valid format");
        return null;
    }

    /**
     * Load LegacySymConfig from an input stream
     *
     * @param inputStream InputStream
     *
     * @return Symphony Bot Legacy Configuration
     */
    public static LegacySymConfig loadLegacyConfigFromInputStream(InputStream inputStream) {
        try {
            return JSON_MAPPER.readValue(inputStream, LegacySymConfig.class);
        } catch (IOException e) {
            log.debug("Config is not in JSON format");
        }

        log.error("Config is not in a valid format");
        return null;
    }

    /**
     * Load LegacySymConfig from a classpath
     *
     * @param configPath Classpath to config file
     *
     * @return Symphony Bot Legacy Configuration
     */
    public static LegacySymConfig loadLegacyConfigFromClasspath(String configPath) {
        if (!configPath.startsWith(File.separator)) {
            configPath = File.separator + configPath;
        }
        String externalUrlPath = System.getProperty("user.dir") + configPath;

        LegacySymConfig config;
        if ((new File(externalUrlPath)).exists()) {
            config = loadLegacyConfigFromFile(externalUrlPath);
        } else {
            InputStream is = BdkConfigLoader.class.getResourceAsStream(configPath);
            if (is == null) {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(configPath);
            }
            config = loadLegacyConfigFromInputStream(is);
        }

        return config;
    }
}
