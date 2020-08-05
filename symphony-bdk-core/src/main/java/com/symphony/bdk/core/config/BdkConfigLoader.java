package com.symphony.bdk.core.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.symphony.bdk.core.config.legacy.model.LegacySymConfig;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.exceptions.BdkConfigException;
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
    public static BdkConfig loadFromFile(String configPath) throws JsonProcessingException, BdkConfigException {
        try {
            File file = new File(configPath);
            InputStream inputStream = new FileInputStream(file);
            return loadFromInputStream(inputStream);
        } catch (FileNotFoundException e) {
            throw new BdkConfigException("Config file is not found");
        }
    }

    /**
     * Load BdkConfig from an InputStream
     *
     * @param inputStream InputStream
     *
     * @return Symphony Bot Configuration
     */
    public static BdkConfig loadFromInputStream(InputStream inputStream) throws JsonProcessingException, BdkConfigException {
        if (inputStream != null) {
            JsonNode jsonNode = BdkConfigParser.parse(inputStream);
            if (jsonNode != null) {
                JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                if (jsonNode.at("botUsername") != null) {
                    LegacySymConfig legacySymConfig = JSON_MAPPER.treeToValue(jsonNode, LegacySymConfig.class);
                    return LegacyConfigMapper.map(legacySymConfig);
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
    public static BdkConfig loadFromClasspath(String configPath) throws JsonProcessingException, BdkConfigException {
        InputStream inputStream = BdkConfigLoader.class.getResourceAsStream(configPath);
        if (inputStream != null) {
            return loadFromInputStream(inputStream);
        }
        throw new BdkConfigException("Config file is not found");
    }
}
