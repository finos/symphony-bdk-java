package com.symphony.bdk.core.api.invoker.jersey2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.openapitools.jackson.nullable.JsonNullableModule;

import java.text.DateFormat;

import javax.ws.rs.ext.ContextResolver;


public class JSON implements ContextResolver<ObjectMapper> {

  private final ObjectMapper mapper;

  public JSON() {
    this.mapper = new ObjectMapper();
    this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    this.mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    this.mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    this.mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    this.mapper.setDateFormat(new RFC3339DateFormat());
    this.mapper.registerModule(new JavaTimeModule());
    this.mapper.registerModule(new JsonNullableModule());
  }

  /**
   * Set the date format for JSON (de)serialization with Date properties.
   * @param dateFormat Date format
   */
  public void setDateFormat(DateFormat dateFormat) {
    mapper.setDateFormat(dateFormat);
  }

  @Override
  public ObjectMapper getContext(Class<?> type) {
    return this.mapper;
  }
}
