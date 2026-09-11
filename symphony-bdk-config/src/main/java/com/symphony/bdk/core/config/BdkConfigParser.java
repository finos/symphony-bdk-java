package com.symphony.bdk.core.config;

import com.symphony.bdk.core.config.exception.BdkConfigException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;
import org.apiguardian.api.API;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.StringNode;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@API(status = API.Status.INTERNAL)
class BdkConfigParser {

  private static final ObjectMapper JSON_MAPPER = JsonMapper.builder()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .build();
  private static final ObjectMapper YAML_MAPPER = YAMLMapper.builder()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .build();
  private final StringSubstitutor envVarStringSubstitutor;

  public BdkConfigParser() {
    envVarStringSubstitutor = new StringSubstitutor(StringLookupFactory.INSTANCE.environmentVariableStringLookup());
  }

  public JsonNode parse(InputStream inputStream) throws BdkConfigException {
    final JsonNode jsonNode = parseJsonNode(inputStream);
    interpolateProperties(jsonNode);
    return jsonNode;
  }

  public JsonNode parseJsonNode(InputStream inputStream) throws BdkConfigException {
    String content = "";
    try (
        InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr)) {
      content = reader.lines().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      log.error("Error: {}", e.getMessage());
    }
    try {
      return JSON_MAPPER.readTree(content);
    } catch (JacksonException e) {
      log.debug("Config file is not in JSON format.");
    }

    try {
      JsonNode jsonNode = YAML_MAPPER.readTree(content);
      if (jsonNode.isContainer()) {
        log.debug("Config file found in YAML format.");
        return jsonNode;
      }
    } catch (JacksonException e) {
      log.debug("Config file is not in YAML format.");
    }
    throw new BdkConfigException("Given InputStream is not valid. Only YAML or JSON are allowed.");
  }

  public void interpolateProperties(JsonNode jsonNode) {
    if (jsonNode.isArray()) {
      for (final JsonNode arrayItem : jsonNode) {
        interpolateProperties(arrayItem);
      }
    } else if (jsonNode.isObject()) {
      interpolatePropertiesInObject((ObjectNode) jsonNode);
    }
  }

  private void interpolatePropertiesInObject(ObjectNode objectNode) {
    for (final String field : objectNode.propertyNames()) {
      interpolatePropertyInField(objectNode, field);
    }
  }

  private void interpolatePropertyInField(ObjectNode objectNode, String field) {
    final JsonNode node = objectNode.get(field);
    if (node.isTextual()) {
      //Start by replacing any java system properties found, then match any remaining keys with environment variables
      final String interpolatedFieldValue =
          envVarStringSubstitutor.replace(StringSubstitutor.replaceSystemProperties(node.asText()));
      objectNode.set(field, new StringNode(interpolatedFieldValue));
    } else if (node.isObject() || node.isArray()) {
      interpolateProperties(node);
    }
  }
}
