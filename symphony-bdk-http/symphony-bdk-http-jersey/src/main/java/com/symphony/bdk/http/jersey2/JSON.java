package com.symphony.bdk.http.jersey2;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.ws.rs.ext.ContextResolver;
import org.apiguardian.api.API;
import org.openapitools.jackson.nullable.JsonNullableJackson3Module;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;

import java.text.DateFormat;


@API(status = API.Status.INTERNAL)
public class JSON implements ContextResolver<ObjectMapper> {

  private ObjectMapper mapper;

  public JSON() {
    this.mapper = buildMapper(new RFC3339DateFormat());
  }

  /**
   * Set the date format for JSON (de)serialization with Date properties.
   * @param dateFormat Date format
   */
  public void setDateFormat(DateFormat dateFormat) {
    this.mapper = buildMapper(dateFormat);
  }

  private static ObjectMapper buildMapper(DateFormat dateFormat) {
    return JsonMapper.builder()
        .changeDefaultPropertyInclusion(inclusion -> inclusion.withValueInclusion(JsonInclude.Include.NON_NULL))
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
        .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
        .enable(EnumFeature.READ_ENUMS_USING_TO_STRING)
        .defaultDateFormat(dateFormat)
        .addModule(new JsonNullableJackson3Module())
        .build();
  }

  @Override
  public ObjectMapper getContext(Class<?> type) {
    return this.mapper;
  }
}
