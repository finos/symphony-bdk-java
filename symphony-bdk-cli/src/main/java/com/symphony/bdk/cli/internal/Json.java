package com.symphony.bdk.cli.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.UncheckedIOException;

/**
 * Shared Jackson serialisation for CLI output.
 *
 * <p>Command results are written to {@code stdout} as JSON. A pretty-printed form is used for
 * single-document results ({@link #pretty(Object)}); a single-line form is used for the {@code
 * datafeed read} JSON Lines stream ({@link #compact(Object)}).
 */
public final class Json {

  private static final ObjectMapper MAPPER = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  private static final ObjectWriter PRETTY = MAPPER.writerWithDefaultPrettyPrinter();
  private static final ObjectWriter COMPACT = MAPPER.writer();

  private Json() {
  }

  /** Serialise {@code value} as an indented, multi-line JSON document. */
  public static String pretty(Object value) {
    return write(PRETTY, value);
  }

  /** Serialise {@code value} as a single-line JSON document (one NDJSON record). */
  public static String compact(Object value) {
    return write(COMPACT, value);
  }

  private static String write(ObjectWriter writer, Object value) {
    try {
      return writer.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }
}
