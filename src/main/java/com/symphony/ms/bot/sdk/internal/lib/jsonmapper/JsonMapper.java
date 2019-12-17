package com.symphony.ms.bot.sdk.internal.lib.jsonmapper;

import java.util.Map;

/**
 * Interface which abstracts the underlying JSON library
 *
 * @author Marcus Secato
 *
 */
public interface JsonMapper {

  /**
   * Generates a JSON string representation of the data used to enrich messages
   * in extension applications.
   *
   * @param entityName
   * @param entity
   * @param version
   * @return the json string
   */
  String toEnricherString(String entityName, Object entity, String version);

  /**
   * Serializes a Java object to JSON string representation
   *
   * @param entity
   * @return json string
   */
  String toJsonString(Object entity);

  /**
   * Converts Java object to Map representation
   *
   * @param data
   * @return map representation
   */
  Map<String, Object> objectToMap(Object data);

  /**
   * Converts a JSON string to a Java object of the specified class
   *
   * @param jsonString
   * @param clazz
   * @return the Java object
   */
  <T> T toObject(String jsonString, Class<T> clazz);
}
