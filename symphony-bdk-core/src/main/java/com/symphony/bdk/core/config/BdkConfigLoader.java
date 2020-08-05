package com.symphony.bdk.core.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.symphony.bdk.core.config.legacy.model.LegacySymConfig;
import com.symphony.bdk.core.config.model.BdkConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Slf4j
public class BdkConfigLoader {

    private static final ObjectMapper JSON_MAPPER = new JsonMapper();

    /**
     * Load BdkConfig from a file path
     *
     * @param configPath Path of the config file
     *
     * @return Symphony Bot Configuration
     */
    public static BdkConfig loadFromFile(String configPath) throws JsonProcessingException {
        try {
            File file = new File(configPath);
            InputStream inputStream = new FileInputStream(file);
            return loadFromInputStream(inputStream);
        } catch (FileNotFoundException e) {
            log.error("Config file is not found.");
        }
        return null;
    }

    /**
     * Load BdkConfig from an InputStream
     *
     * @param inputStream InputStream
     *
     * @return Symphony Bot Configuration
     */
    public static BdkConfig loadFromInputStream(InputStream inputStream) throws JsonProcessingException {
        if (inputStream != null) {
            JsonNode jsonNode = BdkConfigParser.parse(inputStream);
            if (jsonNode != null) {
                if (jsonNode.at("botUsername") != null) {
                    LegacySymConfig legacySymConfig = JSON_MAPPER.treeToValue(jsonNode, LegacySymConfig.class);
                    return BdkConfig.fromLegacyConfig(legacySymConfig);
                } else {
                    return JSON_MAPPER.treeToValue(jsonNode, BdkConfig.class);
                }
            }
        }
        return null;
    }

    /**
     * Load BdkConfig from a classpath
     *
     * @param configPath Classpath to config file
     *
     * @return Symphony Bot Configuration
     */
    public static BdkConfig loadFromClasspath(String configPath) throws JsonProcessingException {
        InputStream inputStream = BdkConfigLoader.class.getResourceAsStream(configPath);
        if (inputStream != null) {
            return loadFromInputStream(inputStream);
        }
        log.error("Config file is not found");
        return null;
    }
}
