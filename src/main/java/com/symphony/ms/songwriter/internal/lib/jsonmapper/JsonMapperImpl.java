package com.symphony.ms.songwriter.internal.lib.jsonmapper;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonMapperImpl implements JsonMapper {
  private static final Logger LOGGER = LoggerFactory.getLogger(JsonMapperImpl.class);

  private ObjectMapper objectMapper;

  public JsonMapperImpl(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public String toJsonString(Object entity) {
    try {
      return objectMapper.writeValueAsString(entity);
    } catch (JsonProcessingException jpe) {
      LOGGER.error("Error serializing to JSON\n{}", jpe);
      throw new JsonMapperException();
    }
  }

  @Override
  public String toEnricherString(String entityName, Object entity, String version) {
    ObjectNode parent = objectMapper.createObjectNode();
    ObjectNode description = new ObjectMapper().createObjectNode();
    description.put("type", entityName);
    description.put("version", version);
    description.put("payload", toJsonString(entity));
    parent.set(entityName, description);

    return parent.toString();
  }

  @Override
  public Object fromJsonString(String jsonString) {
    return null;
  }

  @Override
  public Map<String, Object> objectToMap(Object data) {
    return objectMapper.convertValue(data, Map.class);
  }

}
