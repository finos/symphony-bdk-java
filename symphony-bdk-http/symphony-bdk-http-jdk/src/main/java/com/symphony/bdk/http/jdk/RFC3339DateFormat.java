package com.symphony.bdk.http.jdk;

import org.apiguardian.api.API;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Formats dates as RFC 3339 (ISO 8601 with a fixed UTC offset), always including milliseconds.
 *
 * <p>Duplicated from {@code com.symphony.bdk.http.jersey2.RFC3339DateFormat} rather than shared, per design
 * decision D5 in the {@code jdk-httpclient-transport} OpenSpec change: two implementations already existed
 * without a shared abstraction, and this is deliberately kept that way until a fourth implementation appears.</p>
 */
@API(status = API.Status.INTERNAL)
public class RFC3339DateFormat extends DateFormat {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  public RFC3339DateFormat() {
    final Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.ROOT);
    calendar.setLenient(false);
    this.calendar = calendar;
    final NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.ROOT);
    numberFormat.setGroupingUsed(false);
    this.numberFormat = numberFormat;
  }

  @Override
  public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
    toAppendTo.append(FORMATTER.format(date.toInstant().atOffset(ZoneOffset.UTC)));
    return toAppendTo;
  }

  @Override
  public Date parse(String source, ParsePosition pos) {
    try {
      final OffsetDateTime parsed = OffsetDateTime.parse(source, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
      pos.setIndex(source.length());
      return Date.from(parsed.toInstant());
    } catch (java.time.format.DateTimeParseException e) {
      pos.setErrorIndex(pos.getIndex());
      return null;
    }
  }
}
