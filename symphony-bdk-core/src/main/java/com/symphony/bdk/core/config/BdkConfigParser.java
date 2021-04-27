package com.symphony.bdk.core.config;

import com.symphony.bdk.core.config.exception.BdkConfigException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;
import org.apiguardian.api.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.stream.Collectors;

@Slf4j
@API(status = API.Status.INTERNAL)
class BdkConfigParser {

  private final ObjectMapper JSON_MAPPER = new JsonMapper();
  private final ObjectMapper YAML_MAPPER = new YAMLMapper();
  private final StringSubstitutor envVarStringSubstitutor;

  public BdkConfigParser() {
    envVarStringSubstitutor = new StringSubstitutor(StringLookupFactory.INSTANCE.environmentVariableStringLookup());
    JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    YAML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public JsonNode parse(InputStream inputStream) throws BdkConfigException {
    final JsonNode jsonNode = parseJsonNode(inputStream);
    interpolateProperties(jsonNode);

    return jsonNode;
  }

  public JsonNode parseJsonNode(InputStream inputStream) throws BdkConfigException {
    String content = new BufferedReader(
        new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
    try {
      return JSON_MAPPER.readTree(content);
    } catch (IOException e) {
      log.debug("Config file is not in JSON format, checking for YAML format.");
    }

    try {
      JsonNode jsonNode = YAML_MAPPER.readTree(content);
      if (jsonNode.isContainerNode()) {
        log.debug("Config file found in YAML format.");
        return jsonNode;
      }
    } catch (IOException e) {
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
    final Iterator<String> fieldNames = objectNode.fieldNames();
    while (fieldNames.hasNext()) {
      interpolatePropertyInField(objectNode, fieldNames.next());
    }
  }

  private void interpolatePropertyInField(ObjectNode objectNode, String field) {
    final JsonNode node = objectNode.get(field);
    if (node.isTextual()) {
      //Start by replacing any java system properties found, then match any remaining keys with environment variables
      final String interpolatedFieldValue =
          envVarStringSubstitutor.replace(StringSubstitutor.replaceSystemProperties(node.asText()));
      objectNode.set(field, new TextNode(interpolatedFieldValue));
    } else if (node.isObject() || node.isArray()) {
      interpolateProperties(node);
    }
  }
}
