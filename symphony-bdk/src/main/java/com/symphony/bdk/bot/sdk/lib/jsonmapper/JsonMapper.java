package com.symphony.bdk.bot.sdk.lib.jsonmapper;

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
   * @param entityName the entity name
   * @param entity the data
   * @param version the entity version
   * @return the json string
   */
  String toEnricherString(String entityName, Object entity, String version);

  /**
   * Serializes a Java object to JSON string representation
   *
   * @param entity the data
   * @return json string
   */
  String toJsonString(Object entity);

  /**
   * Converts Java object to Map representation
   *
   * @param data the data to be converted
   * @return map representation
   */
  Map<String, Object> objectToMap(Object data);

  /**
   * Converts a JSON string to a Java object of the specified class
   *
   * @param <T> the object type
   * @param jsonString the JSON string 
   * @param clazz the class to convert to
   * @return the Java object
   */
  <T> T toObject(String jsonString, Class<T> clazz);
}
