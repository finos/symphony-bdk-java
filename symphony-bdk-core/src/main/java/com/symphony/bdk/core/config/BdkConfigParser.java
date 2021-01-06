package com.symphony.bdk.core.config;

import com.symphony.bdk.core.config.exception.BdkConfigException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.apiguardian.api.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@API(status = API.Status.INTERNAL)
class BdkConfigParser {

    private static final ObjectMapper JSON_MAPPER = new JsonMapper();
    private static final ObjectMapper YAML_MAPPER = new YAMLMapper();

    public static JsonNode parse(InputStream inputStream) throws BdkConfigException {
        JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        YAML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      String content = StringSubstitutor.replaceSystemProperties(new BufferedReader(
          new InputStreamReader(inputStream, StandardCharsets.UTF_8))
          .lines()
          .collect(Collectors.joining("\n")));
        try {
            return JSON_MAPPER.readTree(content);
        } catch (IOException e) {
            log.debug("Config file is not in JSON format");
        }

        try {
            JsonNode jsonNode =  YAML_MAPPER.readTree(content);
            if (jsonNode.isContainerNode()) return jsonNode;
            log.debug("Config file is not in YAML format");
        } catch (IOException e) {
            log.debug("Config file is not in YAML format");
        }
        throw new BdkConfigException("Config file is not in a valid format");
    }
}
