package com.symphony.bdk.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.symphony.bdk.core.exceptions.BdkConfigException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
class BdkConfigParser {

    private static final ObjectMapper JSON_MAPPER = new JsonMapper();
    private static final ObjectMapper YAML_MAPPER = new YAMLMapper();

    public static JsonNode parse(InputStream inputStream) throws BdkConfigException {
        JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        YAML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return JSON_MAPPER.readTree(inputStream);
        } catch (IOException e) {
            log.debug("Config file is not in JSON format");
        }

        try {
            return YAML_MAPPER.readTree(inputStream);
        } catch (IOException e) {
            log.debug("Config file is not in YAML format");
        }
        throw new BdkConfigException("Config file is not in a valid format");
    }
}
