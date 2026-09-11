package com.symphony.bdk.http.jdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apiguardian.api.API;
import org.openapitools.jackson.nullable.JsonNullableJackson3Module;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;

import java.text.DateFormat;

/**
 * Configures the {@link ObjectMapper} used by {@link ApiClientJdk} for request/response (de)serialization.
 *
 * <p>Duplicated from {@code com.symphony.bdk.http.jersey2.JSON} rather than shared, per design decision D5 in
 * the {@code jdk-httpclient-transport} OpenSpec change.</p>
 */
@API(status = API.Status.INTERNAL)
public class JSON {

  private ObjectMapper mapper;

  public JSON() {
    this.mapper = buildMapper(new RFC3339DateFormat());
  }

  /**
   * Set the date format for JSON (de)serialization with Date properties.
   *
   * @param dateFormat Date format
   */
  public void setDateFormat(DateFormat dateFormat) {
    this.mapper = buildMapper(dateFormat);
  }

  /**
   * @return the configured {@link ObjectMapper} instance.
   */
  public ObjectMapper getMapper() {
    return this.mapper;
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
}
