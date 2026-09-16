package com.symphony.bdk.cli.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Shared Jackson serialisation for CLI output.
 *
 * <p>Command results are written to {@code stdout} as JSON. A pretty-printed form is used for
 * single-document results ({@link #pretty(Object)}); a single-line form is used for the {@code
 * datafeed read} JSON Lines stream ({@link #compact(Object)}).
 */
public final class Json {

  private static final ObjectMapper MAPPER = JsonMapper.builder()
      .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
      .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      .changeDefaultPropertyInclusion(inclusion -> inclusion.withValueInclusion(JsonInclude.Include.NON_NULL))
      .build();

  private static final ObjectWriter PRETTY = MAPPER.writerWithDefaultPrettyPrinter();
  private static final ObjectWriter COMPACT = MAPPER.writer();

  private Json() {
  }

  /** Serialise {@code value} as an indented, multi-line JSON document. */
  public static String pretty(Object value) {
    return PRETTY.writeValueAsString(value);
  }

  /** Serialise {@code value} as a single-line JSON document (one NDJSON record). */
  public static String compact(Object value) {
    return COMPACT.writeValueAsString(value);
  }
}
