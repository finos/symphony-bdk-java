package com.symphony.bdk.http.jdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.text.ParsePosition;
import java.time.Instant;
import java.util.Date;

class JSONTest {

  static class DatedModel {
    public Date date;
  }

  static class SimpleModel {
    public String name;
  }

  @Test
  void dateFieldSerializesAsRfc3339String() {
    ObjectMapper mapper = new JSON().getMapper();
    DatedModel model = new DatedModel();
    model.date = Date.from(Instant.parse("2024-01-15T10:30:00.123Z"));

    String json = mapper.writeValueAsString(model);

    assertEquals("{\"date\":\"2024-01-15T10:30:00.123Z\"}", json);
  }

  @Test
  void dateFieldRoundTrips() {
    ObjectMapper mapper = new JSON().getMapper();
    DatedModel model = new DatedModel();
    model.date = Date.from(Instant.parse("2024-01-15T10:30:00.123Z"));

    String json = mapper.writeValueAsString(model);
    DatedModel parsed = mapper.readValue(json, DatedModel.class);

    assertEquals(model.date, parsed.date);
  }

  @Test
  void unknownPropertyIsIgnoredOnDeserialization() {
    ObjectMapper mapper = new JSON().getMapper();
    String json = "{\"name\":\"foo\",\"unknownField\":\"bar\"}";

    SimpleModel parsed = assertDoesNotThrow(() -> mapper.readValue(json, SimpleModel.class));

    assertEquals("foo", parsed.name);
  }

  @Test
  void setDateFormatOverridesTheMapperConfiguration() {
    JSON json = new JSON();
    json.setDateFormat(new RFC3339DateFormat());

    DatedModel model = new DatedModel();
    model.date = Date.from(Instant.parse("2024-01-15T10:30:00.123Z"));

    String output = json.getMapper().writeValueAsString(model);

    assertEquals("{\"date\":\"2024-01-15T10:30:00.123Z\"}", output);
  }

  @Test
  void rfc3339DateFormatParseReturnsNull_forInvalidInput() {
    RFC3339DateFormat format = new RFC3339DateFormat();
    ParsePosition pos = new ParsePosition(0);

    Date result = format.parse("not-a-date", pos);

    assertNull(result);
    assertEquals(0, pos.getErrorIndex());
  }

  @Test
  void nullFieldsAreExcludedFromSerializedOutput() {
    ObjectMapper mapper = new JSON().getMapper();
    SimpleModel model = new SimpleModel();
    model.name = null;

    String json = mapper.writeValueAsString(model);

    assertEquals("{}", json);
    assertNull(model.name);
  }
}
