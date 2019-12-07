package com.symphony.ms.songwriter.internal.lib.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Jackson-based implementation of the {@link JsonMapper}
 *
 * @author Marcus Secato
 *
 */
public class JsonMapperImpl implements JsonMapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonMapperImpl.class);

  private ObjectMapper objectMapper;

  public JsonMapperImpl(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toJsonString(Object entity) {
    try {
      return objectMapper.writeValueAsString(entity);
    } catch (JsonProcessingException jpe) {
      LOGGER.error("Error serializing to JSON\n{}", jpe);
      throw new JsonMapperException();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toEnricherString(String entityName, Object entity,
      String version) {
    ObjectNode parent = objectMapper.createObjectNode();
    ObjectNode description = new ObjectMapper().createObjectNode();
    description.put("type", entityName);
    description.put("version", version);
    description.put("payload", toJsonString(entity));
    parent.set(entityName, description);

    return parent.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Object> objectToMap(Object data) {
    return objectMapper.convertValue(data, Map.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T toObject(String jsonString, Class<T> clazz) {
    try {
      return objectMapper.readValue(jsonString, clazz);
    } catch (IOException e) {
      throw new JsonMapperException();
    }
  }
}
