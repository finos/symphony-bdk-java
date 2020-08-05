package com.symphony.bdk.core.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
class BdkConfigParser {

    private static final ObjectMapper JSON_MAPPER = new JsonMapper();
    private static final ObjectMapper YAML_MAPPER = new YAMLMapper();

    public static JsonNode parse(InputStream inputStream) {
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
        log.error("Config file is not in a valid format");
        return null;
    }
}
