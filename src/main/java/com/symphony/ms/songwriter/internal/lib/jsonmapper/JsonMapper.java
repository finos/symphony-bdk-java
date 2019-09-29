package com.symphony.ms.songwriter.internal.lib.jsonmapper;

import java.util.Map;

public interface JsonMapper {

  String toEnricherString(String entityName, Object entity, String version);

  String toJsonString(Object entity);

  Object fromJsonString(String jsonString);

  Map<String, Object> objectToMap(Object data);

}
